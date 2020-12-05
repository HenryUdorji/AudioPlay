package com.codemountain.audioplay.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.codemountain.audioplay.R;
import com.codemountain.audioplay.database.SaveQueueDatabase;
import com.codemountain.audioplay.fragments.PlaylistPickerFragment;
import com.codemountain.audioplay.interfaces.RefreshPlaylist;
import com.codemountain.audioplay.loaders.DefaultSongLoader;
import com.codemountain.audioplay.model.Album;
import com.codemountain.audioplay.model.Artist;
import com.codemountain.audioplay.model.Folder;
import com.codemountain.audioplay.model.Playlist;
import com.codemountain.audioplay.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.codemountain.audioplay.utils.Constants.Five;
import static com.codemountain.audioplay.utils.Constants.Four;
import static com.codemountain.audioplay.utils.Constants.One;
import static com.codemountain.audioplay.utils.Constants.Three;
import static com.codemountain.audioplay.utils.Constants.Two;
import static com.codemountain.audioplay.utils.Constants.Zero;
import static com.codemountain.audioplay.utils.Constants.fileExtensions;

public class Helper {

    private Context context;

    public Helper(Context context) {
        this.context = context;
    }

    //Filter AudioFile Length
    public static String filterAudio(){
        String filterAudio = "15000";
        switch (Extras.getInstance().getAudioFilter()){
            case Zero:
                filterAudio = "30000"; //30sec
                break;
            case One:
                filterAudio = "60000"; //1min
                break;
            case Two:
                filterAudio = "120000"; //2min
                break;
            case Three:
                filterAudio = "180000"; //3min
                break;
            case Four:
                filterAudio = "240000"; //4min
                break;
            case Five:
                filterAudio = "300000"; //5min
                break;
        }
        return filterAudio;
    }

    //////////////////// Filter for Search Query ////////////////////////

    //Filter Song List
    public static List<Song> filterSong(List<Song> songList, String query) {
        query = query.toLowerCase().trim();
        final List<Song> filterSongList = new ArrayList<>();
        for (Song song : songList) {
            final String songTitle = song.getTitle().toLowerCase().trim();
            final String artistName = song.getArtist().toLowerCase().trim();
            if (songTitle.contains(query) || artistName.contains(query)) {
                Log.e("Helper", "Query --> search song");
                filterSongList.add(song);
            }
        }
        return filterSongList;
    }

    //Filter Artist ArrayList
    public static List<Artist> filterArtist(List<Artist> artistList, String query) {
        query = query.toLowerCase().trim();
        final List<Artist> filterArtistList = new ArrayList<>();
        for (Artist artist : artistList) {
            final String text = artist.getName().toLowerCase().trim();
            if (text.contains(query)) {
                Log.e("Helper", "Query --> search artist");
                filterArtistList.add(artist);
            }
        }
        return filterArtistList;
    }

    //Filter Album
    public static List<Album> filterAlbum(List<Album> albumList, String query) {
        query = query.toLowerCase();
        final List<Album> filterAlbumList = new ArrayList<>();
        for (Album album : albumList) {
            final String text = album.getAlbumName().toLowerCase().trim();
            final String text2 = album.getArtistName().toLowerCase().trim();
            if (text.contains(query) || text2.contains(query)) {
                Log.e("Helper", "Query --> search album");
                filterAlbumList.add(album);
            }
        }
        return filterAlbumList;
    }

    //Filter Playlist
    public static List<Playlist> filterPlaylist(List<Playlist> playlists, String query) {
        query = query.toLowerCase();
        final List<Playlist> filterPlaylist = new ArrayList<>();
        for (Playlist playlist : playlists) {
            final String text = playlist.getName().toLowerCase().trim();
            if (text.contains(query)) {
                Log.e("Helper", "Query --> search playlist");
                filterPlaylist.add(playlist);
            }
        }
        return filterPlaylist;
    }
    //////////////////// Filter for Search Query -- END ////////////////////////


    //AlbumArtwork Location
    public static String getAlbumArtworkLocation() {
        return Environment.getExternalStorageDirectory() + "/AudioPlay/" + ".AlbumArtwork/";
    }

    //ArtistArtwork Location
    public static String getArtistArtworkLocation() {
        return Environment.getExternalStorageDirectory() + "/AudioPlay/" + ".ArtistArtwork/";
    }

    //Set fileName
    static String setFileName(String title) {
        if (TextUtils.isEmpty(title)) {
            title = "unknown";
        }
        return title;
    }


