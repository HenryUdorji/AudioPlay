package com.codemountain.audioplay.service;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.codemountain.audioplay.interfaces.bitmap;
import com.codemountain.audioplay.utils.ArtWork;

import static com.codemountain.audioplay.utils.Constants.META_CHANGED;
import static com.codemountain.audioplay.utils.Constants.PLAYSTATE_CHANGED;


public class MediaSession {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void lockScreenMedia(MediaSessionCompat mediaSessionCompat, AudioPlayService audioPlayService, String what) {
        if (audioPlayService == null) {
            return;
        }
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        if (what.equals(PLAYSTATE_CHANGED) || what.equals(META_CHANGED)) {
            int state = MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying() ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_PLAYING;
            mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(state, audioPlayService.getPlayerPos(), 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());

            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioPlayService.getSongTitle());
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioPlayService.getDuration());
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioPlayService.getSongArtistName());
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audioPlayService.getSongAlbumName());

            handler.post(new Runnable() {
                @Override
                public void run() {
                    ArtWork.ArtworkLoader(audioPlayService, 300, 300, audioPlayService.getSongAlbumName(), audioPlayService.getSongAlbumID(), new bitmap() {
                        @Override
                        public void bitmapWorking(Bitmap bitmap) {
                            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                            mediaSessionCompat.setMetadata(builder.build());
                        }

                        @Override
                        public void bitmapFailed(Bitmap bitmap) {
                            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                            mediaSessionCompat.setMetadata(builder.build());
                        }
                    });
                }
            });
        }
    }
}
