
package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBg = findViewById(R.id.startBackground);
        Button stopBg = findViewById(R.id.stopBackground);
        Button startFg = findViewById(R.id.startForeground);
        Button stopFg = findViewById(R.id.stopForeground);

        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    100
            );
        }

        startBg.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this, com.example.servicedemo.BackgroundDemoService.class);

            startService(intent);
            showMessage("Background Service Started");
        });

        stopBg.setOnClickListener(v -> {
            stopService(new Intent(
                    this, com.example.servicedemo.BackgroundDemoService.class));

            showMessage("Background Service Stopped");
        });

        startFg.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this, com.example.servicedemo.ForegroundDemoService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }

            showMessage("Foreground Service Started");
        });

        stopFg.setOnClickListener(v -> {
            stopService(new Intent(
                    this, com.example.servicedemo.ForegroundDemoService.class));

            showMessage("Foreground Service Stopped");
        });
    }

    private void showMessage(String message) {
        Toast.makeText(
                this, message, Toast.LENGTH_SHORT
        ).show();
    }
}
