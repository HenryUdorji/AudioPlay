package com.codemountain.audioplay.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codemountain.audioplay.interfaces.DefaultColumn;
import com.codemountain.audioplay.model.Artist;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CommonDatabase extends SQLiteOpenHelper implements DefaultColumn {


    public String tableName;
    private SQLiteDatabase sqLiteDatabase;
    private List<Song> songList;
    private List<Artist> artistList;
    private boolean torf;

    public CommonDatabase(Context context, String name, boolean torf) {
        super(context, name, null, Constants.DbVersion);
        this.tableName = name;
        this.torf = torf;
        songList = new ArrayList<>();
        artistList = new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constants.DefaultColumn(tableName, torf));
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(sqLiteDatabase);
    }

    public void add(Song song) {
        sqLiteDatabase = getWritableDatabase();
        try {
            addSongMetaData(sqLiteDatabase, song);
        } finally {
            sqLiteDatabase.close();
        }
    }

    private void addSongMetaData(SQLiteDatabase sqLiteDatabase, Song song) {
        ContentValues values = new ContentValues();
        values.put(SongId, song.getId());
        values.put(SongTitle, song.getTitle());
        values.put(SongAlbum, song.getAlbum());
        values.put(SongArtist, song.getArtist());
        values.put(SongNumber, song.getTrackNumber());
        values.put(SongPath, song.getmSongPath());
        values.put(SongAlbumId, song.getAlbumId());
        sqLiteDatabase.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }


    private void addArtistMetaData(SQLiteDatabase sqLiteDatabase, Artist artist) {
        ContentValues values = new ContentValues();
        values.put(ArtistId, artist.getId());
        values.put(ArtistTitle, artist.getName());
        values.put(ArtistAlbumCount, artist.getAlbumCount());
        values.put(ArtistTrackCount, artist.getTrackCount());
        sqLiteDatabase.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }


    public void removeAll() {
        sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(tableName, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public void addArtist(List<Artist> artists) {
        sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            for (Artist artist : artists) {
                addArtistMetaData(sqLiteDatabase, artist);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public void add(List<Song> songList) {
        sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            for (Song song : songList) {
                addSongMetaData(sqLiteDatabase, song);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public List<Artist> readArtist(){
        sqLiteDatabase = getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(getTableName(), null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(ArtistId);
                int nameCol = cursor.getColumnIndex(ArtistTitle);
                int albumsNbCol = cursor.getColumnIndex(ArtistAlbumCount);
                int tracksNbCol = cursor.getColumnIndex(ArtistTrackCount);
                do {
                    long id = cursor.getLong(idCol);
                    String artistName = cursor.getString(nameCol);
                    int albumCount = cursor.getInt(albumsNbCol);
                    int trackCount = cursor.getInt(tracksNbCol);
                    artistList.add(new Artist(id, artistName, albumCount, trackCount));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }finally {
            sqLiteDatabase.close();
        }
        return artistList;
    }

    public Cursor check(SQLiteDatabase sqLiteDatabase, int limit, String sortOrder) {
        Cursor cursor;
        if (limit > 0) {
            cursor = sqLiteDatabase.query(getTableName(), null, null, null, null, null, sortOrder, String.valueOf(limit));
        } else {
            cursor = sqLiteDatabase.query(getTableName(), null, null, null, null, null, sortOrder);
        }
        return cursor;
    }

    public List<Song> readLimit(int limit, String sortOrder) {
        sqLiteDatabase = getReadableDatabase();
        try {
            Cursor cursor = check(sqLiteDatabase, limit, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {

                int idCol = cursor.getColumnIndex(SongId);
                int titleCol = cursor.getColumnIndex(SongTitle);
                int artistCol = cursor.getColumnIndex(SongArtist);
                int albumCol = cursor.getColumnIndex(SongAlbum);
                int albumIdCol = cursor.getColumnIndex(SongAlbumId);
                int trackCol = cursor.getColumnIndex(SongNumber);
                int datacol = cursor.getColumnIndex(SongPath);
                do {
                    /**
                     * @return songs metadata
                     */
                    long id = cursor.getLong(idCol);
                    String title = cursor.getString(titleCol);
                    String artist = cursor.getString(artistCol);
                    String album = cursor.getString(albumCol);
                    long albumId = cursor.getLong(albumIdCol);
                    int track = cursor.getInt(trackCol);
                    String mSongPath = cursor.getString(datacol);

                    Song song = new Song();

                    song.setAlbum(album);
                    song.setmSongPath(mSongPath);
                    song.setArtist(artist);
                    song.setId(id);
                    song.setAlbumId(albumId);
                    song.setTrackNumber(track);
                    song.setTitle(title);
                    songList.add(song);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }finally {
            sqLiteDatabase.close();
        }
        return songList;
    }

    public boolean exists(long songId) {
        boolean result = false;
        sqLiteDatabase = getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(getTableName(), null, SongId + "= ?", new String[]{String.valueOf(songId)}, null, null, null, "1");
            if (cursor != null && cursor.moveToFirst()) {
                result = true;
            }
            if (cursor != null) {
                cursor.close();
            }
        }finally {
            sqLiteDatabase.close();
        }
        return result;
    }

    public void delete(long songId) {
        sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.delete(getTableName(), SongId + "= ?", new String[]{String.valueOf(songId)});
        } finally {
            sqLiteDatabase.close();
        }
    }

    public String getTableName() {
        return tableName;
    }
}