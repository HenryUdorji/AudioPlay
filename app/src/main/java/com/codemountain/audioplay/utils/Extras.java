package com.codemountain.audioplay.utils;

import android.content.SharedPreferences;
import android.os.Environment;

import com.codemountain.audioplay.AudioPlayerApplication;
import com.codemountain.audioplay.loaders.SortOrder;
import com.codemountain.audioplay.model.Song;

import static com.codemountain.audioplay.utils.Constants.ALBUM_SORT_ORDER;
import static com.codemountain.audioplay.utils.Constants.ARTIST_ALBUM_SORT;
import static com.codemountain.audioplay.utils.Constants.ARTIST_SORT_ORDER;
import static com.codemountain.audioplay.utils.Constants.AUDIO_FILTER;
import static com.codemountain.audioplay.utils.Constants.CURRENTPOS;
import static com.codemountain.audioplay.utils.Constants.FADE_IN_OUT_DURATION;
import static com.codemountain.audioplay.utils.Constants.FADE_SWITCH;
import static com.codemountain.audioplay.utils.Constants.FADE_TIMER;
import static com.codemountain.audioplay.utils.Constants.FADE_TRACK;
import static com.codemountain.audioplay.utils.Constants.FOLDER_PATH;
import static com.codemountain.audioplay.utils.Constants.FloatingView;
import static com.codemountain.audioplay.utils.Constants.HD_ARTWORK;
import static com.codemountain.audioplay.utils.Constants.HIDE_LOCKSCREEEN;
import static com.codemountain.audioplay.utils.Constants.HIDE_NOTIFY;
import static com.codemountain.audioplay.utils.Constants.KEY_POSITION_X;
import static com.codemountain.audioplay.utils.Constants.KEY_POSITION_Y;
import static com.codemountain.audioplay.utils.Constants.LANGUAGE;
import static com.codemountain.audioplay.utils.Constants.PLAYER_POS;
import static com.codemountain.audioplay.utils.Constants.PLAYINGSTATE;
import static com.codemountain.audioplay.utils.Constants.PLAYLIST_SORT_ORDER;
import static com.codemountain.audioplay.utils.Constants.PREF_AUTO_PAUSE;
import static com.codemountain.audioplay.utils.Constants.REPEATMODE;
import static com.codemountain.audioplay.utils.Constants.SETTINGS_TRACK;
import static com.codemountain.audioplay.utils.Constants.SHUFFLEMODE;
import static com.codemountain.audioplay.utils.Constants.SONG_ALBUM;
import static com.codemountain.audioplay.utils.Constants.SONG_ALBUM_ID;
import static com.codemountain.audioplay.utils.Constants.SONG_ARTIST;
import static com.codemountain.audioplay.utils.Constants.SONG_ID;
import static com.codemountain.audioplay.utils.Constants.SONG_PATH;
import static com.codemountain.audioplay.utils.Constants.SONG_SORT_ORDER;
import static com.codemountain.audioplay.utils.Constants.SONG_TITLE;
import static com.codemountain.audioplay.utils.Constants.SONG_TRACK_NUMBER;
import static com.codemountain.audioplay.utils.Constants.SONG_YEAR;
import static com.codemountain.audioplay.utils.Constants.SaveHeadset;
import static com.codemountain.audioplay.utils.Constants.SaveTelephony;
import static com.codemountain.audioplay.utils.Constants.THEME_SWITCH;
import static com.codemountain.audioplay.utils.Constants.TIMER_DURATION;
import static com.codemountain.audioplay.utils.Constants.VISUALIZER;

public class Extras {

    private static Extras instance;

    public Extras() {
    }

    public static Extras init() {
        if (instance == null){
            instance = new Extras();
        }
        return instance;
    }

    public static Extras getInstance(){
        return instance;
    }

