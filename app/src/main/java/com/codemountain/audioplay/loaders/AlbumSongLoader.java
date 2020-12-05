package com.codemountain.audioplay.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.codemountain.audioplay.model.Song;

import java.util.ArrayList;

public class AlbumSongLoader {


    private static String sortOrder = null;

    public static ArrayList<Song> getAllAlbumSongs(Context context, long album_ID){
        ArrayList<Song> listAlbumSongs = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = "is_music = 1 and title != '' and album_id = " + album_ID;
        String[] projection = {
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

        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, sortOrder);
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
                listAlbumSongs.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listAlbumSongs;
    }

    public void setSortOrder(String orderBy) {
        sortOrder = orderBy;
    }
}
