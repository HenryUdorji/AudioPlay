package com.codemountain.audioplay.interfaces;


import com.codemountain.audioplay.model.Song;

import java.util.List;


public interface MetaDatas {

    void onSongSelected(List<Song> songList, int pos);

    void onShuffleRequested(List<Song> songList, boolean play);

    void addToQueue(Song song);

    void setAsNextTrack(Song song);

}
