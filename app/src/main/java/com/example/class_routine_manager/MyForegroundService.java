package com.example.class_routine_manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyForegroundService extends Service {
    private static final String CHANNEL_ID = "RoutineServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Routine Manager")
                .setContentText("Syncing your class routines in background...")
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, notification);
        }

        // Ekhane Data Sync er kaj ta hoche (Background Thread e)
        syncDataWithFirebase();

        return START_NOT_STICKY;
    }

    private void syncDataWithFirebase() {
        // SQLite theke shob class niye ashbo
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<ClassRoutine> classList = dbHelper.getAllClasses();

        // Firebase Firestore e save korbo
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null && !classList.isEmpty()) {
            // Amra ekta "UserRoutines" collection banabo jekhane user er ID diye data save hobe
            for (ClassRoutine routine : classList) {
                Map<String, Object> routineData = new HashMap<>();
                routineData.put("subject", routine.getSubject());
                routineData.put("code", routine.getCode());
                routineData.put("teacher", routine.getTeacher());
                routineData.put("room", routine.getRoom());
                routineData.put("day", routine.getDay());
                routineData.put("startTime", routine.getStartTime());
                routineData.put("endTime", routine.getEndTime());

                // Protita class ekta unique document hisebe save hobe
                firestore.collection("UserRoutines").document(userId)
                        .collection("MyClasses").document(routine.getSubject() + "_" + routine.getStartTime())
                        .set(routineData);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Routine Sync Service", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}