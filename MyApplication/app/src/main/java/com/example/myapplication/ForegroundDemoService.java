
package com.example.myapplication;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class ForegroundDemoService extends Service {

    private static final String TAG = "ForegroundService";
    private static final String CHANNEL_ID = "service_channel";

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private int count = 0;
    private int activeStartId;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            count++;

            Log.d(TAG, "Foreground count: " + count);

            handler.postDelayed(this, 5000);
        }
    };

    private final Runnable stopTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Demo time limit reached");
            stopSelf(activeStartId);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate()");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(
            Intent intent, int flags, int startId) {

        activeStartId = startId;

        Log.d(TAG, "onStartCommand()");

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(
                    this, CHANNEL_ID)
                    .setContentTitle("Service Lifecycle Demo")
                    .setContentText("Foreground service is running")
                    .setSmallIcon(
                            android.R.drawable.ic_media_play)
                    .setOngoing(true)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("Service Lifecycle Demo")
                    .setContentText("Foreground service is running")
                    .setSmallIcon(
                            android.R.drawable.ic_media_play)
                    .setOngoing(true)
                    .build();
        }

        startForeground(1, notification);

        handler.removeCallbacks(task);
        handler.removeCallbacks(stopTask);

        handler.post(task);
        handler.postDelayed(stopTask, 120000);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Service Demo",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        Log.d(TAG, "onDestroy()");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
