package com.example.jean.jcplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

/**
 * Created by jean on 12/07/16.
 */
public class JCNotificationPlayer implements JCPlayerService.JCPlayerServiceListener{
    public static final int NOTIFICATION_ID = 100;
    public static final int NEXT_ID = 0;
    public static final int PREVIOUS_ID = 1;
    public static final int PLAY_ID = 2;
    public static final int PAUSE_ID = 3;
    public static final String NEXT = "NEXT";
    public static final String PREVIOUS = "PREVIOUS";
    public static final String PAUSE = "PAUSE";
    public static final String PLAY = "PLAY";
    public static final String ACTION = "ACTION";
    public static final String PLAYLIST = "PLAYLIST";
    public static final String CURRENT_AUDIO = "CURRENT_AUDIO";
    private NotificationManager notificationManager;
    private Context context;
    private String title;
    private String time  = "00:00";
    private int iconResource;

    public JCNotificationPlayer(Context context){
        this.context = context;
    }

    public void createNotificationPlayer(String title, int iconResourceResource){
        this.title = title;
        this.iconResource = iconResourceResource;
        Intent openUi = new Intent(context, context.getClass());
        openUi.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        JCAudioPlayer.getInstance().registerNotificationListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new Notification.Builder(context)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setSmallIcon(iconResourceResource)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconResourceResource))
                    .setContent(createNotificationPlayerView())
                    .setContentIntent(PendingIntent.getActivity(context, NOTIFICATION_ID, openUi, PendingIntent.FLAG_CANCEL_CURRENT))
                    .setCategory(Notification.CATEGORY_SOCIAL)
                    .build();

            /*

                private PendingIntent createContentIntent(MediaDescriptionCompat description) {
                    Intent openUI = new Intent(mService, MusicPlayerActivity.class);
                    openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    openUI.putExtra(MusicPlayerActivity.EXTRA_START_FULLSCREEN, true);
                    if (description != null) {
                        openUI.putExtra(MusicPlayerActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, description);
                    }
                    return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                }
             */

            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void updateNotification(){
        createNotificationPlayer(title, iconResource);
    }

    private RemoteViews createNotificationPlayerView(){
        RemoteViews remoteView;

        if (JCAudioPlayer.getInstance().isPaused()){
            remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_play);
            remoteView.setOnClickPendingIntent(R.id.btn_play_notification, buildPendingIntent(PLAY, PLAY_ID));
        }else {
            remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_pause);
            remoteView.setOnClickPendingIntent(R.id.btn_pause_notification, buildPendingIntent(PAUSE, PAUSE_ID));
        }

        remoteView.setTextViewText(R.id.txt_current_music_notification, title);
        remoteView.setTextViewText(R.id.txt_duration_notification, time);
        remoteView.setImageViewResource(R.id.icon_player,iconResource);
        remoteView.setOnClickPendingIntent(R.id.btn_next_notification, buildPendingIntent(NEXT, NEXT_ID));
        remoteView.setOnClickPendingIntent(R.id.btn_prev_notification, buildPendingIntent(PREVIOUS, PREVIOUS_ID));

        return  remoteView;
    }


    private PendingIntent buildPendingIntent(String action, int id){
        Intent playIntent = new Intent(context.getApplicationContext(), JCPlayerNotificationReceiver.class);
        playIntent.putExtra(ACTION, action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), id, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onPreparedAudio() {

    }

    @Override
    public void onCompletedAudio() {

    }

    @Override
    public void onPaused() {
        createNotificationPlayer(title, iconResource);
    }

    @Override
    public void onPlaying() {
        createNotificationPlayer(title, iconResource);
    }

    @Override
    public void updateTime(String time) {
        this.time = time;
        createNotificationPlayer(title, iconResource);
    }

    @Override
    public void updateTitle(String title) {
        this.title = title;
        createNotificationPlayer(title, iconResource);
    }

    public void destroy(){
        if(notificationManager != null);
        try {
            notificationManager.cancel(NOTIFICATION_ID);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}