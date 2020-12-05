package com.codemountain.audioplay.loaders;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.core.content.PermissionChecker;
import androidx.loader.content.AsyncTaskLoader;

import com.codemountain.audioplay.model.Artist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ArtistLoader extends AsyncTaskLoader<List<Artist>> {

    private String where;
    private static String sortOrder;
    private String[] selectionArgs = null;
    private static Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

    public ArtistLoader(Context context) {
        super(context);
    }

    @Override
    public List<Artist> loadInBackground() {

        List<Artist> artistList = new ArrayList<>();

        if (PermissionChecker.checkCallingOrSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {

                int idCol = cursor.getColumnIndex(BaseColumns._ID);
                int nameCol = cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST);
                int albumsNbCol = cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS);
                int tracksNbCol = cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS);
                do {
                    long id = cursor.getLong(idCol);
                    String artistName = cursor.getString(nameCol);
                    int albumCount = cursor.getInt(albumsNbCol);
                    int trackCount = cursor.getInt(tracksNbCol);
                    artistList.add(new Artist(id, artistName, albumCount, trackCount));

                } while (cursor.moveToNext());
                cursor.close();
            }
            if (cursor == null) {
                return Collections.emptyList();
            }
            return artistList;
        } else {
            return null;
        }

    }

    public void setSortOrder(String orderBy) {
        sortOrder = orderBy;
    }

    public Artist getSingleArtist(Context context, long ID){
        return artist(makeCursor(context, "_id = ?", new String[]{String.valueOf(ID)}));
    }

    //Method get a single album from the cursor

    private Artist artist(Cursor musicCursor) {
        Artist artist = new Artist();

            if (musicCursor != null && musicCursor.moveToFirst()) {

                int idCol = musicCursor.getColumnIndex(BaseColumns._ID);
                int nameCol = musicCursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST);
                int albumsNbCol = musicCursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS);
                int tracksNbCol = musicCursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS);
                do {
                    long id = musicCursor.getLong(idCol);
                    String artistName = musicCursor.getString(nameCol);
                    int albumCount = musicCursor.getInt(albumsNbCol);
                    int trackCount = musicCursor.getInt(tracksNbCol);

                    artist.setAlbumCount(albumCount);
                    artist.setId(id);
                    artist.setTrackCount(trackCount);
                    artist.setName(artistName);

                } while (musicCursor.moveToNext());
                musicCursor.close();
            }
        if (musicCursor != null){
            musicCursor.close();
        }
        return artist;
    }

    public static Cursor makeCursor(Context context, String selection, String[] selectionArgs){
        return context.getContentResolver().query(uri, null, selection, selectionArgs, sortOrder);
    }
}