    //ArtistImage  load
    public String loadArtistImage(String name) {
        return getArtistArtworkLocation() + setFileName(name) + ".jpeg";
    }

    //AlbumImage  load
    public String loadAlbumImage(String name) {
        return getAlbumArtworkLocation() + setFileName(name) + ".jpeg";
    }

    public static String calculateDuration(long id) {
        String finalTimerString = "";
        String secondsString = "";
        String mp3Minutes = "";
        // Convert total duration into time

        int minutes = (int) (id % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((id % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes < 10) {
            mp3Minutes = "0" + minutes;
        } else {
            mp3Minutes = "" + minutes;
        }
        finalTimerString = finalTimerString + mp3Minutes + ":" + secondsString;
        // return timer string
        return finalTimerString;
    }

    public static List<Song> getSongMetaData(Context context, String path) {
        if (path == null){
            return null;
        }
        List<Song> songList = new ArrayList<>();
        DefaultSongLoader defaultSongLoader = new DefaultSongLoader(context);
        defaultSongLoader.setProvider(true);
        defaultSongLoader.setUri(Uri.parse(String.valueOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)));
        defaultSongLoader.setQueryTable(null);
        for (String ext : fileExtensions) {
            if (path.toLowerCase().endsWith(ext)) {
                defaultSongLoader.setSelection(MediaStore.Audio.Media.DATA + " like ? ");
                defaultSongLoader.setQueryTable2(new String[]{"%" + path + "%"});
                defaultSongLoader.setSortOrder(null);
                songList.add(defaultSongLoader.getSongData());
            }
        }
        return songList;
    }

    public static Song getSongData(String sortOrder, @NonNull Context context, String path) {
        if (path == null) {
            return null;
        }
        Song song = new Song();
        if (PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int trackCol = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
                int datacol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

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

                song.setAlbum(album);
                song.setmSongPath(mSongPath);
                song.setArtist(artist);
                song.setId(id);
                song.setAlbumId(albumId);
                song.setTrackNumber(track);
                song.setTitle(title);
            }
            if (cursor != null) {
                cursor.close();
            }
        } else {
            Log.e("DefaultSongLoader", "No read permissions");
        }
        return song;
    }

