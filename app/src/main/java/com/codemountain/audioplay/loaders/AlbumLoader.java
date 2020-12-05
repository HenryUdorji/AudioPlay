package com.codemountain.audioplay.loaders;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.core.content.PermissionChecker;
import androidx.loader.content.AsyncTaskLoader;

import com.codemountain.audioplay.model.Album;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AlbumLoader extends AsyncTaskLoader<List<Album>> {

    private String where;
    private static String sortOrder;
    private String[] selectionArgs = null;
    private static Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;


    public AlbumLoader(Context context) {
        super(context);
    }

    @Override
    public List<Album> loadInBackground() {
        ArrayList<Album> albums = new ArrayList<>();
        if (PermissionChecker.checkCallingOrSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Cursor musicCursor = getContext().getContentResolver().query(uri, null, where, selectionArgs, sortOrder);
            if (musicCursor != null && musicCursor.moveToFirst()) {

                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM);
                int idColumn = musicCursor.getColumnIndex(BaseColumns._ID);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST);
                int numOfSongsColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS);
                int albumFirstColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR);

                do {
                    String albumName = musicCursor.getString(titleColumn);
                    long albumId = musicCursor.getLong(idColumn);
                    String artistName = musicCursor.getString(artistColumn);
                    int year = musicCursor.getInt(albumFirstColumn);
                    int no = musicCursor.getInt(numOfSongsColumn);
                    Album album = new Album();

                    /**
                     * Setting Album Metadata
                     */
                    album.setArtistName(artistName);
                    album.setAlbumName(albumName);
                    album.setId(albumId);
                    album.setTrackCount(no);
                    album.setYear(year);
                    albums.add(album);

                } while (musicCursor.moveToNext());
                musicCursor.close();
            }
            if (musicCursor == null) {
                return Collections.emptyList();
            }
            return albums;
        } else {
            return null;
        }
    }

    public void setSortOrder(String orderBy) {
        sortOrder = orderBy;
    }

    public void filterArtistSong(String filter, String[] args) {
        where = filter;
        selectionArgs = args;
    }


    public Album getSingleAlbum(Context context, long ID){
        return album(makeCursor(context, "_id = ?", new String[]{String.valueOf(ID)}));
    }

    //Method get a single album from the cursor
    private Album album(Cursor musicCursor) {
        Album album = new Album();
        if (musicCursor.moveToFirst() && musicCursor != null){

            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM);
            int idColumn = musicCursor.getColumnIndex(BaseColumns._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST);
            int numOfSongsColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS);
            int albumFirstColumn = musicCursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR);

            do {
                String albumName = musicCursor.getString(titleColumn);
                long albumId = musicCursor.getLong(idColumn);
                String artistName = musicCursor.getString(artistColumn);
                int year = musicCursor.getInt(albumFirstColumn);
                int no = musicCursor.getInt(numOfSongsColumn);

                album.setArtistName(artistName);
                album.setAlbumName(albumName);
                album.setId(albumId);
                album.setTrackCount(no);
                album.setYear(year);
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        if (musicCursor != null){
            musicCursor.close();
        }
        return album;
    }

    public static Cursor makeCursor(Context context, String selection, String[] selectionArgs){
        return context.getContentResolver().query(uri, null, selection, selectionArgs, sortOrder);
    }

}
