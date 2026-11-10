package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.class_routine_manager.databinding.ActivityNotificationBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private FirebaseFirestore db;
    private List<Map<String, Object>> noticeList = new ArrayList<>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        adapter = new NotificationAdapter(noticeList);
        binding.recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNotifications.setAdapter(adapter);

        loadNotices();
    }

    private void loadNotices() {
        db.collection("Notices")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        noticeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            noticeList.add(document.getData());
                        }

                        if (noticeList.isEmpty()) {
                            binding.tvNoNotification.setVisibility(View.VISIBLE);
                            binding.recyclerViewNotifications.setVisibility(View.GONE);
                        } else {
                            binding.tvNoNotification.setVisibility(View.GONE);
                            binding.recyclerViewNotifications.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Error loading data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}