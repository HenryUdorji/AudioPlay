package com.codemountain.audioplay.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.adapters.ArtistDetailsAdapter;
import com.codemountain.audioplay.interfaces.MetaDatas;
import com.codemountain.audioplay.loaders.ArtistLoader;
import com.codemountain.audioplay.loaders.ArtistSongLoader;
import com.codemountain.audioplay.model.Artist;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.service.AudioPlayService;
import com.codemountain.audioplay.service.MediaPlayerSingleton;
import com.codemountain.audioplay.utils.ArtWork;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.codemountain.audioplay.utils.Constants.ITEM_ADDED;
import static com.codemountain.audioplay.utils.Constants.META_CHANGED;
import static com.codemountain.audioplay.utils.Constants.NAV;
import static com.codemountain.audioplay.utils.Constants.ORDER_CHANGED;
import static com.codemountain.audioplay.utils.Constants.PLAYSTATE_CHANGED;
import static com.codemountain.audioplay.utils.Constants.POSITION_CHANGED;

public class ArtistDetailActivity extends AppCompatActivity implements MetaDatas, SearchView.OnQueryTextListener {

    private static final String TAG = "ArtistDetailActivity";
    private static ArtistDetailActivity artistDetailActivity;
    private long artistID;
    private Toolbar toolbar;
    private Intent intent;
    private ImageView detailsImage;
    private RecyclerView recyclerView;
    private TextView artistNameText, artistNumOfSongText, artistAlbumCountText;
    private CircleImageView musicImage;
    private ImageButton musicPlayPauseToggle;
    private TextView musicNameText, musicArtistText;
    private ProgressBar musicProgressBar;
    private ConstraintLayout musicConstraint;
    private Drawable pause, play;
    public ArtistDetailsAdapter artistDetailsAdapter;
    private ProgressRunnable progressRunnable;
    private RequestManager mRequestManager;
    private boolean mService = false;
    private AudioPlayService audioPlayService;
    private static Handler songProgressHandler;
    private Helper helper;
    private Artist artist;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayService.AudioPlayBinder binder = (AudioPlayService.AudioPlayBinder) service;
            audioPlayService = binder.getService();
            mService = true;
            if (audioPlayService != null) {
                reload();
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
                    Intent intent = new Intent(ArtistDetailActivity.this, MusicPlayerActivity.class);
                    startActivityForResult(intent, NAV);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    break;
            }
        }
    };

    public static ArtistDetailActivity getInstance(){
        return artistDetailActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Intent intent = getIntent();
        artistID = intent.getLongExtra("ARTIST_ID", 0);

        artistDetailActivity = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        artistNameText = findViewById(R.id.artistDetailsArtistName);
        artistNumOfSongText = findViewById(R.id.artistDetailsNumOfSong);
        artistAlbumCountText = findViewById(R.id.artistDetailsNumOfAlbums);
        detailsImage = findViewById(R.id.albumDetailsImage);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        musicImage = findViewById(R.id.musicImage);
        musicArtistText = findViewById(R.id.musicArtist);
        musicNameText = findViewById(R.id.musicName);
        musicPlayPauseToggle = findViewById(R.id.musicPlayPauseToggle);
        musicProgressBar = findViewById(R.id.musicProgress);
        musicConstraint = findViewById(R.id.musicConstraint);

        musicConstraint.setOnClickListener(onClick);
        musicPlayPauseToggle.setOnClickListener(onClick);

        mRequestManager = Glide.with(this);
        songProgressHandler = new Handler(Looper.getMainLooper());
        progressRunnable = new ProgressRunnable(ArtistDetailActivity.this);
        helper = new Helper(this);

        pause = ContextCompat.getDrawable(this, R.drawable.ic_pause_circle);
        play = ContextCompat.getDrawable(this, R.drawable.ic_play_circle_small);

        setArtistDetails();
        new LoadArtistSongData().execute("");
    }

    private class LoadArtistSongData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            ArrayList<Song> songSize = new ArrayList<>(ArtistSongLoader.getAllArtistSongs(ArtistDetailActivity.this, artistID));
            artistDetailsAdapter = new ArtistDetailsAdapter(ArtistDetailActivity.this,
                    songSize);
            Log.e(TAG, "Total songs in list " + songSize.size());

            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            artistDetailsAdapter.setOnItemClickListener((view, position) -> {
                onSongSelected(artistDetailsAdapter.getSongsList(), position);
                Extras.getInstance().saveSeekServices(0);
            });
            recyclerView.setAdapter(artistDetailsAdapter);

        }
    }

    private void reload() {
        playingView();

    }

    private void setArtistDetails() {
        artist = new ArtistLoader(this).getSingleArtist(this, artistID);
        artistNameText.setText(artist.getName());
        artistNumOfSongText.setText(artist.getTrackCount() + " Song(s)");
        artistAlbumCountText.setText("Albums: " + artist.getAlbumCount());
    }


    //play song from outside of the app
    private void openFile(Uri data) {
        List<Song> playList = Helper.getSongMetaData(ArtistDetailActivity.this, data.getPath());
        if (playList.size() > 0) {
            onSongSelected(playList, 0);
        }
    }

    private void playingView() {
        miniPlayerView();
        playPauseToggle();
        finalProgress();
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

    public int audioSessionID() {
        int audioID = MediaPlayerSingleton.getInstance().getMediaPlayer().getAudioSessionId();
        return audioID;
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

            mRequestManager.load(ArtWork.albumArtUri(artist.getId()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .placeholder(R.drawable.default_song_album_art)
                    .error(R.drawable.default_song_album_art)
                    .override(300, 300)
                    .into(detailsImage);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.artist_details_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.artist_details_search));
        searchView.setQueryHint("Search Artist Songs");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(ArtistDetailActivity.this, MainActivity.class));
        }
        /*Extras extras = Extras.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setArtistSortOrder(SortOrder.ArtistSongSortOrder.SONG_A_Z);
                this.recreate();
                break;
            case R.id.menu_sort_by_za:
                extras.setArtistSortOrder(SortOrder.ArtistSongSortOrder.SONG_Z_A);
                this.recreate();
                break;
            case R.id.menu_sort_by_year:
                extras.setArtistSortOrder(SortOrder.ArtistSongSortOrder.SONG_YEAR);
                this.recreate();
                break;
            case R.id.menu_sort_by_artist:
                extras.setArtistSortOrder(SortOrder.ArtistSongSortOrder.SONG_DATE);
                this.recreate();
                break;
            case R.id.menu_sort_by_number_of_songs:
                extras.setArtistSortOrder(SortOrder.ArtistSongSortOrder.SONG_DURATION);
                this.recreate();
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Song> filterList = Helper.filterSong(ArtistSongLoader.getAllArtistSongs(ArtistDetailActivity.this, artistID), newText);
        if (filterList.size() > 0) {
            artistDetailsAdapter.setFilter(filterList);
            return true;
        } else {
            Toast.makeText(ArtistDetailActivity.this, "No data found...", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    private static class ProgressRunnable implements Runnable {

        private final WeakReference<ArtistDetailActivity> baseLoaderWeakReference;

        public ProgressRunnable(ArtistDetailActivity myClassInstance) {
            baseLoaderWeakReference = new WeakReference<>(myClassInstance);
        }

        @Override
        public void run () {
            ArtistDetailActivity artistDetailActivity = baseLoaderWeakReference.get();
            if (artistDetailActivity != null){
                artistDetailActivity.updateProgress();
            }
            songProgressHandler.postDelayed(ArtistDetailActivity.ProgressRunnable.this,1000);
        }
    }
}