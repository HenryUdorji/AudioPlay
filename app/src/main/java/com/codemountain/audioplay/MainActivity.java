package com.codemountain.audioplay;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codemountain.audioplay.activities.MusicPlayerActivity;
import com.codemountain.audioplay.adapters.ViewPagerAdapter;
import com.codemountain.audioplay.fragments.AlbumFragment;
import com.codemountain.audioplay.fragments.ArtistFragment;
import com.codemountain.audioplay.fragments.PlaylistFragment;
import com.codemountain.audioplay.fragments.SongsFragment;
import com.codemountain.audioplay.interfaces.MetaDatas;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.service.AudioPlayService;
import com.codemountain.audioplay.service.MediaPlayerSingleton;
import com.codemountain.audioplay.utils.ArtWork;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;
import com.codemountain.audioplay.utils.NavigationViewHelper;
import com.codemountain.audioplay.utils.permissionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.M;
import static com.codemountain.audioplay.utils.Constants.ITEM_ADDED;
import static com.codemountain.audioplay.utils.Constants.META_CHANGED;
import static com.codemountain.audioplay.utils.Constants.NAV;
import static com.codemountain.audioplay.utils.Constants.ORDER_CHANGED;
import static com.codemountain.audioplay.utils.Constants.PERMISSIONS_REQ;
import static com.codemountain.audioplay.utils.Constants.PLAYSTATE_CHANGED;
import static com.codemountain.audioplay.utils.Constants.POSITION_CHANGED;
import static com.codemountain.audioplay.utils.Constants.SETTINGS_TRACK;
import static com.codemountain.audioplay.utils.Constants.WRITE_SETTINGS;

public class MainActivity extends AppCompatActivity implements MetaDatas {

    private static final int REQUEST_CODE = 1;
    private static MainActivity mainActivity;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private CircleImageView musicImage;
    private ImageButton musicPlayPauseToggle;
    private TextView musicNameText, musicArtistText;
    private ProgressBar musicProgressBar;
    private ConstraintLayout musicConstraint;
    private Drawable pause, play;
    private ActionBarDrawerToggle mToggle;
    private Intent intent;
    private Helper helper;
    private static Handler songProgressHandler;
    private ProgressRunnable progressRunnable;
    private RequestManager mRequestManager;
    private boolean mService = false;
    private AudioPlayService audioPlayService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayService.AudioPlayBinder binder = (AudioPlayService.AudioPlayBinder) service;
            audioPlayService = binder.getService();
            mService = true;
            if (audioPlayService != null) {
                playingView();
            }
            Intent filterIntent = getIntent();
            if (filterIntent != null) {
                Uri data = filterIntent.getData();
                if (data != null) {
                    try {
                        openFile(data);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = false;
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (audioPlayService == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(PLAYSTATE_CHANGED)) {
                playPauseToggle();

            }
            else if (action.equals(META_CHANGED)) {
                miniPlayerView();
                finalProgress();
            }
        }
    };

    //OnClick listener
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (audioPlayService == null) {
                return;
            }
            switch (view.getId()) {
                case R.id.musicPlayPauseToggle:
                    audioPlayService.toggle();
                    break;

                case R.id.musicConstraint:
                    Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                    startActivityForResult(intent, NAV);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AudioPlay");

        mRequestManager = Glide.with(MainActivity.this);
        songProgressHandler = new Handler(Looper.getMainLooper());
        progressRunnable = new ProgressRunnable(MainActivity.this);
        helper = new Helper(this);
        pause = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_pause_circle);
        play = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_play_circle_small);

        tabLayout = findViewById(R.id.mainTabLayout);
        viewPager = findViewById(R.id.mainViewPager);

        musicImage = findViewById(R.id.musicImage);
        musicArtistText = findViewById(R.id.musicArtist);
        musicNameText = findViewById(R.id.musicName);
        musicPlayPauseToggle = findViewById(R.id.musicPlayPauseToggle);
        musicProgressBar = findViewById(R.id.musicProgress);
        musicConstraint = findViewById(R.id.musicConstraint);

        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.setDrawerIndicatorEnabled(false);
        mToggle.setToolbarNavigationClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mToggle.setHomeAsUpIndicator(R.drawable.menu);
        mToggle.isDrawerSlideAnimationEnabled();
        mToggle.syncState();

