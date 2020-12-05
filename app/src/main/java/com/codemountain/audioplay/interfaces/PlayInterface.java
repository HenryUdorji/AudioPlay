package com.codemountain.audioplay.interfaces;


import com.codemountain.audioplay.model.Song;

import java.util.List;


public interface PlayInterface {

    /*
    *@playing properties
    */
    void play();

    void pause();

    void playNext(boolean value);

    void playPrev(boolean value);

    void shuffle();

    void toggle();

    void returnHome();

    void forwardPlayingView();

    int getDuration();

    int getPlayerPos();

    void seekTo(int seek);

    void setPlaylist(List<Song> songList, int pos, boolean play);

    void smartPlaylist(List<Song> smartPlaylist);

    void trackingStart();

    void trackingStop();

    void startCurrentTrack(Song song);

    void setPlaylistAndShuffle(List<Song> songList, boolean play);

    void addToQueue(Song song);

    void setAsNextTrack(Song song);

    void fastPlay(boolean torf, Song song);

    int getNextPos(boolean yorno);

    int getPrevPos(boolean yorno);

    void checkTelephonyState();

    void headsetState();

    void stopMediaPlayer();

    void mediaLockScreen();

    void updateService(String updateServices);

    int getNextRepeatMode();

    void setDataPos(int pos, boolean play);

    int returnPos();

    void clearQueue();

    void forceStop();

    void receiverCleanup();

    int audioSession();

    void restorePos();

    int fadeDurationValue();

    /*
    *@return song MetaData
    */
    String getSongTitle();

    long getSongId();

    String getSongAlbumName();

    String getSongArtistName();

    String getSongData();

    long getSongAlbumID();

    int getSongNumber();

    long getArtistID();

    String getYear();


}
