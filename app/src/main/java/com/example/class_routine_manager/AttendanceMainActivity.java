package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import com.example.class_routine_manager.databinding.ActivityAttendanceMainBinding;
import java.util.List;

public class AttendanceMainActivity extends AppCompatActivity {

    private ActivityAttendanceMainBinding binding;
    private AttendanceDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = new AttendanceDatabaseHelper(this);

        binding.btnAddCourse.setOnClickListener(v -> {
            startActivity(new Intent(AttendanceMainActivity.this, AddAttendanceCourseActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    private void loadCourses() {
        List<AttendanceCourse> courses = db.getAllCourses();
        AttendanceAdapter adapter = new AttendanceAdapter(this, courses);
        binding.recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAttendance.setAdapter(adapter);
    }
}