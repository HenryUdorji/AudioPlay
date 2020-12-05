package com.codemountain.audioplay.loaders;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.PermissionChecker;
import androidx.loader.content.AsyncTaskLoader;

import com.codemountain.audioplay.model.Playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlaylistLoaders extends AsyncTaskLoader<List<Playlist>> {

    private String sortOrder;
    private Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    private String[] projection = new String[]{
            MediaStore.Audio.Playlists.NAME,
            MediaStore.Audio.Playlists._ID
    };

    public PlaylistLoaders(Context context) {
        super(context);
    }

    @Override
    public List<Playlist> loadInBackground() {
        List<Playlist> playlistList = new ArrayList<>();
        if (PermissionChecker.checkCallingOrSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Cursor cursor = getContext().getContentResolver().query(uri, projection, "", null, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
                int nameCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
                do {
                    long id = cursor.getLong(idCol);
                    String name = cursor.getString(nameCol);

                    Playlist playlist = new Playlist();
                    playlist.setId(id);
                    playlist.setName(name);
                    playlistList.add(playlist);
                } while (cursor.moveToNext());
                cursor.close();
            }
            if (cursor == null) {
                return Collections.emptyList();
            }
            return playlistList;
        } else {
            return null;
        }
    }

    public void setSortOrder(String orderBy) {
        sortOrder = orderBy;
    }

}
