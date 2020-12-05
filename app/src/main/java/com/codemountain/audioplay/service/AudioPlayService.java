package com.codemountain.audioplay.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.codemountain.audioplay.database.CommonDatabase;
import com.codemountain.audioplay.interfaces.PlayInterface;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.utils.Constants;
import com.codemountain.audioplay.utils.Extras;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.codemountain.audioplay.service.NotificationHandler.notificationID;
import static com.codemountain.audioplay.utils.Constants.ACTION_CHANGE_STATE;
import static com.codemountain.audioplay.utils.Constants.ACTION_CHOOSE_SONG;
import static com.codemountain.audioplay.utils.Constants.ACTION_COMMAND;
import static com.codemountain.audioplay.utils.Constants.ACTION_COMMAND1;
import static com.codemountain.audioplay.utils.Constants.ACTION_FAV;
import static com.codemountain.audioplay.utils.Constants.ACTION_NEXT;
import static com.codemountain.audioplay.utils.Constants.ACTION_PAUSE;
import static com.codemountain.audioplay.utils.Constants.ACTION_PLAY;
import static com.codemountain.audioplay.utils.Constants.ACTION_PREVIOUS;
import static com.codemountain.audioplay.utils.Constants.ACTION_STOP;
import static com.codemountain.audioplay.utils.Constants.ACTION_TOGGLE;
import static com.codemountain.audioplay.utils.Constants.ITEM_ADDED;
import static com.codemountain.audioplay.utils.Constants.META_CHANGED;
import static com.codemountain.audioplay.utils.Constants.ORDER_CHANGED;
import static com.codemountain.audioplay.utils.Constants.PLAYER_POS;
import static com.codemountain.audioplay.utils.Constants.PLAYSTATE_CHANGED;
import static com.codemountain.audioplay.utils.Constants.POSITION_CHANGED;
import static com.codemountain.audioplay.utils.Constants.QUEUE_CHANGED;
import static com.codemountain.audioplay.utils.Constants.REPEAT_MODE_CHANGED;
import static com.codemountain.audioplay.utils.Constants.SONG_ALBUM;
import static com.codemountain.audioplay.utils.Constants.SONG_ALBUM_ID;
import static com.codemountain.audioplay.utils.Constants.SONG_ARTIST;
import static com.codemountain.audioplay.utils.Constants.SONG_ID;
import static com.codemountain.audioplay.utils.Constants.SONG_PATH;
import static com.codemountain.audioplay.utils.Constants.SONG_TITLE;
import static com.codemountain.audioplay.utils.Constants.SONG_TRACK_NUMBER;

