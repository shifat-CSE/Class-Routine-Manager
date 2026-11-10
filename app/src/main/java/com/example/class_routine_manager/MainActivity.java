package com.example.class_routine_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.class_routine_manager.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseHelper sqliteDb;
    private DatabaseReference realtimeDbRef;
    private String userRole = "Student";
    private SystemEventReceiver systemEventReceiver;

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isTorchOn = false;
    private Handler torchHandler = new Handler();

    private boolean isTodayView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sqliteDb = new DatabaseHelper(this);

        String dbUrl = "https://classroutinemanager-a41e2-default-rtdb.asia-southeast1.firebasedatabase.app/";
        realtimeDbRef = FirebaseDatabase.getInstance(dbUrl).getReference("LiveAlert");

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.dashboardContent.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        systemEventReceiver = new SystemEventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(systemEventReceiver, filter, ContextCompat.RECEIVER_EXPORTED);
        } else {
            registerReceiver(systemEventReceiver, filter);
        }

        setupTorchDetector();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        realtimeDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String alertMsg = dataSnapshot.getValue(String.class);
                    if (alertMsg != null && !alertMsg.isEmpty()) {
                        binding.tvLiveAlert.setVisibility(View.VISIBLE);
                        binding.tvLiveAlert.setText("📢 LIVE: " + alertMsg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            userRole = "Student";
            String name = "User";

            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                String fetchedRole = task.getResult().getString("role");
                String fetchedName = task.getResult().getString("name");
                if (fetchedRole != null) userRole = fetchedRole;
                if (fetchedName != null) name = fetchedName;
            }

            binding.tvSubtitle.setText("Role: " + userRole + " | " + name);

            DatabaseReference profileRef = FirebaseDatabase.getInstance("https://classroutinemanager-a41e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Profiles").child(userId);
            String finalName = name;
            profileRef.get().addOnCompleteListener(profileTask -> {
                if (profileTask.isSuccessful() && profileTask.getResult().exists()) {
                    String rtName = profileTask.getResult().child("name").getValue(String.class);
                    String rtRole = profileTask.getResult().child("role").getValue(String.class);
                    String rtImage = profileTask.getResult().child("image").getValue(String.class);

                    if (rtName != null && rtRole != null) {
                        binding.tvSubtitle.setText("Role: " + rtRole + " | " + rtName);
                        userRole = rtRole;
                    }

                    // Profile Image load kora holo
                    if (rtImage != null && !rtImage.isEmpty()) {
                        Bitmap bitmap = decodeImage(rtImage);
                        if (bitmap != null) {
                            binding.ivDashboardProfile.setImageBitmap(bitmap);
                        }
                    }
                } else {
                    binding.tvSubtitle.setText("Role: " + userRole + " | " + finalName);
                }

                binding.progressBar.setVisibility(View.GONE);
                binding.dashboardContent.setVisibility(View.VISIBLE);
                updateButtonsVisibility();
            });
        }).addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.dashboardContent.setVisibility(View.VISIBLE);
            binding.tvSubtitle.setText("Role: Student | User");
            updateButtonsVisibility();
        });

        // Profile Picture click korle ProfileActivity te jabe
        binding.ivDashboardProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        binding.btnAddClass.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddClassActivity.class)));
        binding.btnSmartScheduler.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SmartSchedulerActivity.class)));
        binding.btnAttendance.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AttendanceMainActivity.class)));

        binding.btnTodayRoutine.setOnClickListener(v -> {
            isTodayView = true;
            binding.tvTodayTitle.setText("Today's Classes");
            loadData();
        });

        binding.btnWeeklyRoutine.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WeeklyRoutineActivity.class));
        });
    }

    private Bitmap decodeImage(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateButtonsVisibility() {
        binding.btnAddClass.setVisibility(View.GONE);
        binding.btnSmartScheduler.setVisibility(View.GONE);
        binding.btnAttendance.setVisibility(View.GONE);

        if ("CR".equals(userRole)) {
            binding.btnAddClass.setVisibility(View.VISIBLE);
            binding.btnSmartScheduler.setVisibility(View.VISIBLE);
            binding.btnAttendance.setVisibility(View.GONE);
        } else if ("Teacher".equals(userRole)) {
            binding.btnAddClass.setVisibility(View.GONE);
            binding.btnSmartScheduler.setVisibility(View.GONE);
            binding.btnAttendance.setVisibility(View.GONE);
        } else {
            binding.btnAddClass.setVisibility(View.GONE);
            binding.btnSmartScheduler.setVisibility(View.GONE);
            binding.btnAttendance.setVisibility(View.VISIBLE);
        }
    }

    private void setupTorchDetector() {
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cameraManager != null) {
                cameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
                    @Override
                    public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                        super.onTorchModeChanged(cameraId, enabled);
                        if (enabled != isTorchOn) {
                            isTorchOn = enabled;
                            if (systemEventReceiver != null) {
                                systemEventReceiver.triggerTorchNotification(MainActivity.this, enabled);
                            }
                        }
                    }
                }, torchHandler);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (systemEventReceiver != null) {
            try {
                unregisterReceiver(systemEventReceiver);
            } catch (IllegalArgumentException e) { }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (id == R.id.action_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        } else if (id == R.id.action_api) {
            startActivity(new Intent(MainActivity.this, HolidayActivity.class));
        } else if (id == R.id.action_service) {
            Intent serviceIntent = new Intent(MainActivity.this, MyForegroundService.class);
            ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
            Toast.makeText(this, "Sync Service Started", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_provider) {
            readViaContentProvider();
        } else if (id == R.id.action_live_alert) {
            if ("CR".equals(userRole) || "Teacher".equals(userRole)) {
                startActivity(new Intent(MainActivity.this, SetLiveAlertActivity.class));
            } else {
                Toast.makeText(this, "Only CR/Teacher can set alerts", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_notice) {
            if ("Teacher".equals(userRole)) {
                startActivity(new Intent(MainActivity.this, TeacherNoticeActivity.class));
            } else {
                Toast.makeText(this, "Only Teachers can send notices", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void readViaContentProvider() {
        Uri uri = Uri.parse("content://com.example.class_routine_manager.provider/RoutineTable");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder data = new StringBuilder();
            while (cursor.moveToNext()) {
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("SUBJECT"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("DAY"));
                data.append("📚 ").append(subject).append(" (").append(day).append(")\n");
            }
            cursor.close();

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Data from Content Provider")
                    .setMessage(data.toString())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "No data found. Please add a class first.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<ClassRoutine> allClasses = sqliteDb.getAllClasses();

        if (isTodayView) {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            String today = days[dayOfWeek];

            List<ClassRoutine> todayClasses = new ArrayList<>();
            for (ClassRoutine c : allClasses) {
                if (c.getDay().equalsIgnoreCase(today)) {
                    todayClasses.add(c);
                }
            }
            updateRecyclerView(todayClasses);
        }
    }

    private void updateRecyclerView(List<ClassRoutine> classList) {
        if (classList.isEmpty()) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerViewClasses.setVisibility(View.GONE);
        } else {
            binding.tvEmptyState.setVisibility(View.GONE);
            binding.recyclerViewClasses.setVisibility(View.VISIBLE);
            ClassAdapter adapter = new ClassAdapter(this, classList);
            binding.recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewClasses.setAdapter(adapter);
        }
    }
}