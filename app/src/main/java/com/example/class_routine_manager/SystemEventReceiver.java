package com.example.class_routine_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

public class SystemEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;
        String action = intent.getAction();

        // ১. এয়ারপ্লেন মোড চেক
        if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
            boolean isAirplaneOn = intent.getBooleanExtra("state", false);
            if (isAirplaneOn) {
                Toast.makeText(context, "Airplane Mode ON detected!", Toast.LENGTH_SHORT).show();
                showNotification(context, "Airplane Mode ON", "Network is disconnected.");
            } else {
                Toast.makeText(context, "Airplane Mode OFF detected!", Toast.LENGTH_SHORT).show();
                showNotification(context, "Airplane Mode OFF", "Network is connected.");
            }
        }

        // ২. ব্যাটারি সেভার চেক (ডার্ক মোডের জন্য)
        else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                boolean isPowerSaveOn = powerManager.isPowerSaveMode();

                if (isPowerSaveOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(context, "Battery Saver ON: Dark Mode Enabled!", Toast.LENGTH_SHORT).show();
                    showNotification(context, "Battery Saver ON", "App switched to Dark Mode to save power.");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(context, "Battery Saver OFF: Normal Mode Enabled!", Toast.LENGTH_SHORT).show();
                    showNotification(context, "Battery Saver OFF", "App switched to Normal Mode.");
                }
            }
        }
    }

    // টর্চের জন্য এই নতুন মেথডটি MainActivity থেকে কল করা হবে
    public void triggerTorchNotification(Context context, boolean isOn) {
        if (isOn) {
            Toast.makeText(context, "Torch ON detected!", Toast.LENGTH_SHORT).show();
            showNotification(context, "Torch ON", "Flashlight has been turned on.");
        } else {
            Toast.makeText(context, "Torch OFF detected!", Toast.LENGTH_SHORT).show();
            showNotification(context, "Torch OFF", "Flashlight has been turned off.");
        }
    }

    private void showNotification(Context context, String title, String message) {
        String channelId = "SystemEventChannel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "System Events", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}