package com.codemountain.audioplay.interfaces;

import android.provider.BaseColumns;


public interface DefaultColumn extends BaseColumns {

    String SongTitle = "SongTitle";
    String SongArtist = "SongArtist";
    String SongAlbum = "SongAlbum";
    String SongNumber = "SongNumber";
    String SongId = "SongId";
    String SongAlbumId = "SongAlbumId";
    String SongPath = "SongPath";


    String ArtistTitle = "ArtistTitle";
    String ArtistId = "ArtistId";
    String ArtistAlbumCount = "ArtistAlbumCount";
    String ArtistTrackCount = "ArtistTrackCount";

    String QueueName = "QueueName";

    String dirPath = "DirPath";

}
