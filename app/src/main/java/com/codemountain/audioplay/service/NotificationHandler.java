package com.codemountain.audioplay.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.codemountain.audioplay.R;
import com.codemountain.audioplay.activities.MusicPlayerActivity;
import com.codemountain.audioplay.interfaces.bitmap;
import com.codemountain.audioplay.utils.ArtWork;

import static com.codemountain.audioplay.utils.Constants.ACTION_NEXT;
import static com.codemountain.audioplay.utils.Constants.ACTION_PREVIOUS;
import static com.codemountain.audioplay.utils.Constants.ACTION_TOGGLE;
import static com.codemountain.audioplay.utils.Constants.PLAYSTATE_CHANGED;


public class NotificationHandler {

    public static final int notificationID = 1127;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static String CHANNEL_ID = "audioPlayChannel";

    public static void buildNotification(AudioPlayService audioPlayService, String what) {
        if (audioPlayService == null) {
            return;
        }
        NotificationCompat.Builder builder;
        RemoteViews remoteViews = new RemoteViews(audioPlayService.getPackageName(), R.layout.widget);
        RemoteViews smallRemoteView = new RemoteViews(audioPlayService.getPackageName(), R.layout.small_notification);

        Intent intent = new Intent(audioPlayService, MusicPlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(audioPlayService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder = new NotificationCompat.Builder(audioPlayService, CHANNEL_ID)
                    .setWhen(System.currentTimeMillis())
                    .setCategory(Intent.CATEGORY_APP_MUSIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setShowWhen(false)
                    .setAutoCancel(true)
                    .setCustomBigContentView(remoteViews)
                    .setContent(smallRemoteView)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }else {
            builder = new NotificationCompat.Builder(audioPlayService)
                    .setWhen(System.currentTimeMillis())
                    .setCategory(Intent.CATEGORY_APP_MUSIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setShowWhen(false)
                    .setAutoCancel(true)
                    .setCustomBigContentView(remoteViews)
                    .setContent(smallRemoteView)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        remoteViews.setOnClickPendingIntent(R.id.item_view, pendInt);
        smallRemoteView.setOnClickPendingIntent(R.id.small_item_view, pendInt);
        remoteViews.setTextViewText(R.id.title, audioPlayService.getSongTitle());
        remoteViews.setTextViewText(R.id.artist, audioPlayService.getSongArtistName());
        smallRemoteView.setTextViewText(R.id.small_title, audioPlayService.getSongTitle());
        smallRemoteView.setTextViewText(R.id.small_artist, audioPlayService.getSongArtistName());

        /*FavHelper favHelper = new FavHelper(musicXService);
        if (favHelper.isFavorite(musicXService.getsongId())) {
            remoteViews.setImageViewResource(R.id.action_favorite, R.drawable.ic_action_favorite);
        } else {
            remoteViews.setImageViewResource(R.id.action_favorite, R.drawable.ic_action_favorite_outline);
        }*/

        if (audioPlayService.isPlaying()) {
            builder.setSmallIcon(R.drawable.ic_play_circle_small);
            builder.setOngoing(true);
        }
        else {
            builder.setSmallIcon(R.drawable.ic_play_circle_small);
            builder.setOngoing(false);
        }
        if (what.equals(PLAYSTATE_CHANGED)) {
            if (MediaPlayerSingleton.getInstance().getMediaPlayer().isPlaying()) {
                remoteViews.setImageViewResource(R.id.toggle, R.drawable.ic_play);
                smallRemoteView.setImageViewResource(R.id.small_toggle, R.drawable.ic_play);
            }
            else {
                remoteViews.setImageViewResource(R.id.toggle, R.drawable.ic_pause);
                smallRemoteView.setImageViewResource(R.id.small_toggle, R.drawable.ic_pause);
            }
        }
        handler.post(() -> {
            ArtWork.ArtworkLoader(audioPlayService, 300, 300, audioPlayService.getSongAlbumName(), audioPlayService.getSongAlbumID(), new bitmap() {
                @Override
                public void bitmapWorking(Bitmap bitmap) {
                    remoteViews.setImageViewBitmap(R.id.artwork, bitmap);
                    smallRemoteView.setImageViewBitmap(R.id.small_artwork, bitmap);
                    NotificationManagerCompat.from(audioPlayService).notify(notificationID, builder.build());
                }

                @Override
                public void bitmapFailed(Bitmap bitmap) {
                    remoteViews.setImageViewBitmap(R.id.artwork, bitmap);
                    smallRemoteView.setImageViewBitmap(R.id.small_artwork, bitmap);
                    NotificationManagerCompat.from(audioPlayService).notify(notificationID, builder.build());
                }
            });


            controls(remoteViews, smallRemoteView, audioPlayService);
        });
    }

        private static void controls(RemoteViews remoteViews, RemoteViews smallViews, AudioPlayService audioPlayService) {
            PendingIntent toggleIntent = PendingIntent.getService(audioPlayService, 0, new Intent(audioPlayService, AudioPlayService.class).setAction(ACTION_TOGGLE), 0);
            PendingIntent nextIntent = PendingIntent.getService(audioPlayService, 0, new Intent(audioPlayService, AudioPlayService.class).setAction(ACTION_NEXT), 0);
            PendingIntent previousIntent = PendingIntent.getService(audioPlayService, 0, new Intent(audioPlayService, AudioPlayService.class).setAction(ACTION_PREVIOUS), 0);
            //PendingIntent savefavIntent = PendingIntent.getService(audioPlayService, 0, new Intent(audioPlayService, AudioPlayService.class).setAction(ACTION_FAV), 0);

            remoteViews.setOnClickPendingIntent(R.id.toggle, toggleIntent);
            remoteViews.setOnClickPendingIntent(R.id.next, nextIntent);
            remoteViews.setOnClickPendingIntent(R.id.prev, previousIntent);
            //remoteViews.setOnClickPendingIntent(R.id.action_favorite, savefavIntent);

            smallViews.setOnClickPendingIntent(R.id.small_toggle, toggleIntent);
            smallViews.setOnClickPendingIntent(R.id.small_next, nextIntent);
            smallViews.setOnClickPendingIntent(R.id.small_prev, previousIntent);
        }

    /*public static int[] getAvailableColor(Context context, Palette palette) {
        int[] temp = new int[3]; //array with size 3
        if (palette.getDarkVibrantSwatch() != null) {
            temp[0] = palette.getDarkVibrantSwatch().getRgb();
            temp[1] = palette.getDarkVibrantSwatch().getTitleTextColor();
            temp[2] = palette.getDarkVibrantSwatch().getBodyTextColor();
        } else if (palette.getDarkMutedSwatch() != null) {
            temp[0] = palette.getDarkMutedSwatch().getRgb();
            temp[1] = palette.getDarkMutedSwatch().getTitleTextColor();
            temp[2] = palette.getDarkMutedSwatch().getBodyTextColor();
        } else if (palette.getVibrantSwatch() != null) {
            temp[0] = palette.getVibrantSwatch().getRgb();
            temp[1] = palette.getVibrantSwatch().getTitleTextColor();
            temp[2] = palette.getVibrantSwatch().getBodyTextColor();
        } else if (palette.getDominantSwatch() != null) {
            temp[0] = palette.getDominantSwatch().getRgb();
            temp[1] = palette.getDominantSwatch().getTitleTextColor();
            temp[2] = palette.getDominantSwatch().getBodyTextColor();
        } else if (palette.getMutedSwatch() != null) {
            temp[0] = palette.getMutedSwatch().getRgb();
            temp[1] = palette.getMutedSwatch().getTitleTextColor();
            temp[2] = palette.getMutedSwatch().getBodyTextColor();
        } else {
            temp[0] = ContextCompat.getColor(context, R.color.MaterialGrey);
            temp[1] = Color.WHITE;
            temp[2] = Color.WHITE;
        }
        return temp;
    }*/

    }