public class AudioPlayService extends Service implements PlayInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AudioPlayService";
    private static final int NO_REPEAT = 1;
    public final int REPEAT_ALL = 2;
    public final int REPEAT_CURRENT = 3;
    private AudioPlayBinder audioPlayBinder = new AudioPlayBinder();
    private boolean isShuffled = false;
    private boolean isRepeated = false;
    private List<Song> playList = new ArrayList<>();
    private List<Song> ogList = new ArrayList<>();
    private Song currentSong;
    private int playingIndex;
    private boolean fastPlay = false;
    private boolean isPlaying = false;
    private String songTitle, songArtist, songPath;
    private long albumID, songID;
    private int trackDuration;
    private boolean widgetPermission;
    private static Handler handler;
    private boolean paused;
    private int repeatMode = NO_REPEAT;
    private AudioManager audioManager;
    private boolean mIsDucked = false;
    private boolean mLostAudioFocus = false;
    private boolean onPlayNotify = false;
    private CommonDatabase recent, queue;
    private List<Song> queueList = new ArrayList<>();
    private ControlReceiver controlReceiver = null;
    private MediaButtonReceiver mediaButtonReceiver;
    private MediaSessionCompat mediaSessionLockScreen;
    private TelephonyManager telephonyManager;

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_RINGING:
                    if (Extras.getInstance().phoneCallConfig()) {
                        pause();
                    } else {
                        play();
                    }
                    break;
            }
        }
    };
    private BroadcastReceiver headsetListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG) && isPlaying()) {
                if (Extras.getInstance().headsetConfig()) {
                    pause();
                } else {
                    play();
                }
            }

        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBind");
        return audioPlayBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        saveState(true);
        Extras.getInstance().saveSeekServices(getPlayerPos());
        if (isPlaying() && playList.size() > 0) {
            //return false;
            return true;
        }
        Log.d(TAG, "Unbind");
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaPlayer();
        otherStuff();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_CHOOSE_SONG: {
                    returnHome();
                }
                case ACTION_TOGGLE: {
                    toggle();
                    break;
                }
                case ACTION_PAUSE: {
                    pause();
                    break;
                }
                case ACTION_PLAY: {
                    play();
                    break;
                }
                case ACTION_STOP: {
                    stopSelf();
                    break;
                }
                case ACTION_NEXT: {
                    playNext(true);
                    break;
                }
                case ACTION_PREVIOUS: {
                    playPrev(true);
                    break;
                }
                case ACTION_CHANGE_STATE: {
                    if (widgetPermission) {
                        /*if (!Extras.getInstance().floatingWidget()) {
                            audioWidget.show(Extras.getInstance().getwidgetPositionX(), Extras.getInstance().getwidgetPositionY());
                        } else {
                            audioWidget.hide();
                        }*/
                    }
                    break;
                }
                /*case ACTION_COMMAND: {
                    int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    musicxWidget.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
                }
                case ACTION_COMMAND1: {
                    int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    musicXwidget4x4.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
                }
                case ACTION_COMMAND2: {
                    int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    musicXWidget5x5.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
                }
                case ACTION_FAV: {
                    if (favHelper.isFavorite(getsongId())) {
                        favHelper.removeFromFavorites(getsongId());
                        updateService(META_CHANGED);
                    } else {
                        favHelper.addFavorite(getsongId());
                        updateService(META_CHANGED);
                    }
                }*/

            }
            return START_STICKY;
        } else {
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playList.size() == 0 || returnPos() == -1) {
            //audioWidget.controller().stop();
            return;
        }
        playNext(true);
        int pos = playList.size() - 1;
        if (playList.get(pos) != null) {
            updateService(PLAYSTATE_CHANGED);
            /*if (widgetPermission) {
                if (!Extras.getInstance().floatingWidget()) {
                    audioWidget.Stop();
                    audioWidget.Pos(0);
                    trackingstop();
                } else {
                    audioWidget.hide();
                }
            }*/
        }
        Extras.getInstance().saveSeekServices(0);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, String.valueOf(what) + String.valueOf(extra));
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        updateService(META_CHANGED);
        restorePos();
        if (fastPlay) {
            play();
            fastPlay = false;
        }
        trackDuration = MediaPlayerSingleton.getInstance().getMediaPlayer().getDuration();
        /*if (widgetPermission) {
            if (!Extras.getInstance().floatingWidget()) {
                audioWidget.Pos(0);
                trackingstop();
                audioWidget.Dur(getDuration());
                widgetCover();
                trackingstart();
            } else {
                audioWidget.hide();
            }
        }*/
        Log.d(TAG, "Prepared");
    }

    //////////////////////// Play Interface //////////////////////////////

    @Override
    public void play() {
        if (currentSong == null) {
            return;
        }
        if (returnPos() != -1) {
            if (Extras.getInstance().getFadeTrack()) {
                finalPlay();
                fadeIn(MediaPlayerSingleton.getInstance().getMediaPlayer(), fadeDurationValue());
            } else {
                finalPlay();
                MediaPlayerSingleton.getInstance().getMediaPlayer().start();

            }
        }
    }

    @Override
    public void pause() {
        if (currentSong == null) {
            return;
        }
        if (Extras.getInstance().getFadeTrack()) {
            finalPause();
            fadeOut(MediaPlayerSingleton.getInstance().getMediaPlayer(), fadeDurationValue());
        } else {
            finalPause();
            MediaPlayerSingleton.getInstance().getMediaPlayer().pause();
        }
    }

    @Override
    public void playNext(boolean value) {
        int position = getNextPos(value);
        if (position != -1 && position < playList.size()) {
            paused = false;
            fastPlay = true;
            currentSong = playList.get(position);
            fastPlay(true, currentSong);
            Log.d(TAG, "PlayNext");
            Extras.getInstance().saveSeekServices(0);
        }
        else {
            fastPlay = false;
            paused = true;
            isPlaying = false;
        }
    }

    @Override
    public void playPrev(boolean value) {
        int position = getPrevPos(value);
        if (position != -1 && position < playList.size()) {
            fastPlay = true;
            paused = false;
            currentSong = playList.get(position);
            fastPlay(true, currentSong);
            Log.d(TAG, "PlayPrev");
            Extras.getInstance().saveSeekServices(0);
        }
        else {
            fastPlay = false;
            paused = true;
            isPlaying = false;
        }
    }

    @Override
    public void shuffle() {
        if (playList.size() > 0) {
            Random rand = new Random();
            long speed = System.nanoTime();
            rand.setSeed(speed);
            Collections.shuffle(playList, rand);
            Log.d(TAG, "shuffle playlist");
        }
    }

    @Override
    public void toggle() {
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() == null) {
            return;
        }
        if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    @Override
    public void returnHome() {

    }

    @Override
    public void forwardPlayingView() {

    }

    @Override
    public int getDuration() {
        if (returnPos() == -1) {
            return 0;
        }
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() != null && returnPos() < playList.size()) {
            Log.d(TAG, "ReturnDuration");
            return trackDuration;
        }else {
            return -1;
        }
    }

    @Override
    public int getPlayerPos() {
        if (returnPos() == -1) {
            return 0;
        }
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() != null && returnPos() < playList.size()) {
            return MediaPlayerSingleton.getInstance().getMediaPlayer().getCurrentPosition();
        } else {
            return -1;
        }
    }

    @Override
    public void seekTo(int seek) {
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() != null) {
            MediaPlayerSingleton.getInstance().getMediaPlayer().seekTo(seek);
        } else {
            MediaPlayerSingleton.getInstance().getMediaPlayer().seekTo(0);
        }

    }

    @Override
    public void setPlaylist(List<Song> songList, int pos, boolean play) {
        smartPlaylist(songList);
        setDataPos(pos, play);
        if (isShuffled) {
            shuffle();
        }
        updateService(QUEUE_CHANGED);
    }

    @Override
    public void smartPlaylist(List<Song> smartPlaylists) {
        if (smartPlaylists != null && smartPlaylists.size() > 0) {
            ogList = smartPlaylists;
            playList.clear();
            playList.addAll(ogList);
        } else {
            Log.d(TAG, "smartPlaylist error");
        }
    }

    @Override
    public void setDataPos(int pos, boolean play) {
        if (pos != -1 && pos < playList.size()) {
            playingIndex = pos;
            currentSong = playList.get(pos);
            if (play) {
                fastPlay(true, currentSong);
            } else {
                fastPlay(false, currentSong);
            }
        } else {
            Log.d(TAG, "null value");
        }
    }

    @Override
    public void trackingStart() {
        widgetProgress();
    }

    @Override
    public void trackingStop() {
        removeProgress();
    }

    @Override
    public void startCurrentTrack(Song song) {
        if (returnPos() != -1 && playList.size() > 0) {
            if (MediaPlayerSingleton.getInstance().getMediaPlayer() == null) {
                return;
            }
            Uri dataLoader = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
            if (dataLoader == null) {
                return;
            }
            try {
                MediaPlayerSingleton.getInstance().getMediaPlayer().reset();
                MediaPlayerSingleton.getInstance().getMediaPlayer().setDataSource(AudioPlayService.this, dataLoader);
                MediaPlayerSingleton.getInstance().getMediaPlayer().prepareAsync();
                MediaPlayerSingleton.getInstance().getMediaPlayer().setAuxEffectSendLevel(1.0f);
                Log.d(TAG, "Prepared");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "position error");
        }
    }

    @Override
    public void setPlaylistAndShuffle(List<Song> songList, boolean play) {
        if (play) {
            smartPlaylist(songList);
            isShuffled = true;
            updateService(QUEUE_CHANGED);
            shuffle();
            Extras.getInstance().saveSeekServices(0);
            setDataPos(0, true);
        }
    }

    @Override
    public void addToQueue(Song song) {
        if (playList.size() > 0 || playList.size() == 0) {
            ogList.add(song);
            playList.add(song);
            updateService(ITEM_ADDED);
        }
    }

    @Override
    public void setAsNextTrack(Song song) {
        if (playList.size() > 0 || playList.size() == 0) {
            ogList.add(song);
            playList.add(returnPos() + 1, song);
            updateService(ITEM_ADDED);
        }
    }

    @Override
    public void fastPlay(boolean torf, Song song) {
        fastPlay = torf;
        startCurrentTrack(song);
    }

    @Override
    public int getNextPos(boolean yorno) {
        int incPos = returnPos() + 1;
        if (repeatMode == REPEAT_ALL) {
            Log.d(TAG, "Repeat --> All");
            int pos = playList.size() - 1;
            if (pos == returnPos()){
                return 0;
            } else {
                return incPos;
            }
        } else if (repeatMode == REPEAT_CURRENT) {
            Log.d(TAG, "Repeat --> CURRENT");
            return returnPos();
        } else if (repeatMode == NO_REPEAT & yorno) {
            Log.d(TAG, "Repeat --> NO REPEAT");
            return incPos;
        }
        return -1;
    }

    @Override
    public int getPrevPos(boolean yorno) {
        int pos = returnPos();

        if (repeatMode == REPEAT_CURRENT) {
            if (pos != -1 && pos < playList.size()) {
                return pos;
            } else {
                return -1;
            }
        } else if (yorno) {
            if (pos != -1 && pos < playList.size()) {
                return pos - 1;
            } else {
                return -1;
            }
        }
        return -1;
    }

    @Override
    public void checkTelephonyState() {
        if (Extras.getInstance().getHeadset()) {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    }

    @Override
    public void headsetState() {
        if (headsetListener != null) {
            IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(headsetListener, receiverFilter);
        }
    }

    @Override
    public void stopMediaPlayer() {
        if (MediaPlayerSingleton.getInstance().getMediaPlayer() == null) {
            return;
        }
        MediaPlayerSingleton.getInstance().getMediaPlayer().reset();
    }


    @Override
    public void mediaLockScreen() {
        mediaSessionLockScreen = new MediaSessionCompat(this, TAG);
        mediaSessionLockScreen.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                playNext(true);
            }

            @Override
            public void onSkipToPrevious() {
                playPrev(true);
            }

            @Override
            public void onStop() {
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                seekTo((int) pos);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                if (mediaButtonReceiver != null) {
                    mediaButtonReceiver.onReceive(AudioPlayService.this, mediaButtonEvent);
                }
                return true;
            }
        });

        mediaSessionLockScreen.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        ComponentName buttonCom = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(buttonCom);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSessionLockScreen.setMediaButtonReceiver(pendingIntent);
        mediaSessionLockScreen.setActive(true);
    }


    @Override
    public void updateService(String updateServices) {
        Intent intent = new Intent(updateServices);
        if (updateServices.equals(PLAYSTATE_CHANGED) && intent.getAction().equals(PLAYSTATE_CHANGED)) {
            sendBroadcast(intent);
            /**
             * Called the notification here so as to handle pause & play toggle perfectly
             */
            NotificationHandler.buildNotification(this, PLAYSTATE_CHANGED);

        }
        else if (updateServices.equals(META_CHANGED) && intent.getAction().equals(META_CHANGED)) {
            Bundle bundle = new Bundle();
            bundle.putString(SONG_TITLE, getSongTitle());
            bundle.putString(SONG_ALBUM, getSongAlbumName());
            bundle.putLong(SONG_ALBUM_ID, getSongAlbumID());
            bundle.putString(SONG_ARTIST, getSongArtistName());
            bundle.putLong(SONG_ID, getSongId());
            bundle.putString(SONG_PATH, getSongData());
            bundle.putInt(SONG_TRACK_NUMBER, getSongNumber());
            bundle.putInt(POSITION_CHANGED, returnPos());
            intent.putExtras(bundle);
            Log.d(TAG, "broadcast song metadata");
            sendBroadcast(intent);

        }
        else if ((updateServices.equals(QUEUE_CHANGED) || updateServices.equals(ORDER_CHANGED) || updateServices.equals(ITEM_ADDED)) && (intent.getAction().equals(QUEUE_CHANGED) || intent.getAction().equals(ORDER_CHANGED)
                || intent.getAction().equals(ITEM_ADDED) || intent.getAction().equals(REPEAT_MODE_CHANGED))) {
            sendBroadcast(intent);
            saveState(true);
        }

        if (onPlayNotify) {
            if (!Extras.getInstance().hideNotify()) {
                NotificationHandler.buildNotification(AudioPlayService.this, updateServices);
            }
        }
        /*musicxWidget.notifyChange(this, updateservices);
        musicXwidget4x4.notifyChange(this, updateservices);
        musicXWidget5x5.notifyChange(this, updateservices);
        if (!Extras.getInstance().hideLockscreen()) {
            MediaSession.lockscreenMedia(getMediaSession(), MusicXService.this, updateservices);
        }*/
    }

    @Override
    public int getNextRepeatMode() {
        switch (repeatMode) {
            case NO_REPEAT:
                return REPEAT_ALL;
            case REPEAT_ALL:
                return REPEAT_CURRENT;
            case REPEAT_CURRENT:
                return NO_REPEAT;
        }
        return NO_REPEAT;
    }

    @Override
    public int returnPos() {
        return playList.indexOf(currentSong) != -1 && playList.indexOf(currentSong) < playList.size() ? playList.indexOf(currentSong) : -1;
    }

    @Override
    public void clearQueue() {
        if (playList.size() < 0) {
            return;
        }
        playList.clear();
    }

    @Override
    public void forceStop() {
        if (isPlaying()) {
            paused = false;
            isPlaying = false;
            fastPlay = false;
            updateService(PLAYSTATE_CHANGED);
            stopSelf();
        }
    }

    @Override
    public void receiverCleanup() {
        if (controlReceiver != null) {
            try {
                unregisterReceiver(controlReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            controlReceiver = null;
        }
        if (headsetListener != null) {
            try {
                unregisterReceiver(headsetListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            headsetListener = null;
        }
        if (mediaButtonReceiver != null) {
            try {
                unregisterReceiver(mediaButtonReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaButtonReceiver = null;
        }
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public int audioSession() {
        return MediaPlayerSingleton.getInstance().getMediaPlayer() != null ? MediaPlayerSingleton.getInstance().getMediaPlayer().getAudioSessionId() : 0;
    }

    @Override
    public void restorePos() {
        int seekPos = Extras.getInstance().getmPreferences().getInt(PLAYER_POS, 0);
        seekTo(seekPos);
    }

    @Override
    public int fadeDurationValue() {
        int fadeDuration = 0;
        String savedValue = Extras.getInstance().getFadeDuration();
        if (savedValue != null){
            switch (savedValue) {
                case "3":
                    fadeDuration = 3000;
                    return fadeDuration;
                case "6":
                    fadeDuration = 6000;
                    return fadeDuration;
                case "9":
                    fadeDuration = 9000;
                    return fadeDuration;
                case "12":
                    fadeDuration = 12000;
                    return fadeDuration;
                case "15":
                    fadeDuration = 15000;
                    return fadeDuration;
            }
        }
        return fadeDuration;
    }

    @Override
    public String getSongTitle() {
        if (currentSong != null) {
            return currentSong.getTitle();
        } else {
            return null;
        }
    }

    @Override
    public long getSongId() {
        if (currentSong != null) {
            return currentSong.getId();
        } else {
            return 0;
        }
    }

    @Override
    public String getSongAlbumName() {
        if (currentSong != null) {
            return currentSong.getAlbum();
        } else {
            return null;
        }
    }

    @Override
    public String getSongArtistName() {
        if (currentSong != null) {
            return currentSong.getArtist();
        } else {
            return null;
        }
    }

    @Override
    public String getSongData() {
        if (currentSong != null) {
            return currentSong.getmSongPath();
        } else {
            return null;
        }
    }

    @Override
    public long getSongAlbumID() {
        if (currentSong != null) {
            return currentSong.getAlbumId();
        } else {
            return 0;
        }
    }

    @Override
    public int getSongNumber() {
        if (currentSong != null) {
            return currentSong.getTrackNumber();
        } else {
            return 0;
        }
    }

    @Override
    public long getArtistID() {
        if (currentSong != null) {
            return currentSong.getArtistId();
        } else {
            return 0;
        }
    }

    @Override
    public String getYear() {
        if (currentSong != null) {
            return currentSong.getYear();
        } else {
            return null;
        }
    }

    //////////////////////// Play Interface -- END //////////////////////////////



    ///////////////////////// Other Methods /////////////////////////////////////

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return paused;
    }

    public List<Song> getPlayList() {
        return playList;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSessionLockScreen;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    private void widgetProgress() {
        handler.post(new ProgressRunnable(this));
    }

    private void removeProgress() {
        handler.removeCallbacks(new ProgressRunnable(this));
        handler.removeCallbacksAndMessages(null);
    }

    private void initializeMediaPlayer() {
        try {
            MediaPlayerSingleton.getInstance().getMediaPlayer();
            MediaPlayerSingleton.getInstance().getMediaPlayer().setOnPreparedListener(this);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setOnCompletionListener(this);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setOnErrorListener(this);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
            MediaPlayerSingleton.getInstance().getMediaPlayer().setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            Log.d(TAG, "MediaInitialize");
        } catch (Exception e) {
            Log.d(TAG, "initMedia_error", e);
        }
    }

    private void otherStuff() {
        recent = new CommonDatabase(this, Constants.RecentlyPlayed_TableName, true);
        queue = new CommonDatabase(this, Constants.Queue_TableName, true);
        currentSong = new Song();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        checkTelephonyState();
        headsetState();
        mediaLockScreen();
        restoreState();
        mediaButtonReceiver = new MediaButtonReceiver();

        if (controlReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            intentFilter.addAction(ACTION_PREVIOUS);
            intentFilter.addAction(ACTION_PAUSE);
            intentFilter.addAction(ACTION_PLAY);
            intentFilter.addAction(ACTION_TOGGLE);
            intentFilter.addAction(ACTION_NEXT);
            intentFilter.addAction(ACTION_CHANGE_STATE);
            intentFilter.addAction(ACTION_COMMAND1);
            intentFilter.addAction(ACTION_COMMAND);
            intentFilter.addAction(ACTION_FAV);
            registerReceiver(controlReceiver, intentFilter);
            registerReceiver(mediaButtonReceiver, intentFilter);
            Log.d(TAG, "Broadcast");
        }
        /*if (permissionManager.isAudioRecordGranted(this)) {
            int audioID = audioSession();
            Equalizers.initEq(audioID);
            BassBoosts.initBass(audioID);
            Virtualizers.initVirtualizer(audioID);
            Loud.initLoudnessEnhancer(audioID);
            Reverb.initReverb();
        } else {
            Log.d(TAG, "permission not granted");
        }
        favHelper = new FavHelper(this);
        handler = new Handler();
        helper = new Helper(this);
        if (permissionManager.isSystemAlertGranted(MusicXService.this)) {
            widgetPermission = true;
        } else {
            widgetPermission = false;
            Log.d(TAG, "Overlay permission not detected");
        }*/
    }

    public int getNoRepeat() {
        return NO_REPEAT;
    }

    public int getRepeatAll() {
        return REPEAT_ALL;
    }

    public int getRepeatCurrent() {
        return REPEAT_CURRENT;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int mode) {
        repeatMode = mode;
        updateService(REPEAT_MODE_CHANGED);
    }

    public boolean isShuffleEnabled() {
        return isShuffled;
    }

    public void setShuffleEnabled(boolean enable) {
        if (isShuffled != enable) {
            isShuffled = enable;
            if (enable) {
                shuffle();
                isShuffled = true;
            } else {
                clearQueue();
                playList.addAll(ogList);
            }
            updateService(ORDER_CHANGED);
        }
    }

    /*private void widgetCover() {
        int size = getResources().getDimensionPixelSize(R.dimen.cover_size);
        if (Extras.getInstance().getDownloadedArtwork()){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(MusicXService.this)
                            .load(helper.loadAlbumImage(getsongAlbumName()))
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(size, size)
                            .transform(new CropCircleTransformation(MusicXService.this))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    audioWidget.setAlbumArt(ArtworkUtils.drawableToBitmap(errorDrawable));
                                }

                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    audioWidget.setAlbumArt(resource);
                                }

                            });
                }
            });
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(MusicXService.this)
                            .load(ArtworkUtils.uri(getsongAlbumID()))
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(size, size)
                            .transform(new CropCircleTransformation(MusicXService.this))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    audioWidget.setAlbumArt(ArtworkUtils.drawableToBitmap(errorDrawable));
                                }

                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    audioWidget.setAlbumArt(resource);
                                }

                            });
                }
            });
        }
    }*/

    private void finalPlay(){
        if (!successfullyRetrievedAudioFocus()) {
            return;
        }
        onPlayNotify = true;
        updateService(PLAYSTATE_CHANGED);
        paused = false;
        isPlaying = true;
        Log.d(TAG, "Play");
        if (repeatMode == REPEAT_CURRENT && getDuration() > 2000 && getPlayerPos() >= getDuration() - 2000) {
            playNext(true);
        }
        if (widgetPermission){
            /*if (!Extras.getInstance().floatingWidget()) {
                if (!audioWidget.isShown()){
                    audioWidget.show(Extras.getInstance().getwidgetPositionX(), Extras.getInstance().getwidgetPositionY());
                }
                audioWidget.Start();
                trackingStart();
            } else {
                audioWidget.hide();
            }*/
        }
        recent.add(currentSong);
        recent.close();
    }

    private void finalPause(){
        Log.d(TAG, "Pause");
        paused = true;
        isPlaying = false;
        updateService(PLAYSTATE_CHANGED);
        if (widgetPermission){
            /*if (!Extras.getInstance().floatingWidget()) {
                trackingStop();
                audioWidget.Pause();
            } else {
                audioWidget.hide();
            }*/
        }
    }

    public void fadeOut(final MediaPlayer _player, final int duration) {
        final float deviceVolume = getDeviceVolume();
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private float time = duration;
            private float volume = 0.0f;

            @Override
            public void run() {
                // can call h again after work!
                time -= 100;
                volume = (deviceVolume * time) / duration;
                _player.setVolume(volume, volume);
                if (time > 0)
                    h.postDelayed(this, 100);
                else {
                    _player.pause();
                }
            }
        }, 100); // 1 second delay (takes millis)
    }

    public void fadeIn(final MediaPlayer _player, final int duration) {
        final float deviceVolume = getDeviceVolume();
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private float time = 0.0f;
            private float volume = 0.0f;

            @Override
            public void run() {
                _player.start();
                // can call h again after work!
                time += 100;
                volume = (deviceVolume * time) / duration;
                _player.setVolume(volume, volume);
                if (time < duration)
                    h.postDelayed(this, 100);
            }
        }, 100); // 1 second delay (takes millis)

    }

    public float getDeviceVolume() {
        int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (float) volumeLevel / maxVolume;
    }

    private boolean successfullyRetrievedAudioFocus() {
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    public void saveState(boolean yorno) {
        if (playList.size() > 0) {
            if (yorno) {
                queue.removeAll();
                queue.add(playList);
                queue.close();
            }
            songTitle = getSongTitle() == null ? SONG_TITLE : getSongTitle();
            songArtist = getSongTitle() == null ? SONG_ARTIST : getSongArtistName();
            albumID = getSongAlbumID() == 0 ? 0 : getSongAlbumID();
            songID = getSongId() == 0 ? 0 : getSongId();
            songPath = getSongData() == null ? SONG_PATH : getSongData();
            Log.d(TAG, "SavingData");
            Extras.getInstance().saveServices(true, returnPos(), repeatMode, isShuffled, songTitle, songArtist, songPath, songID, albumID);
        }
    }

    public void restoreState() {
        if (Extras.getInstance().getState(false)) {
            queueList = queue.readLimit(-1, null);
            queue.close();
            int restorePos = Extras.getInstance().getCurrentPos();
            String songName = Extras.getInstance().getSongTitle(songTitle);
            String songArtist = Extras.getInstance().getSongArtist(this.songArtist);
            long albumId = Extras.getInstance().getAlbumId(albumID);
            long songId = Extras.getInstance().getSongId(songID);
            String songPath = Extras.getInstance().getSongPath(this.songPath);
            repeatMode = Extras.getInstance().getRepeatMode(repeatMode);
            isShuffled = Extras.getInstance().getShuffle(isShuffled);

            if (queueList.size() > 0 || !isPlaying() && isPaused()) {
                smartPlaylist(queueList);
                setSongTitle(songName);
                setSongArtist(songArtist);
                setAlbumID(albumId);
                setSongID(songId);
                setSongPath(songPath);
                if (restorePos != -1 && restorePos < playList.size()) {
                    setDataPos(restorePos, false);
                }
                Log.d(TAG, "restoring data");
            } else {
                Log.d(TAG, "Failed to restore data");
            }
        }
    }

    private void removeNotification() {
        NotificationManagerCompat.from(this).cancel(notificationID);
    }

    ///////////////////////// Other Methods -- END /////////////////////////////////////



    ///////////////////////// Inner Classes /////////////////////////////////////

    private static class ProgressRunnable implements Runnable {

        private final WeakReference<AudioPlayService> audioPlayService;

        public ProgressRunnable(AudioPlayService service) {
            audioPlayService = new WeakReference<>(service);
        }

        @Override
        public void run () {
            AudioPlayService service = audioPlayService.get();
            if (service != null) {
                /*if (service.audioWidget != null && service.isPlaying() && service.returnPos() < service.playList.size()) {
                    service.audioWidget.Pos(service.getPlayerPos());
                }*/
            }
            handler.postDelayed(this,1000);
        }
    }

    //Binding services
    public class AudioPlayBinder extends Binder {

        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }

    //Control Receiver
    private class ControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                if (mLostAudioFocus) {
                    mLostAudioFocus = false;
                }
                pause();
                Log.d(TAG, "noisyAudio");
            }
            else if (intent.getAction().equals(ACTION_PLAY)) {
                play();
            }
            else if (intent.getAction().equals(ACTION_PAUSE)) {
                pause();
            }
            else if (intent.getAction().equals(ACTION_NEXT)) {
                playNext(true);
            }
            else if (intent.getAction().equals(ACTION_PREVIOUS)) {
                playPrev(true);
            }
            else if (intent.getAction().equals(ACTION_STOP)) {
                stopSelf();
            }
            else if (intent.getAction().equals(ACTION_TOGGLE)) {
                toggle();
            }
            /*else if (intent.getAction().equals(ACTION_CHANGE_STATE)) {
                if (widgetPermission) {
                    if (!Extras.getInstance().floatingWidget()) {
                        audioWidget.show(Extras.getInstance().getwidgetPositionX(), Extras.getInstance().getwidgetPositionY());
                    } else {
                        audioWidget.hide();
                    }
                }
            } else if (intent.getAction().equals(ACTION_FAV)) {
                if (favHelper.isFavorite(Extras.getInstance().getSongId(getsongId()))) {
                    favHelper.removeFromFavorites(Extras.getInstance().getSongId(getsongId()));
                    updateService(META_CHANGED);
                } else {
                    favHelper.addFavorite(Extras.getInstance().getSongId(getsongId()));
                    updateService(META_CHANGED);
                }
            } else if (intent.getAction().equals(ACTION_COMMAND)) {
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                musicxWidget.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
            } else if (intent.getAction().equals(ACTION_COMMAND1)) {
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                musicXwidget4x4.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
            } else if (intent.getAction().equals(ACTION_COMMAND2)) {
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                musicXWidget5x5.musicxWidgetUpdate(MusicXService.this, appWidgetIds, null);
            }*/
        }

    }

    ///////////////////////// Inner Classes -- END /////////////////////////////////////

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "AudioFocus Loss");
                if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                    pause();
                    //service.stopSelf();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                    MediaPlayerSingleton.getInstance().getMediaPlayer().setVolume(0.3f, 0.3f);
                    mIsDucked = true;
                }
                Log.d(TAG, "AudioFocus Loss Can Duck Transient");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "AudioFocus Loss Transient");
                if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                    pause();
                    mLostAudioFocus = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "AudioFocus Gain");
                if (mIsDucked) {
                    MediaPlayerSingleton.getInstance().getMediaPlayer().setVolume(1.0f, 1.0f);
                    mIsDucked = false;
                } else if (mLostAudioFocus) {
                    // If we temporarily lost the audio focus we can resume playback here
                    if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                        play();
                    }
                    mLostAudioFocus = false;
                }
                break;
            default:
                Log.d(TAG, "Unknown focus");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*Extras.getInstance().setWidgetPosition(100);
        audioWidget.cleanUp();
        audioWidget = null;
        Equalizers.EndEq();
        BassBoosts.EndBass();
        Virtualizers.EndVirtual();
        Loud.EndLoudnessEnhancer();
        Reverb.EndReverb();
        receiverCleanup();
        Extras.getInstance().eqSwitch(false);*/
        audioManager.abandonAudioFocus(this);
        removeProgress();
        fastPlay = false;
        isPlaying = false;
        paused = false;
        stopMediaPlayer();
        if (!Extras.getInstance().hideLockScreen()) {
            if (mediaSessionLockScreen != null) {
                mediaSessionLockScreen.release();
                mediaSessionLockScreen = null;
            }
        }
        /*Intent i = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        if (Helper.isActivityPresent(this, i)) {
            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSession());
            i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, this.getPackageName());
            sendBroadcast(i);
        } else {
            Log.d(TAG, "no activity found");
        }*/
        if (!Extras.getInstance().hideNotify()) {
            removeNotification();
        }
    }

}