    public SharedPreferences getmPreferences() {
        return AudioPlayerApplication.getmPreferences();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = AudioPlayerApplication.getmPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = AudioPlayerApplication.getmPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public String getAudioFilter(){
        return AudioPlayerApplication.getmPreferences().getString(AUDIO_FILTER, "0");
    }

    ////////////////////////// Preferences /////////////////////////

    public void setWidgetPosition(int pos) {
        SharedPreferences.Editor sharedEditor = AudioPlayerApplication.getmPreferences().edit();
        sharedEditor.putInt(KEY_POSITION_X, pos);
        sharedEditor.putInt(KEY_POSITION_Y, pos);
        sharedEditor.commit();

    }

    public boolean getHdArtwork() {
        return AudioPlayerApplication.getmPreferences().getBoolean(HD_ARTWORK, false);
    }

    public boolean floatingWidget() {
        return AudioPlayerApplication.getmPreferences().getBoolean(FloatingView, false);
    }

    public String getLanguageValue() {
        return AudioPlayerApplication.getmPreferences().getString(LANGUAGE, "English");
    }

    public String getVisualizerValue() {
        return AudioPlayerApplication.getmPreferences().getString(VISUALIZER, "wave");
    }

    public boolean getSwitchTheme() {
        return AudioPlayerApplication.getmPreferences().getBoolean(THEME_SWITCH, false);
    }

    public boolean getSwitchTimer(){
        return AudioPlayerApplication.getmPreferences().getBoolean(FADE_TIMER, false);
    }

    public String getTimerDuration() {
        return AudioPlayerApplication.getmPreferences().getString(TIMER_DURATION, "10");
    }

    public boolean getSwitchFade(){
        return AudioPlayerApplication.getmPreferences().getBoolean(FADE_SWITCH, false);
    }

    public String getFadeDuration() {
        return AudioPlayerApplication.getmPreferences().getString(FADE_IN_OUT_DURATION, "0");
    }

    public boolean getFadeTrack() {
        return AudioPlayerApplication.getmPreferences().getBoolean(FADE_TRACK, false);
    }

    public boolean headsetConfig() {
        return AudioPlayerApplication.getmPreferences().getBoolean(SaveHeadset, true);
    }

    public boolean phoneCallConfig() {
        return AudioPlayerApplication.getmPreferences().getBoolean(SaveTelephony, true);
    }

    public boolean hideNotify() {
        return AudioPlayerApplication.getmPreferences().getBoolean(HIDE_NOTIFY, false);
    }

    public boolean hideLockScreen() {
        return AudioPlayerApplication.getmPreferences().getBoolean(HIDE_LOCKSCREEEN, false);
    }

    //////////////////// Sorting ////////////////////////
    public String getSongSortOrder() {
        return AudioPlayerApplication.getmPreferences().getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(String value) {
        putString(SONG_SORT_ORDER, value);
    }

    public String getArtistSortOrder() {
        return AudioPlayerApplication.getmPreferences().getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(String value) {
        putString(ARTIST_SORT_ORDER, value);
    }

    public String getAlbumSortOrder() {
        return AudioPlayerApplication.getmPreferences().getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(String value) {
        putString(ALBUM_SORT_ORDER, value);
    }

    public void setArtistAlbumSortOrder(String value) {
        putString(ARTIST_ALBUM_SORT, value);
    }

    public String getArtistAlbumSort() {
        return AudioPlayerApplication.getmPreferences().getString(ARTIST_ALBUM_SORT, SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z);
    }

    public void setPlaylistSortOrder(String value) {
        putString(PLAYLIST_SORT_ORDER, value);
    }

    public String getPlaylistSort() {
        return AudioPlayerApplication.getmPreferences().getString(PLAYLIST_SORT_ORDER, SortOrder.PlaylistSortOrder.PLAYLIST_A_Z);
    }


    ///////////////////// AudioPlay Service pref /////////////////////////

    public void saveServices(boolean saveState, int pos, int repeat, boolean shuffle, String songTitle, String songArtist, String path, long songID, long albumID) {
        SharedPreferences.Editor editor = AudioPlayerApplication.getmPreferences().edit();
        editor.putInt(CURRENTPOS, pos);
        editor.putInt(REPEATMODE, repeat);
        editor.putBoolean(SHUFFLEMODE, shuffle);
        editor.putBoolean(PLAYINGSTATE, saveState);
        editor.putString(SONG_TITLE, songTitle);
        editor.putString(SONG_ARTIST, songArtist);
        editor.putLong(SONG_ID, songID);
        editor.putLong(SONG_ALBUM_ID, albumID);
        editor.putString(SONG_PATH, path);
        editor.apply();
    }

    public void saveSeekServices(int playerPos){
        SharedPreferences.Editor editor = AudioPlayerApplication.getmPreferences().edit();
        editor.putInt(PLAYER_POS, playerPos);
        editor.commit();
    }

    public int getCurrentPos() {
        return AudioPlayerApplication.getmPreferences().getInt(CURRENTPOS, 0);
    }

    public int getRepeatMode(int repeatMode) {
        return AudioPlayerApplication.getmPreferences().getInt(REPEATMODE, repeatMode);
    }

    public boolean getShuffle(boolean shuffle) {
        return AudioPlayerApplication.getmPreferences().getBoolean(SHUFFLEMODE, shuffle);
    }

    public boolean getState(boolean state) {
        return AudioPlayerApplication.getmPreferences().getBoolean(PLAYINGSTATE, state);
    }

    public String getSongTitle(String songTitle) {
        return AudioPlayerApplication.getmPreferences().getString(SONG_TITLE, songTitle);
    }

    public String getSongArtist(String artist) {
        return AudioPlayerApplication.getmPreferences().getString(SONG_ARTIST, artist);
    }

    public long getSongId(long id) {
        return AudioPlayerApplication.getmPreferences().getLong(SONG_ID, id);
    }

    public long getAlbumId(long albumId) {
        return AudioPlayerApplication.getmPreferences().getLong(SONG_ALBUM_ID, albumId);
    }

    public String getSongPath(String path) {
        return AudioPlayerApplication.getmPreferences().getString(SONG_PATH, path);
    }

    public boolean getHeadset() {
        return Extras.getInstance().getmPreferences().getBoolean(PREF_AUTO_PAUSE, false);
    }

    //////////////////// Save Metadata pref ////////////////////////

    public void saveMetaData(Song song) {
        SharedPreferences.Editor editor = AudioPlayerApplication.getMetaData().edit();
        editor.putString(Constants.SONG_TITLE, song.getTitle());
        editor.putString(Constants.SONG_ARTIST, song.getArtist());
        editor.putString(SONG_ALBUM, song.getAlbum());
        editor.putString(SONG_YEAR, song.getYear());
        editor.putInt(SONG_TRACK_NUMBER, song.getTrackNumber());
        editor.putLong(Constants.SONG_ALBUM_ID, song.getAlbumId());
        editor.putString(Constants.SONG_PATH, song.getmSongPath());
        editor.putLong(Constants.SONG_ID, song.getId());
        editor.apply();
    }

    public String getTitle() {
        return AudioPlayerApplication.getMetaData().getString(SONG_TITLE, null);
    }

    public String getArtist() {
        return AudioPlayerApplication.getMetaData().getString(SONG_ARTIST, null);
    }

    public String getAlbum() {
        return AudioPlayerApplication.getMetaData().getString(SONG_ALBUM, null);
    }

    public String getPath() {
        return AudioPlayerApplication.getMetaData().getString(SONG_PATH, null);
    }

    public int getNo() {
        return AudioPlayerApplication.getMetaData().getInt(SONG_TRACK_NUMBER, 0);
    }

    public long getAlbumID() {
        return AudioPlayerApplication.getMetaData().getLong(SONG_ALBUM_ID, 0);
    }

    public long getId() {
        return AudioPlayerApplication.getMetaData().getLong(SONG_ID, 0);
    }

    public String getYear() {
        return AudioPlayerApplication.getMetaData().getString(SONG_YEAR, null);
    }

    ////////////////// folder pref //////////////////

    public String getFolderPath() {
        return AudioPlayerApplication.getmPreferences().getString(FOLDER_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
    }

    /////////////// Settings Permission /////////////

    public boolean getSettings(){
        return AudioPlayerApplication.getmPreferences().getBoolean(SETTINGS_TRACK, false);
    }


}
