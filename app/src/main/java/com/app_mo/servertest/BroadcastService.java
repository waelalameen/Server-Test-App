package com.app_mo.servertest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BroadcastService extends Service {
    public static final String COUNTDOWN_BR = "com.app_mo.servertest.Timer";
    Intent intent1 = new Intent(COUNTDOWN_BR);
    private int val = 100;
    private Notification notification;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 1234;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CountDownTimer countDownTimer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(long l) {
                val = val - 1;
                intent1.putExtra("counter", val);
                sendBroadcast(intent1);
            }

            @Override
            public void onFinish() {
                intent1.putExtra("counter", 0);
                sendBroadcast(intent1);
                stopSelf();
            }
        };

        countDownTimer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(getBaseContext());
        builder.setContentIntent(pendIntent);
        builder.setSmallIcon(R.drawable.ic_home_black_24dp);
        builder.setTicker("CUSTOM MESSAGE");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setContentTitle("Test Service");
        builder.setContentText("Count Down Timer");

        notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        startForeground(NOTIFICATION_ID, notification);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //countDownTimer.cancel();
    }
}
