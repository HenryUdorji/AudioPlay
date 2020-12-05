package com.codemountain.audioplay.service;

import android.media.MediaPlayer;


public class MediaPlayerSingleton {

    private static MediaPlayerSingleton instance = null;

    private MediaPlayer sMediaPlayer;

    protected MediaPlayerSingleton() {
        sMediaPlayer = new MediaPlayer();
    }

    public static MediaPlayerSingleton getInstance() {
        if (instance == null) {
            instance = new MediaPlayerSingleton();
        }
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return sMediaPlayer;
    }
}
