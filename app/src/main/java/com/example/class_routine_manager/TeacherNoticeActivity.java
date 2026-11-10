package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.class_routine_manager.databinding.ActivityTeacherNoticeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TeacherNoticeActivity extends AppCompatActivity {

    private ActivityTeacherNoticeBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnSendNotice.setOnClickListener(v -> {
            String course = binding.etCourseName.getText().toString().trim();
            String desc = binding.etDescription.getText().toString().trim();
            String date = binding.etDate.getText().toString().trim();
            String time = binding.etTime.getText().toString().trim();

            if (course.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> notice = new HashMap<>();
            notice.put("course", course);
            notice.put("description", desc);
            notice.put("date", date);
            notice.put("time", time);
            notice.put("timestamp", System.currentTimeMillis()); // Sorting er jonno

            db.collection("Notices").add(notice)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Notice Sent Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}