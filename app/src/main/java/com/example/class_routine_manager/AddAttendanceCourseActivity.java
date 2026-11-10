package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.class_routine_manager.databinding.ActivityAddAttendanceCourseBinding;

public class AddAttendanceCourseActivity extends AppCompatActivity {

    private ActivityAddAttendanceCourseBinding binding;
    private AttendanceDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAttendanceCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new AttendanceDatabaseHelper(this);

        // Toolbar e back button er kaj
        binding.toolbar.setNavigationOnClickListener(v -> {
            finish(); // Eta activity close kore ager page e (Attendance Dashboard) niye jabe
        });

        binding.btnSaveCourse.setOnClickListener(v -> {
            String courseName = binding.etCourseName.getText().toString().trim();
            if (courseName.isEmpty()) {
                Toast.makeText(this, "Enter course name", Toast.LENGTH_SHORT).show();
                return;
            }
            db.addCourse(courseName);
            Toast.makeText(this, "Course Added!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}