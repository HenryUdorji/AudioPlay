package com.codemountain.audioplay.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.codemountain.audioplay.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongLoader {


    private static String sortOrder = null;

    public static List<Song> getAllPlaylistSongs(Context context, long playlist_ID){
        List<Song> playlist = new ArrayList<>();
        String sortOrder = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
        String selection = "is_music = 1 and title != '' and playlist_id = " + playlist_ID;
        final String[] sProjection = {
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
                MediaStore.Audio.Playlists.Members.ARTIST_ID,
                MediaStore.Audio.Playlists.Members.TRACK,
                MediaStore.Audio.Playlists.Members.DATA
        };

        long mPlaylistId;
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_ID);


        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            if (idCol == -1) {
                idCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
            }
            int titleCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
            int artistCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST);
            int albumCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);
            int albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID);
            int trackCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TRACK);
            int dataCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA);
            do {
                long id = cursor.getLong(idCol);
                String title = cursor.getString(titleCol);
                String artist = cursor.getString(artistCol);
                String album = cursor.getString(albumCol);
                long albumId = cursor.getLong(albumIdCol);
                int track = cursor.getInt(trackCol);
                String mSongPath = cursor.getString(dataCol);
                Song song = new Song();
                    /*
                     Setup metadata of playlist
                    */
                song.setAlbum(album);
                song.setmSongPath(mSongPath);
                song.setArtist(artist);
                song.setId(id);
                song.setAlbumId(albumId);
                song.setTrackNumber(track);
                song.setTitle(title);
                playlist.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return playlist;

    }

    public void setSortOrder(String orderBy) {
        sortOrder = orderBy;
    }
}
