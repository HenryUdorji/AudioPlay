package com.codemountain.audioplay.utils;

import android.Manifest;

import com.codemountain.audioplay.interfaces.DefaultColumn;

public class Constants {

    /*
    permission properties
     */
    public static final int PERMISSIONS_REQ = 10;
    public static final int OVERLAY_REQ = 1;
    public static final int WRITE_SETTINGS = 2;
    public static final int EQ = 3445;
    public static final int NAV = 56660;
    public static final int IMAGE_PICKER = 25350;

    /*
    Sorting properties
     */
    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    public static final String ARTIST_ALBUM_SORT = "artist_album_sort";
    public static final String PLAYLIST_SORT_ORDER = "playlist_sort";
    /*
    Playlist  & fav. properties
     */
    public static final String PARAM_PLAYLIST_NAME = "playlist_name";
    public static final String PARAM_PLAYLIST_ID = "playlist_id";
    /*
    Floating Widget properties
     */
    public static final String KEY_POSITION_X = "position_x";
    public static final String KEY_POSITION_Y = "position_y";
    /*
    Preferences
     */
    public static final String PlayingView = "playing_selection";
    public static final String FloatingView = "floating_view";
    public static final String TextFonts = "change_fonts";
    public static final String BlurView = "blur_view";
    public static final String GridViewAlbum = "gridlist_albumview";
    public static final String GridViewArtist = "gridlist_artistview";
    public static final String GridViewSong = "gridlist_songview";
    public static final String SaveLyrics = "save_lyrics";
    public static final String SaveTelephony = "save_telephony";
    public static final String SaveHeadset = "save_headset";
    public static final String ClearFav = "clear_favdb";
    public static final String ClearRecently = "clear_recentdb";
    public static final String ClearQueue = "clear_queuedb";
    public static final String SAVE_DATA = "save_internet";
    public static final String HIDE_NOTIFY = "hide_notification";
    public static final String HIDE_LOCKSCREEEN = "hide_lockscreenMedia";
    public static final String REORDER_TAB = "tab_selection";
    public static final String RESTORE_LASTTAB = "restore_lasttab";
    public static final String HQ_ARTISTARTWORK = "hqartist_artwork";
    public static final String EQSWITCH = "eqswitch";
    public static final String ARTWORKCOLOR = "artwork_adaptive";
    public static final String FOLDER_PATH = "folderpath";
    public static final String ALBUMGRID = "albumgrid";
    public static final String ARTISTGRID = "artistgrid";
    public static final String SONGGRID = "songgrid";
    public static final String WIDGETTRACK = "widgettrack";
    public static final String PLAYLIST_ID = "playlistId";
    public static final String FADE_IN_OUT_DURATION = "fadein_fadeout_seekbar";
    public static final String FADE_TRACK = "fade_inout";
    public static final String FADE_SWITCH = "fade_switch";
    public static final String FADE_TIMER = "timer_switch";
    public static final String THEME_SWITCH = "theme_switch";
    public static final String VISUALIZER = "visualizer";
    public static final String LANGUAGE = "language";
    public static final String TIMER_DURATION = "timer_duration";
    public static final String REMOVETABS = "remove_tabs";
    public static final String TRYPEFACE_PATH = "font_path";
    public static final String WIDGET_COLOR = "widget_color";
    public static final String PLAYINGVIEW_TRACK = "isplayingView3";
    public static final String SETTINGS_TRACK = "settings_track";
    public static final String HD_ARTWORK = "hd_artwork";
    public static final String DOWNLOADED_ARTWORK = "downloaded_artwork";
    public static final String REMOVE_TABLIST = "removeTablist";
    public static final String TAG_METADATA = "MetaData";
    public static final String AUDIO_FILTER = "audio_filter";
    public static final String NAV_SETTINGS = "nav_settings";

    /**
     * Files filter
     */
    public static final String fileExtensions[] = new String[]{
            ".aac", ".mp3", ".wav", ".ogg", ".midi", ".3gp", ".mp4", ".m4a", ".amr", ".flac"
    };