        musicConstraint.setOnClickListener(onClick);
        musicPlayPauseToggle.setOnClickListener(onClick);

        //askForPermission();
        permissionManager.checkPermissions(MainActivity.this);
        if (!Extras.getInstance().getSettings()){
            permissionManager.settingPermission(MainActivity.this);
        }
        setupNavigationView();
        setupViewPagerContent();
    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQ);
        }
        else {
            setupViewPagerContent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQ){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupViewPagerContent();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NAV && resultCode == RESULT_OK) {
            intent = data;
        }
        if (requestCode == WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= M) {
                if (!Settings.System.canWrite(this)) {
                    Log.d("MainActivity", "Granted");
                } else {
                    Extras.getInstance().putBoolean(SETTINGS_TRACK, true);
                    Log.d("MainActivity", "Denied or Grant permission Manually");
                }
            }
        }
    }

    private void setupViewPagerContent() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SongsFragment(), "Songs");
        adapter.addFragment(new AlbumFragment(), "Album");
        adapter.addFragment(new ArtistFragment(), "Artist");
        adapter.addFragment(new PlaylistFragment(), "Playlist");
        //adapter.addFragment(new FolderFragment(), "Folder");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_song);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_album);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_artist);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_playlist);
        //tabLayout.getTabAt(4).setIcon(R.drawable.ic_folder);
    }

    private void setupNavigationView() {
        navigationView = findViewById(R.id.navigationView);
        NavigationViewHelper.enableNavigation(MainActivity.this, navigationView);
    }

    @Override
    public void onSongSelected(List<Song> songList, int pos) {
        if (audioPlayService == null) {
            return;
        }
        Extras.getInstance().saveSeekServices(0);
        audioPlayService.setPlaylist(songList, pos, true);
    }

    @Override
    public void onShuffleRequested(List<Song> songList, boolean play) {
        if (audioPlayService == null) {
            return;
        }
        Extras.getInstance().saveSeekServices(0);
        audioPlayService.setPlaylistAndShuffle(songList, play);
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    /**
     * play Multi Saved Queue
     */
    private void multiQueuePlay() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheet = inflater.inflate(R.layout.bottom_sheet_queue_layout, null);
        RecyclerView recyclerView = bottomSheet.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Dialog bottomSheetDialog = new Dialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        List<Song> mixList = new ArrayList<>();
        List<String> savedQueue;
        savedQueue = Helper.getSavedQueueList(MainActivity.this);
        if (savedQueue == null) {
            return;
        }

       /* new MaterialDialog.Builder(this)
                .title("Play Saved Queue")
                .items(savedQueue)
                .typeface(Helper.getFont(this), Helper.getFont(this))
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        if (dialog.getItems() != null) {
                            List<Song> queueList = new ArrayList<>();
                            for (CharSequence name : text) {
                                CommonDatabase commonDatabase = new CommonDatabase(MainActivity.this, name.toString(), true);
                                queueList.clear();
                                queueList = commonDatabase.readLimit(-1, null);
                                commonDatabase.close();
                            }
                            for (Song song : queueList) {
                                mixList.add(song);
                            }
                        }
                        return true;
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        dialog.clearSelectedIndices();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (mixList.size() > 0) {
                            onShuffleRequested(mixList, true);
                        }
                    }
                })
                .positiveText("Play")
                .negativeText(android.R.string.cancel)
                .autoDismiss(true)
                .dividerColor(accentcolor)
                .build()
                .show();*/
    }

    @Override
    public void addToQueue(Song song) {
        if (audioPlayService != null) {
            audioPlayService.addToQueue(song);
        }
    }

    @Override
    public void setAsNextTrack(Song song) {
        if (audioPlayService != null) {
            audioPlayService.setAsNextTrack(song);
        }
    }

    //play song from outside of the app
    private void openFile(Uri data) {
        List<Song> playList = Helper.getSongMetaData(MainActivity.this, data.getPath());
        if (playList.size() > 0) {
            onSongSelected(playList, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioPlayService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(META_CHANGED);
        filter.addAction(PLAYSTATE_CHANGED);
        filter.addAction(POSITION_CHANGED);
        filter.addAction(ITEM_ADDED);
        filter.addAction(ORDER_CHANGED);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mService) {
            Intent intent = new Intent(this, AudioPlayService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            startService(intent);

        } else {
            if (audioPlayService != null) {
                playingView();
            }
        }
        Glide.get(this).clearMemory();
    }

    //View with song playing details on the mainActivity
    private void playingView() {
        miniPlayerView();
        playPauseToggle();
        finalProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mService) {
            unbindService(mServiceConnection);
            mService = false;
            audioPlayService = null;
            unregisterReceiver(broadcastReceiver);
            removeProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    private void miniPlayerView() {
        if (audioPlayService != null) {
            String title = audioPlayService.getSongTitle();
            String artist = audioPlayService.getSongArtistName();
            musicNameText.setText(title);
            musicNameText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            musicArtistText.setText(artist);
            int duration = audioPlayService.getDuration();
            if (duration != -1) {
                musicProgressBar.setMax(duration);
            }
            musicBackgroundArt();
            Helper.rotationAnim(musicPlayPauseToggle);
        }
    }

    private void musicBackgroundArt() {
        if (audioPlayService == null){
            return;
        }
        runOnUiThread(() -> {
                mRequestManager.load(ArtWork.albumArtUri(audioPlayService.getSongAlbumID()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .placeholder(R.drawable.default_song_album_art)
                        .error(R.drawable.default_song_album_art)
                        .override(300, 300)
                        .into(musicImage);
        });
    }

    private void playPauseToggle() {
        if (audioPlayService != null) {
            if (audioPlayService.isPlaying()) {
                musicPlayPauseToggle.setImageDrawable(pause);
            } else {
                musicPlayPauseToggle.setImageDrawable(play);
            }
        }

    }

    private void finalProgress() {
        songProgressHandler.post(progressRunnable);
    }

    private void removeProgress() {
        songProgressHandler.removeCallbacks(progressRunnable);
        songProgressHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /*if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (intent.getAction().equals(SHOW_ALBUM)) {
                long id = bundle.getLong(ALBUM_ID);
                String title = bundle.getString(ALBUM_NAME);
                String artist = bundle.getString(ALBUM_ARTIST);
                int trackCount = bundle.getInt(ALBUM_TRACK_COUNT);
                Album album = new Album();
                album.setId(id);
                album.setArtistName(artist);
                album.setTrackCount(trackCount);
                album.setAlbumName(title);
                album.setYear(0);
                Log.e("Move", "Go_to_AlbumFrag");
                //setFragment(AlbumFragment.newInstance(album));
            } else if (intent.getAction().equals(SHOW_ARTIST)) {
                long id = bundle.getLong(Constants.ARTIST_ARTIST_ID);
                String name = bundle.getString(Constants.ARTIST_NAME);
                Artist artist = new Artist(id, name, 0, 0);
                Log.e("Move", "Go_to_ArtistFrag");
                //setFragment(ArtistFragment.newInstance(artist));
            } else if (intent.getAction().equals(SHOW_TAG)) {
                //setFragment(TagEditorFragment.getInstance());
                Log.e("Move", "Go_to_TagFrag");
            }
            intent = null;
        }*/
    }

    private void updateProgress() {
        if (audioPlayService != null) {
            int pos = audioPlayService.getPlayerPos();
            musicProgressBar.setProgress(pos);
        }
    }

    public int audioSessionID() {
        int audioID = MediaPlayerSingleton.getInstance().getMediaPlayer().getAudioSessionId();
        return audioID;
    }


    private static class ProgressRunnable implements Runnable {

        private final WeakReference<MainActivity> activityWeakReference;

        public ProgressRunnable(MainActivity myClassInstance) {
            activityWeakReference = new WeakReference<>(myClassInstance);
        }

        @Override
        public void run() {
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null)
                mainActivity.updateProgress();
            songProgressHandler.postDelayed(ProgressRunnable.this, 1000);
        }
    }
}