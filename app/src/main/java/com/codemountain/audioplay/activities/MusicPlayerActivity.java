package com.codemountain.audioplay.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.fragments.PlaylistPickerFragment;
import com.codemountain.audioplay.interfaces.bitmap;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.service.AudioPlayService;
import com.codemountain.audioplay.service.MediaPlayerSingleton;
import com.codemountain.audioplay.utils.ArtWork;
import com.codemountain.audioplay.utils.Constants;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.codemountain.audioplay.utils.Constants.ITEM_ADDED;
import static com.codemountain.audioplay.utils.Constants.META_CHANGED;
import static com.codemountain.audioplay.utils.Constants.ORDER_CHANGED;
import static com.codemountain.audioplay.utils.Constants.PLAYSTATE_CHANGED;
import static com.codemountain.audioplay.utils.Constants.POSITION_CHANGED;
import static com.codemountain.audioplay.utils.Constants.QUEUE_CHANGED;

public class MusicPlayerActivity extends AppCompatActivity {

    private static MusicPlayerActivity musicPlayerActivity;
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView songTitle, artistName, currentDuration, totalDuration;
    private ImageButton skipPreviousBtn, skipNextBtn, playPauseBtn, repeatSongBtn, shuffleSongBtn;
    private SeekBar seekBar;
    private AudioPlayService audioPlayService;
    private boolean mServiceBound = false;
    private static Handler handler;
    private Drawable shuffleOff, shuffleOn, repeatAll, repeatOne, noRepeat;
    private int position;
    private RecyclerView queueRv;
    //private QueueAdapter queueAdapter;
    private Helper helper;
    private List<Song> queueList = new ArrayList<>();
    //private UpdateAlbumArt updateAlbumArt;
    private com.codemountain.audioplay.interfaces.bitmap bitmap;
    private RequestManager mRequestManager;
    private WaveVisualizer waveVisualizer;
    private BarVisualizer barVisualizer;

    //BottomSheet widgets
    private ImageView bottomImage;
    private TextView bottomSongTitle, bottomSongArtist;
    private LinearLayout addToPlaylist, delete, playNext, setAsRingTone, shareSong;
    Dialog bottomSheetDialog;