    /*
    Choices
     */
    public static final String Zero = "0";
    public static final String One = "1";
    public static final String Two = "2";
    public static final String Three = "3";
    public static final String Four = "4";
    public static final String Five = "5";
    /*
    Theming Properties
     */
    public static final String LightTheme = "light_theme";
    public static final String DarkTheme = "dark_theme";
    public static final String BlackTheme = "black_theme";
    /*
    Database Properties
     */
    public static final String RecentlyPlayed_TableName = "RecentlyPlayed";
    public static final String Queue_TableName = "QueuePlaylist";
    public static final String Fav_TableName = "Favorites";
    public static final String Queue_Store_TableName = "QueueStore";

    public static final int DbVersion = 2;
    public static final String Separator = ",";

    public static final String DOWNLOAD_ARTWORK = "DownloadAlbum";
    public static final String DOWNLOAD_ARTWORK2 = "DownloadArtwork";

    /*
    Equalizer
     */
    public static final String BAND_LEVEL = "level";
    public static final String SAVE_PRESET = "preset";
    public static final int GAIN_MAX = 100;
    public static final String SAVE_EQ = "Equalizers";
    public static final String BASS_BOOST = "BassBoost";
    public static final String VIRTUAL_BOOST = "VirtualBoost";
    public static final String LOUD_BOOST = "Loud";
    public static final String PRESET_BOOST = "PresetReverb";
    public static final String PRESET_POS = "spinner_position";
    public static final short BASSBOOST_STRENGTH = 1000;
    public static final short Virtualizer_STRENGTH = 1000;
    /**
     * Developer Name
     */
    public static final String DEVELOPER_NAME = "Udorji Henry";
    public static final String PACKAGE_NAME = "com.codemountain.audioplay.";
    /*
    album properties
     */
    public static final String ALBUM_ID = PACKAGE_NAME + "id";
    public static final String ALBUM_NAME = PACKAGE_NAME + "name";
    public static final String ALBUM_ARTIST = PACKAGE_NAME + "artist";
    public static final String ALBUM_YEAR = PACKAGE_NAME + "year";
    public static final String ALBUM_TRACK_COUNT = PACKAGE_NAME + "track_count";
    /*
    artist properties
     */
    public static final String ARTIST_ARTIST_ID = PACKAGE_NAME + "artist_id";
    public static final String ARTIST_NAME = PACKAGE_NAME + "artist_name";
    public static final String ARTIST_ALBUM_COUNT = PACKAGE_NAME + "album_count";
    public static final String ARTIST_TRACK_COUNT = PACKAGE_NAME + "track_count";
    /*
    song properties
     */
    public static final String SONG_ID = PACKAGE_NAME + "song_id";
    public static final String SONG_TITLE = PACKAGE_NAME + "song_title";
    public static final String SONG_ARTIST = PACKAGE_NAME + "song_artist";
    public static final String SONG_ALBUM = PACKAGE_NAME + "song_album";
    public static final String SONG_ALBUM_ID = PACKAGE_NAME + "song_album_id";
    public static final String SONG_TRACK_NUMBER = PACKAGE_NAME + "song_track_number";
    public static final String SONG_PATH = PACKAGE_NAME + "song_path";
    public static final String SONG_YEAR = PACKAGE_NAME + "song_year";
    /*
    playing propertiess
     */
    public static final String PREF_AUTO_PAUSE = PACKAGE_NAME + "AUTO_PAUSE";
    public static final String ACTION_PLAY = PACKAGE_NAME + "ACTION_PLAY";
    public static final String ACTION_PAUSE = PACKAGE_NAME + "ACTION_PAUSE";
    public static final String ACTION_CHANGE_STATE = PACKAGE_NAME + "ACTION_CHANGE_STATE";
    public static final String ACTION_TOGGLE = PACKAGE_NAME + "ACTION_TOGGLE";
    public static final String ACTION_NEXT = PACKAGE_NAME + "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = PACKAGE_NAME + "ACTION_PREVIOUS";
    public static final String ACTION_STOP = PACKAGE_NAME + "ACTION_STOP";
    public static final String ACTION_CHOOSE_SONG = PACKAGE_NAME + "ACTION_CHOOSE_SONG";
    public static final String META_CHANGED = PACKAGE_NAME + "META_CHANGED";
    public static final String PLAYSTATE_CHANGED = PACKAGE_NAME + "PLAYSTATE_CHANGED";
    public static final String QUEUE_CHANGED = PACKAGE_NAME + "QUEUE_CHANGED";
    public static final String POSITION_CHANGED = PACKAGE_NAME + "POSITION_CHANGED";
    public static final String ITEM_ADDED = PACKAGE_NAME + "ITEM_ADDED";
    public static final String ORDER_CHANGED = PACKAGE_NAME + "ORDER_CHANGED";
    public static final String REPEAT_MODE_CHANGED = PACKAGE_NAME + "REPEAT_MODE_CHANGED";
    public static final String REPEATMODE = PACKAGE_NAME + "repeatMode";
    public static final String SHUFFLEMODE = PACKAGE_NAME + "shuffle";
    public static final String PLAYINGSTATE = PACKAGE_NAME + "playingState";
    public static final String CURRENTPOS = PACKAGE_NAME + "position";
    public static final String ACTION_PLAYINGVIEW = PACKAGE_NAME + "PLAYING_VIEW";
    public static final String ACTION_COMMAND = PACKAGE_NAME + "command";
    public static final String ACTION_COMMAND1 = PACKAGE_NAME + "command1";
    public static final String ACTION_COMMAND2 = PACKAGE_NAME + "command2";
    public static final String ACTION_FAV = PACKAGE_NAME + "widget_fav";
    public static final String PLAYER_POS = PACKAGE_NAME + "player_pos";
    public static final String PAUSE_SHORTCUTS = PACKAGE_NAME + "pause_shortcuts";
    public static final String PLAY_SHORTCUTS = PACKAGE_NAME + "pause_shortcuts";
    public static final String SHORTCUTS_TYPES = PACKAGE_NAME + "shortcuts_type";