    //Create App File Directory
    public static String createAppDir(String direName) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "MusicX", direName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return null;
    }

    //Rotating the view
    public static void rotateView(@NonNull View view){
        ViewCompat.animate(view).
                rotation(360f).
                withLayer().
                setDuration(300).
                setInterpolator(new FastOutSlowInInterpolator()).
                start();
    }

    //Rotating imageView
    public static void rotationAnim(@NonNull View view) {
        RotateAnimation rotateAnimation1 = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation1.setInterpolator(new LinearInterpolator());
        rotateAnimation1.setDuration(300);
        rotateAnimation1.setRepeatCount(0);
        view.startAnimation(rotateAnimation1);
    }

    //Delete Track
    public static void deleteTrack(String name, String path, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.deleteTrackTitle);
        dialog.setMessage(R.string.deleteTrackMessage);
        dialog.setPositiveButton("Delete", (dialog1, which) -> {
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    if (file.delete()) {
                        Log.e("-->", "file Deleted :" + path);
                        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"audio/*"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {

                            }

                            @Override
                            public void onScanCompleted(String s, Uri uri) {

                            }
                        });
                        Toast.makeText(context, "Song deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("-->", "file not Deleted :" + name);
                        Toast.makeText(context, "Failed to delete song", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            } else {
                Log.d("Helper", "Path not found");
            }
        }).setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

   //Set RingTone
    private static void setRingtone(Context context, String path) {
        if (path == null) {
            return;
        }
        File file = new File(path);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        String filterName = path.substring(path.lastIndexOf("/") + 1);
        contentValues.put(MediaStore.MediaColumns.TITLE, filterName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        contentValues.put(MediaStore.MediaColumns.SIZE, file.length());
        contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            String id = cursor.getString(0);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            context.getContentResolver().update(uri, contentValues, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(id));
            try {
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            cursor.close();
        }
    }

    public static void setRingTone(Context context, String path) {
        if (permissionManager.isWriteSettingsGranted(context)) {
            setRingtone(context, path);
            Toast.makeText(context, "Ringtone set", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Helper", "Write Permission Not Granted on Android Version 6+");
            Toast.makeText(context, "Settings write permission denied", Toast.LENGTH_LONG).show();
        }
    }

    public List<Folder> filterFolder(@NonNull Context context, List<Folder> fileList, String query) {
        query = query.toLowerCase().trim();
        final List<Folder> filterFolder = new ArrayList<>();
        final List<Song> songList = new ArrayList<>();
        for (Folder folder : fileList) {
            Folder folders = new Folder();
            if (!folder.getFile().isDirectory()) {
                Song song = Helper.getSongData(Extras.getInstance().getSongSortOrder(), context, folder.getFile().getAbsolutePath());
                final String text = song.getTitle().toLowerCase().trim();
                if (text.contains(query)) {
                    songList.add(song);
                }
            }
            final String text1 = folder.getFile().getName().toLowerCase().trim();
            if (text1.contains(query)) {
                folders.setFile(folder.getFile());
                folders.setSongList(songList);
                filterFolder.add(folders);
            }
        }
        return filterFolder;
    }

    public static String getStoragePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String musicfolderpath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d("Helper", musicfolderpath);
            return musicfolderpath;
        } else {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        }
    }

    public static int getIndex(@NonNull String path, @NonNull List<Song> songList) {
        int index = -1;
        for (int i = 0; i < songList.size(); i++) {
            Song song = songList.get(i);
            if (song.getmSongPath().contains(path)) {
                index = i;
                Log.e("Helper", String.valueOf(index));
                break;
            }
        }
        return index;
    }

    //Share song
    public static void shareMusic(long id, Context context) {
        if (permissionManager.isExternalReadStorageGranted(context)) {
            if (id == 0) {
                return;
            }
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri trackUri = Uri.parse(uri.toString() + "/" + id);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, trackUri);
            intent.setType("audio/*");
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
        } else {
            Log.d("Helper", "Permission failed");
        }
    }

    ///////////////////////// Playlist /////////////////////////////////////////

    // Delete Playlist Track
    public static void deletePlaylistTrack(Context context, long playlistId, long audioId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String filter = MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + audioId;
        resolver.delete(uri, filter, null);
        Toast.makeText(context, "Song Removed from Playlist", Toast.LENGTH_SHORT).show();
    }

    //Delete playlist
    public static void deletePlaylist(Context context, String selectedPlaylist) {
        String playlistID = getPlayListId(context, selectedPlaylist);
        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {playlistID};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
    }

    //Return Playlist Id
    private static String getPlayListId(Context context, String playlist) {
        int recordCount;
        Uri newUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        final String playlistID = MediaStore.Audio.Playlists._ID;
        final String playlistName = MediaStore.Audio.Playlists.NAME;
        String where = MediaStore.Audio.Playlists.NAME + "=?";
        String[] whereVal = {playlist};
        String[] projection = {playlistID, playlistName};
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(newUri, projection, where, whereVal, null);
        if (cursor != null) {
            recordCount = cursor.getCount();
            String foundPlaylistID = "";
            if (recordCount > 0) {
                cursor.moveToFirst();
                int idColumn = cursor.getColumnIndex(playlistID);
                foundPlaylistID = cursor.getString(idColumn);
                cursor.close();
            }
            cursor.close();
            return foundPlaylistID;
        } else {
            return "";
        }

    }

    //Playlist Chooser
    public static void PlaylistChooser(Fragment fragment, Context context, long song) {
        PlaylistPickerFragment playListPicker = new PlaylistPickerFragment();
       /* playListPicker.setPicked((playlistPicked) playlist -> {
            //addSongToPlaylist(context, playlist.getId(), song);
            Toast.makeText(context, "Song is added ", Toast.LENGTH_SHORT).show();
        });*/
        //playListPicker.show(fragment.getFragmentManager(), null);
    }

    //Multi playlist chooser
    public static void PlaylistMultiChooser(Fragment fragment, Context context, List<Song> songList) {
        if (songList == null){
            return;
        }
        PlaylistPickerFragment playListPicker = new PlaylistPickerFragment();
        /*playListPicker.setPicked(playlist -> {
            try {
                for (Song song : songList) {
                    //addSongToPlaylist(context, playlist.getId(), song.getId());
                }
            } finally {
                Toast.makeText(context, "Song is added ", Toast.LENGTH_SHORT).show();
            }
        });*/
        //playListPicker.show(fragment.getFragmentManager(), null);
    }

    // Add songs to playlist
    public static void addSongToPlaylist(@NonNull Context context, long playlistId, long songId) {
        if (permissionManager.writeExternalStorageGranted(context)) {
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
            final int base = getSongCount(context.getContentResolver(), uri);
            insertPlaylist(context, uri, songId, base + 1);
            Toast.makeText(context, "Song is added ", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("PlaylistHelper", "permissions failed");
        }
    }

    //Return song Count
    public static int getSongCount(ContentResolver resolver, Uri uri) {
        String[] cols = new String[]{"count(*)"};
        Cursor cur = resolver.query(uri, cols, null, null, null);
        if (cur != null) {
            cur.moveToFirst();
            final int count = cur.getInt(0);
            cur.close();
            return count;
        } else {
            return 0;
        }
    }

    //Create Playlist
    private static void createPlaylist(@NonNull Context context, String playlistName) {
        if (permissionManager.writeExternalStorageGranted(context)) {
            Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            String[] dataCol = new String[]{
                    MediaStore.Audio.Playlists.NAME, MediaStore.Audio.Playlists._ID
            };
            boolean isExist = true;
            Cursor cursor = context.getContentResolver().query(uri, dataCol, "", null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameCol = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
                do {
                    String name = cursor.getString(nameCol);
                    if (name.equals(playlistName)) {
                        isExist = false;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            if (isExist) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Playlists.NAME, playlistName);
                values.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
                values.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
                context.getContentResolver().insert(uri, values);
            } else {
                Toast.makeText(context, "Playlist Already Exists", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("PlaylistHelper", "Permission failed");
        }
    }

    //Insert Playlist
    private static void insertPlaylist(@NonNull Context context, Uri uri, long songId, int index) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, index);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId);
        //  values.put(MediaStore.Audio.Playlists.Members.DATE_ADDED, System.currentTimeMillis());
        //  values.put(MediaStore.Audio.Playlists.Members.DATE_MODIFIED, System.currentTimeMillis());
        context.getContentResolver().insert(uri, values);
    }

    //Rename Playlist
    public static void renamePlaylist(Context context, String newPlaylist, long playlist_id) {
        if (permissionManager.writeExternalStorageGranted(context)) {
            Uri newUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            String where = MediaStore.Audio.Playlists._ID + " =? ";
            String[] whereVal = {Long.toString(playlist_id)};
            values.put(MediaStore.Audio.Playlists.NAME, newPlaylist);
            resolver.update(newUri, values, where, whereVal);
        } else {
            Log.e("PlaylistHelper", "Permission failed");
        }
    }

    // Create Playlist dialog
    public static void showCreatePlaylistDialog(@NonNull Context context, RefreshPlaylist refreshPlaylist) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.new_playlist_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText playlistName = dialog.findViewById(R.id.playlistName);
        Button createPlaylistBtn = dialog.findViewById(R.id.createPlaylistBtn);
        createPlaylistBtn.setOnClickListener(v1 -> {
            String playlist = playlistName.getText().toString();
            if (TextUtils.isEmpty(playlist)){
                playlistName.setError(context.getString(R.string.name_cannot_be_empty));
            }
            else {
                createPlaylist(context, playlist);
                refreshPlaylist.refresh();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Rename Playlist dialog
    public static void showRenameDialog(@NonNull Context context, RefreshPlaylist refreshPlaylist, long id) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.new_playlist_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        EditText playlistName = dialog.findViewById(R.id.playlistName);
        Button renamePlaylistBtn = dialog.findViewById(R.id.createPlaylistBtn);
        dialogTitle.setText(R.string.rename_playlist);
        playlistName.setHint(R.string.rename_playlist);
        renamePlaylistBtn.setText(R.string.rename);
        renamePlaylistBtn.setOnClickListener(v1 -> {
            String playlist = playlistName.getText().toString();
            if (TextUtils.isEmpty(playlist)){
                playlistName.setError(context.getString(R.string.name_cannot_be_empty));
            }
            else {
                renamePlaylist(context, playlist, id);
                refreshPlaylist.refresh();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Delete playlist Dialog
    public static void deletePlaylistDialog(@NonNull Context context, String playlistName, RefreshPlaylist refreshPlaylist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(playlistName);
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_playlist);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            deletePlaylist(context, playlistName);
            Toast.makeText(context, playlistName + " Deleted", Toast.LENGTH_SHORT).show();
            refreshPlaylist.refresh();
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    ///////////////////////// Playlist -- END /////////////////////////////////////////


    public static List<String> getSavedQueueList(Context context){
        List<String> queueList = new ArrayList<>();
        SaveQueueDatabase queueDatabase = new SaveQueueDatabase(context, Constants.Queue_Store_TableName);
        queueList = queueDatabase.readAll();
        queueDatabase.close();
        if (queueList.size() > 0){
            return queueList;
        }else {
            return null;
        }
    }

    //check activity/class/package present or not in the device
    public static boolean isActivityPresent(Context context, Intent intent){
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}

