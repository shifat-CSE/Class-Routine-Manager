package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.class_routine_manager.databinding.ActivityAttendanceCalculatorBinding;

public class AttendanceCalculatorActivity extends AppCompatActivity {

    private ActivityAttendanceCalculatorBinding binding;
    private AttendanceDatabaseHelper db;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new AttendanceDatabaseHelper(this);

        // Adapter theke pathano data receive kora
        courseId = getIntent().getIntExtra("COURSE_ID", -1);
        String courseName = getIntent().getStringExtra("COURSE_NAME");
        int total = getIntent().getIntExtra("TOTAL_CLASSES", 0);
        int present = getIntent().getIntExtra("PRESENT_CLASSES", 0);

        binding.tvCourseTitle.setText(courseName);

        // Jodi purono data thake, seta input e dekhabe
        if (total > 0) {
            binding.etTotalClasses.setText(String.valueOf(total));
            binding.etPresentClasses.setText(String.valueOf(present));
            calculateResult(total, present);
        }

        binding.btnCalculate.setOnClickListener(v -> {
            String totalStr = binding.etTotalClasses.getText().toString();
            String presentStr = binding.etPresentClasses.getText().toString();

            if (totalStr.isEmpty() || presentStr.isEmpty()) {
                Toast.makeText(this, "Please enter both numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalClasses = Integer.parseInt(totalStr);
            int presentClasses = Integer.parseInt(presentStr);

            if (presentClasses > totalClasses) {
                Toast.makeText(this, "Attended classes cannot be greater than total!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Database update kora
            db.updateAttendance(courseId, totalClasses, presentClasses);

            calculateResult(totalClasses, presentClasses);
        });

        // Back Button Click
        binding.btnBack.setOnClickListener(v -> {
            finish(); // Eta activity close kore ager page e niye jabe
        });
    }

    private void calculateResult(int total, int present) {
        if (total == 0) return;

        double percentage = ((double) present / total) * 100;
        double roundedPercentage = Math.round(percentage * 10.0) / 10.0;

        binding.tvResultPercentage.setText(roundedPercentage + "%");
        binding.tvResultMessage.setVisibility(View.VISIBLE);

        if (percentage >= 60.0) {
            binding.tvResultPercentage.setTextColor(Color.parseColor("#10B981")); // Green
            binding.tvResultMessage.setText("You are in safe zone.");
            binding.tvResultMessage.setTextColor(Color.parseColor("#10B981"));
        } else {
            binding.tvResultPercentage.setTextColor(Color.parseColor("#EF4444")); // Red
            binding.tvResultMessage.setText("Contact to your course teacher as soon as possible.");
            binding.tvResultMessage.setTextColor(Color.parseColor("#EF4444"));
        }
    }
}