    /**
     * Service Connection
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayService.AudioPlayBinder binder = (AudioPlayService.AudioPlayBinder) service;
            audioPlayService = binder.getService();
            mServiceBound = true;
            if (audioPlayService != null) {
                reload();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    /**
     * BroadCast
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (audioPlayService == null) {
                return;
            }
            String action = intent.getAction();
            switch (action) {
                case PLAYSTATE_CHANGED:
                    playbackConfig();
                    break;
                case META_CHANGED:
                    metaConfig();
                    break;
                case QUEUE_CHANGED:
                case POSITION_CHANGED:
                case ITEM_ADDED:
                case ORDER_CHANGED:
                    queueConfig();
                    break;
            }
        }
    };

    private View.OnClickListener onClick = v -> {
        if (getAudioPlayService() == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.playPauseImageBtn:
                getAudioPlayService().toggle();
                break;
            case R.id.shuffleSongBtn:
                boolean shuffle = getAudioPlayService().isShuffleEnabled();
                getAudioPlayService().setShuffleEnabled(!shuffle);
                updateShuffleButton();
                break;
            case R.id.repeatSongBtn:
                int mode = getAudioPlayService().getNextRepeatMode();
                getAudioPlayService().setRepeatMode(mode);
                switch (mode){
                    case 1:
                        Toast.makeText(this, "No Repeat", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "Repeat All", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, "Repeat Current", Toast.LENGTH_SHORT).show();
                        break;
                }
                updateRepeatButton();
                break;
            case R.id.skipNextBtn:
                getAudioPlayService().playNext(true);
                break;
            case R.id.skipPreviousBtn:
                getAudioPlayService().playPrev(true);
                break;
        }

    };

    public static MusicPlayerActivity getInstance(){
        return musicPlayerActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        musicPlayerActivity = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        helper = new Helper(this);
        mRequestManager = Glide.with(MusicPlayerActivity.this);


        imageView = findViewById(R.id.musicPlayerImage);
        songTitle = findViewById(R.id.musicPlayerSongTitle);
        artistName = findViewById(R.id.musicPlayerArtistName);
        currentDuration = findViewById(R.id.currentDurationCount);
        totalDuration = findViewById(R.id.totalDurationCount);
        skipNextBtn = findViewById(R.id.skipNextBtn);
        skipPreviousBtn = findViewById(R.id.skipPreviousBtn);
        playPauseBtn = findViewById(R.id.playPauseImageBtn);
        repeatSongBtn = findViewById(R.id.repeatSongBtn);
        shuffleSongBtn = findViewById(R.id.shuffleSongBtn);
        seekBar = findViewById(R.id.circular_seekbar);
        waveVisualizer = findViewById(R.id.musicPlayerWaveVisualizer);
        barVisualizer = findViewById(R.id.musicPlayerBarVisualizer);

        handler = new Handler(Looper.getMainLooper());
        shuffleOff = ContextCompat.getDrawable(this, R.drawable.ic_shuffle);
        shuffleOn = ContextCompat.getDrawable(this, R.drawable.ic_shuffle_accent);
        repeatOne = ContextCompat.getDrawable(this, R.drawable.ic_repeat_one);
        repeatAll = ContextCompat.getDrawable(this, R.drawable.ic_repeat_accent);
        noRepeat = ContextCompat.getDrawable(this, R.drawable.ic_repeat);

        skipNextBtn.setOnClickListener(onClick);
        skipPreviousBtn.setOnClickListener(onClick);
        playPauseBtn.setOnClickListener(onClick);
        shuffleSongBtn.setOnClickListener(onClick);
        repeatSongBtn.setOnClickListener(onClick);

        setupVisualizer();

    }

    private void setupVisualizer() {
        String value = Extras.getInstance().getVisualizerValue();
        switch (value){
            case "wave":
                waveVisualizer.setVisibility(View.VISIBLE);
                barVisualizer.setVisibility(View.GONE);
                if (audioSessionID() != -1){
                    waveVisualizer.setAudioSessionId(audioSessionID());
                }
                break;

            case "bars":
                barVisualizer.setVisibility(View.VISIBLE);
                waveVisualizer.setVisibility(View.GONE);
                if (audioSessionID() != -1){
                    barVisualizer.setAudioSessionId(audioSessionID());
                }
                break;
        }

    }

    //Update shuffle button when clicked
    public void updateShuffleButton() {
        boolean shuffle = audioPlayService.isShuffleEnabled();
        if (shuffle) {
            shuffleButton().setImageDrawable(shuffleOn);
        }
        else {
            shuffleButton().setImageDrawable(shuffleOff);
        }
    }

    //Update repeat button when clicked
    public void updateRepeatButton() {
        int mode = audioPlayService.getRepeatMode();
        if (mode == getAudioPlayService().getNoRepeat()) {
            repeatButton().setImageDrawable(noRepeat);
        }
        else if (mode == getAudioPlayService().getRepeatCurrent()) {
            repeatButton().setImageDrawable(repeatOne);
        }
        else if (mode == getAudioPlayService().getRepeatAll()) {
            repeatButton().setImageDrawable(repeatAll);
        }
    }

    public ImageButton shuffleButton() {
        return shuffleSongBtn;
    }

    public ImageButton repeatButton() {
        return repeatSongBtn;
    }

    public void updateProgress() {
        if (getAudioPlayService() != null && getAudioPlayService().isPlaying()) {
            position = getAudioPlayService().getPlayerPos();
            seekBar.setProgress(position);
            currentDuration.setText(Helper.calculateDuration(position));
        }
    }


    private void updateQueue() {
        /*if (getAudioPlayService() == null) {
            return;
        }
        queueList = getAudioPlayService().getPlayList();
        int pos = getAudioPlayService().returnPos();
        if (queueList != queueAdapter.getSnapshot() && queueList.size() > 0) {
            queueAdapter.addDataList(queueList);
        }
        updateQueuePos(pos);
        queueAdapter.notifyDataSetChanged();*/
    }

    //update queue pos
    private void updateQueuePos(int pos) {
        /*queueAdapter.notifyDataSetChanged();
        queueAdapter.setSelection(pos);
        if (pos >= 0 && pos < queueList.size()) {
            queueRv.scrollToPosition(pos);
        }*/
    }


    public void reload() {
        playingView();
        setButtonDrawable();
        updateShuffleButton();
        updateRepeatButton();
        musicBackgroundArt();
        seekBarProgress();
        updateQueue();
        //favButton();
    }

    public void playbackConfig() {
        setButtonDrawable();
    }

    public void metaConfig() {
        playingView();
        seekBarProgress();
        musicBackgroundArt();
        //favButton();
    }

    public void queueConfig() {
        updateQueue();
    }

    public AudioPlayService getAudioPlayService() {
        return audioPlayService;
    }

    public void seekBarProgress() {
        ProgressRunnable progressRunnable = new ProgressRunnable(MusicPlayerActivity.this);
        handler.post(progressRunnable);
    }

    public void removeCallback() {
        ProgressRunnable progressRunnable = new ProgressRunnable(MusicPlayerActivity.this);
        handler.removeCallbacks(progressRunnable);
        handler.removeCallbacksAndMessages(null);
    }

    public void metaChangedBroadcast() {
        Intent intent = new Intent(Constants.META_CHANGED);
        if (Constants.META_CHANGED.equals(intent.getAction())) {
            this.sendBroadcast(intent);
            Log.e("BasePlay", "BroadCast");
        }
    }

    public int audioSessionID() {
        int audioID = MediaPlayerSingleton.getInstance().getMediaPlayer().getAudioSessionId();
        return audioID;
    }

    private void setButtonDrawable() {
        if (getAudioPlayService() != null) {
            if (getAudioPlayService().isPlaying()) {
                playPauseBtn.setImageResource(R.drawable.ic_pause_circle_medium);
            }
            else {
                playPauseBtn.setImageResource(R.drawable.ic_play_circle_medium);
            }
        }

    }

    public void playingView() {
        if (getAudioPlayService() != null) {
            String title = getAudioPlayService().getSongTitle();
            String artist = getAudioPlayService().getSongArtistName();
            songTitle.setText(title);
            songTitle.setSelected(true);
            songTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            artistName.setText(artist);
            Helper.rotationAnim(playPauseBtn);
            //Helper.rotationAnim(imageView);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar circularSeekBar, int progress, boolean fromUser) {
                    if (fromUser && getAudioPlayService() != null && (getAudioPlayService().isPlaying() || getAudioPlayService().isPaused())) {
                        getAudioPlayService().seekTo(circularSeekBar.getProgress());
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
            });
            int duration = getAudioPlayService().getDuration();
            if (duration != -1) {
                seekBar.setMax(duration);
                totalDuration.setText(Helper.calculateDuration(duration));
            }
            //LyricsHelper.LoadLyrics(this, title, artist, getMusicXService().getsongAlbumName(), getMusicXService().getsongData(), lrcView);
            // NetworkHelper.absolutesLyrics(getContext(),artist, title, getMusicXService().getsongAlbumName(), getMusicXService().getsongData(), lrcView);
            updateQueuePos(getAudioPlayService().returnPos());
            bitmap = new bitmap() {
                @Override
                public void bitmapWorking(Bitmap bitmap) {
                    //Artwork.blurPreferances(getContext(), bitmap, blur_artowrk);
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void bitmapFailed(Bitmap bitmap) {
                    //Artwork.blurPreferances(getContext(), bitmap, blur_artowrk);
                    imageView.setImageBitmap(bitmap);
                }
            };
        }
    }

    private void musicBackgroundArt() {
        this.runOnUiThread(() -> {
            mRequestManager.load(ArtWork.albumArtUri(audioPlayService.getSongAlbumID()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .placeholder(R.drawable.default_song_album_art)
                    .error(R.drawable.default_song_album_art)
                    .override(300, 300)
                    .into(imageView);

            /*byte[] image = ArtWork.getAlbumArt(audioPlayService.getSongData());
            Bitmap bitmap;
            if (image != null){

                mRequestManager.load(image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .placeholder(R.drawable.default_song_album_art)
                        .error(R.drawable.default_song_album_art)
                        .override(300, 300)
                        .into(imageView);

                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                Palette.from(bitmap).generate(palette -> {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        RelativeLayout container = findViewById(R.id.container);
                        container.setBackgroundResource(R.color.colorPrimary);
                        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        container.setBackground(drawable);
                        songTitle.setTextColor(swatch.getTitleTextColor());
                        artistName.setTextColor(swatch.getBodyTextColor());
                    }
                    else {
                        RelativeLayout container = findViewById(R.id.container);
                        container.setBackgroundResource(R.color.colorPrimary);
                        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        container.setBackground(drawable);
                        songTitle.setTextColor(ContextCompat.getColor(this,R.color.white));
                        artistName.setTextColor(ContextCompat.getColor(this, R.color.colorLightGray));
                    }
                });
            }*/


        });
    }



    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioPlayService.class);
        this.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mServiceBound) {
            Intent intent = new Intent(this, AudioPlayService.class);
            this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            this.startService(intent);
            IntentFilter filter = new IntentFilter();
            filter.addAction(META_CHANGED);
            filter.addAction(PLAYSTATE_CHANGED);
            filter.addAction(POSITION_CHANGED);
            filter.addAction(ITEM_ADDED);
            filter.addAction(ORDER_CHANGED);
            try {
                this.registerReceiver(broadcastReceiver, filter);
            } catch (Exception e) {
                // already registered
            }
        } else {
            if (audioPlayService != null) {
                reload();
            }
        }
        Glide.get(this).clearMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        //onPaused();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mServiceBound) {
            audioPlayService = null;
            this.unbindService(serviceConnection);
            mServiceBound = false;
            try {
                this.unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                // already unregistered
            }
        }
    }

    @Override
    public void onDestroy() {
        /*if (updateAlbumArt != null) {
            updateAlbumArt.cancel(true);
        }*/
        removeCallback();
        String value = Extras.getInstance().getVisualizerValue();
        switch (value){
            case "wave":
                if (waveVisualizer != null){
                    waveVisualizer.release();
                }
                break;

            case "bars":
                if (barVisualizer != null){
                    barVisualizer.release();
                }
                break;
        }
        super.onDestroy();
    }


    private void openBottomSheetDialog() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheet = inflater.inflate(R.layout.bottom_sheet_layout, null);

        bottomImage = bottomSheet.findViewById(R.id.bottomImage);
        bottomSongTitle = bottomSheet.findViewById(R.id.dialogSongTitle);
        bottomSongArtist = bottomSheet.findViewById(R.id.dialogArtistName);
        addToPlaylist = bottomSheet.findViewById(R.id.addToPlaylist);
        playNext = bottomSheet.findViewById(R.id.playNext);
        setAsRingTone = bottomSheet.findViewById(R.id.setAsRingTone);
        delete = bottomSheet.findViewById(R.id.delete);
        shareSong = bottomSheet.findViewById(R.id.shareSong);


        bottomSongTitle.setText(audioPlayService.getSongTitle());
        bottomSongArtist.setText(audioPlayService.getSongArtistName());
        Glide.with(this).load(ArtWork.albumArtUri(audioPlayService.getSongAlbumID()))
                .placeholder(R.drawable.default_song_album_art)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(bottomImage);

        bottomSheetDialog = new Dialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        addToPlaylist.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("POSITION", audioPlayService.returnPos());
            PlaylistPickerFragment playlistPickerFragment = new PlaylistPickerFragment();
            playlistPickerFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.container, playlistPickerFragment, "playList");
            transaction.addToBackStack("songs");
            transaction.commit();
            bottomSheetDialog.dismiss();
        });

        playNext.setOnClickListener(v -> {
            Toast.makeText(this, "Playing next", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        setAsRingTone.setOnClickListener(v -> {
            String path = audioPlayService.getSongData();
            Helper.setRingTone(this, path);
            bottomSheetDialog.dismiss();
        });
        delete.setOnClickListener(v -> {
            String name = audioPlayService.getSongTitle();
            String path = audioPlayService.getSongData();
            Helper.deleteTrack(name, path, this);
            bottomSheetDialog.dismiss();
        });
        shareSong.setOnClickListener(v ->
                Helper.shareMusic(audioPlayService.getSongId(), this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.music_player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(MusicPlayerActivity.this, MainActivity.class));
        }

        if (item.getItemId() == R.id.equalizerBtn){
            int audioSessionID = audioPlayService.audioSession();
            startActivity(new Intent(MusicPlayerActivity.this, EqualizerActivity.class)
                    .putExtra("AUDIO_SESSION_ID", audioSessionID));
        }

        if (item.getItemId() == R.id.musicPlayerMore){
            openBottomSheetDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ProgressRunnable implements Runnable {

        private final WeakReference<MusicPlayerActivity> baseLoaderWeakReference;

        public ProgressRunnable(MusicPlayerActivity myClassInstance) {
            baseLoaderWeakReference = new WeakReference<>(myClassInstance);
        }

        @Override
        public void run () {
            MusicPlayerActivity musicPlayerActivity = baseLoaderWeakReference.get();
            if (musicPlayerActivity != null){
                musicPlayerActivity.updateProgress();
            }
            handler.postDelayed(ProgressRunnable.this,1000);
        }
    }

}