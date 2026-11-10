package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.example.class_routine_manager.databinding.ActivityAddClassBinding;

public class AddClassActivity extends AppCompatActivity {

    private ActivityAddClassBinding binding;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);

        // Day Spinner Setup
        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDay.setAdapter(adapter);

        binding.btnSaveClass.setOnClickListener(v -> {
            String subject = binding.etSubject.getText().toString();
            String code = binding.etCode.getText().toString();
            String teacher = binding.etTeacher.getText().toString();
            String room = binding.etRoom.getText().toString();
            String day = binding.spinnerDay.getSelectedItem().toString();
            String startTime = binding.etStartTime.getText().toString();
            String endTime = binding.etEndTime.getText().toString();

            if (subject.isEmpty() || code.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ClassRoutine newClass = new ClassRoutine(0, subject, code, teacher, room, day, startTime, endTime);
            db.addClass(newClass);
            Toast.makeText(this, "Class Saved Successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to Dashboard
        });
    }
}