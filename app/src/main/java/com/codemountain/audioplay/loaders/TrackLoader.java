package com.codemountain.audioplay.loaders;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.core.content.PermissionChecker;
import androidx.loader.content.AsyncTaskLoader;

import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackLoader extends AsyncTaskLoader<List<Song>> {

    private String where = null;
    private String sortOrder = null;
    private String[] selectionArgs = null;
    private String mFilter = null;

    private String[] dataCol = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.COMPOSER
    };

    public TrackLoader(Context context) {
        super(context);
    }

    @Override
    public List<Song> loadInBackground() {

        List<Song> songList = new ArrayList<>();

        if (PermissionChecker.checkCallingOrSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, dataCol, where, selectionArgs, sortOrder);

            if (cursor != null && cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int trackCol = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
                int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int yearCol = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
                int artistIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
                do {
                    long id = cursor.getLong(idCol);
                    String title = cursor.getString(titleCol);
                    String artist = cursor.getString(artistCol);
                    String album = cursor.getString(albumCol);
                    long albumId = cursor.getLong(albumIdCol);
                    int track = cursor.getInt(trackCol);
                    String mSongPath = cursor.getString(dataCol);
                    long duration = cursor.getLong(durationCol);
                    String year = cursor.getString(yearCol);
                    long artistId = cursor.getLong(artistIdCol);

                    Song song = new Song();
                    /*
                    Setup metadata of songs
                     */
                    song.setAlbum(album);
                    song.setmSongPath(mSongPath);
                    song.setArtist(artist);
                    song.setId(id);
                    song.setAlbumId(albumId);
                    song.setTrackNumber(track);
                    song.setTitle(title);
                    song.setYear(year);
                    song.setArtistId(artistId);
                    song.setDuration(duration);
                    songList.add(song);
                } while (cursor.moveToNext());
                cursor.close();
            }
            if (cursor == null) {
                return Collections.emptyList();
            }
            return songList;
        } else {
            return null;
        }
    }

    public void setSortOrder(String orderBy) {
        sortOrder = orderBy;
    }

    public void filterAlbumSong(String filter, String[] args) {
        where = filter + " AND " + MediaStore.Audio.Media.DURATION + ">=" + Helper.filterAudio(); // list song greater than 30sec duration
        selectionArgs = args;
    }

    public String getFilter() {
        return mFilter;
    }

    public void setFilter(String filter) {
        mFilter = filter;
    }

}
