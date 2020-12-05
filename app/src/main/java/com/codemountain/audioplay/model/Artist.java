package com.codemountain.audioplay.model;

import android.provider.MediaStore;

public class Artist {

    private long id;
    private String name;
    private int albumCount;
    private int trackCount;

    public Artist(long id, String name, int albumCount, int trackCount) {
        super();
        this.id = id;
        this.name = name == null ? MediaStore.UNKNOWN_STRING : name;
        this.albumCount = albumCount;
        this.trackCount = trackCount;
    }

    public Artist() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }
}