    public static final String SHOW_ALBUM = "show_album";
    public static final String SHOW_ARTIST = "show_artist";
    public static final String SHOW_TAG = "show_tag";

    /**
     * Permissions Array
     */
    public static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.SYSTEM_ALERT_WINDOW};

    /**
     * network link
     */
    public static String lastFmUrl = "http://ws.audioscrobbler.com/2.0/";

    public static String vagUrl = "https://www.vagalume.com.br/";
    public static String indicineUrl = "http://www.indicine.com/";
    public static String lyricsedUrl = "http://lyricsed.com/";
    public static String songlyricsUrl = "http://www.songlyrics.com/";
    public static String atozUrl = "http://www.azlyrics.com/lyrics/";
    public static String metroUrl = "http://www.metrolyrics.com/";
    public static String directUrl = "https://www.directlyrics.com/";
    public static String hindigeetUrl = "http://www.hindigeetmala.net/";
    public static String lyricsondemandUrl = "https://www.lyricsondemand.com/";
    public static String absolutelyricsUrl = "http://www.absolutelyrics.com/lyrics/view/";
    public static String lyricsbogieUrl = "https://www.lyricsbogie.com/movies/";

    /**
     * Database table
     *
     * @param tableName
     * @return
     */
    public static String DefaultColumn(String tableName, boolean torf) {
        if (torf) {
            return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    DefaultColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + Separator +
                    DefaultColumn.SongId + " INTEGER UNIQUE" + Separator +
                    DefaultColumn.SongTitle + " TEXT" + Separator +
                    DefaultColumn.SongArtist + " TEXT" + Separator +
                    DefaultColumn.SongAlbum + " TEXT" + Separator +
                    DefaultColumn.SongAlbumId + " INTEGER" + Separator +
                    DefaultColumn.SongNumber + " INTEGER" + Separator +
                    DefaultColumn.SongPath + " TEXT" + " )";
        } else {
            return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    DefaultColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + Separator +
                    DefaultColumn.ArtistId + " INTEGER UNIQUE" + Separator +
                    DefaultColumn.ArtistTitle + " TEXT" + Separator +
                    DefaultColumn.ArtistAlbumCount + " INTEGER" + Separator +
                    DefaultColumn.ArtistTrackCount + " INTEGER" + " )";
        }
    }

    /**
     * Database to store queueName
     * @param tableName
     * @return
     */
    public static String DefaultColumn(String tableName) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                DefaultColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + Separator +
                DefaultColumn.QueueName + " TEXT" + " )";
    }
}
