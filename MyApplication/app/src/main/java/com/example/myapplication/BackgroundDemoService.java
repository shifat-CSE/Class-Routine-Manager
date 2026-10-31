
package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class BackgroundDemoService extends Service {

    private static final String TAG = "BackgroundService";

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private int count = 0;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            count++;

            Log.d(TAG, "Background count: " + count);

            handler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(
            Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand()");

        handler.removeCallbacks(task);
        handler.post(task);

        return START_NOT_STICKY;
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
