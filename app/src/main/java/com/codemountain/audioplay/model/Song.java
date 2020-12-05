package com.codemountain.audioplay.model;

import android.net.Uri;


public class Song {

    private long id;
    private String title;
    private String artist;
    private String album;
    private int trackNumber;
    private long albumId;
    private String genre;
    private String mSongPath;
    private boolean isSelected = false;
    private String year;
    private String lyrics;
    private Uri albumArt;
    private long artistId;
    private long duration;



    /*
    Getter
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getArtistId() {
        return artistId;
    }

    /*
    Setter
     */

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Uri getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Uri albumArt) {
        this.albumArt = albumArt;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getmSongPath() {
        return mSongPath;
    }

    public void setmSongPath(String mSongPath) {
        this.mSongPath = mSongPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}


