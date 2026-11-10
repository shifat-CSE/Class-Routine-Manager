package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.class_routine_manager.databinding.ActivitySmartSchedulerBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SmartSchedulerActivity extends AppCompatActivity {

    private ActivitySmartSchedulerBinding binding;
    private List<Session> allSessions = new ArrayList<>();

    // Variable to store selected times
    private int startHour = -1, startMinute = -1;
    private int endHour = -1, endMinute = -1;

    // Session Model
    public static class Session {
        String name;
        int start, finish; // Total minutes
        String timeString;

        public Session(String name, int start, int finish, String timeString) {
            this.name = name;
            this.start = start;
            this.finish = finish;
            this.timeString = timeString;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmartSchedulerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Start Time Picker
        binding.btnSelectStartTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(SmartSchedulerActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                startHour = selectedHour;
                startMinute = selectedMinute;
                binding.btnSelectStartTime.setText(formatTime(startHour, startMinute));
            }, hour, minute, false);
            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();
        });

        // End Time Picker
        binding.btnSelectEndTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(SmartSchedulerActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                endHour = selectedHour;
                endMinute = selectedMinute;
                binding.btnSelectEndTime.setText(formatTime(endHour, endMinute));
            }, hour, minute, false);
            mTimePicker.setTitle("Select End Time");
            mTimePicker.show();
        });

        binding.btnAddSession.setOnClickListener(v -> {
            String name = binding.etSessionName.getText().toString();

            if (name.isEmpty() || startHour == -1 || endHour == -1) {
                Toast.makeText(this, "Please enter name and select both times", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert times to minutes for algorithm comparison
            int startTotalMinutes = (startHour * 60) + startMinute;
            int endTotalMinutes = (endHour * 60) + endMinute;

            if (startTotalMinutes >= endTotalMinutes) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            String timeStr = formatTime(startHour, startMinute) + " - " + formatTime(endHour, endMinute);
            allSessions.add(new Session(name, startTotalMinutes, endTotalMinutes, timeStr));

            Toast.makeText(this, "Session Added", Toast.LENGTH_SHORT).show();

            // Reset fields
            binding.etSessionName.setText("");
            binding.btnSelectStartTime.setText("Select Start Time");
            binding.btnSelectEndTime.setText("Select End Time");
            startHour = -1; endHour = -1;
        });

        binding.btnGenerateSchedule.setOnClickListener(v -> {
            if (allSessions.isEmpty()) {
                Toast.makeText(this, "Add some sessions first!", Toast.LENGTH_SHORT).show();
                return;
            }
            generateGreedySchedule();
        });
    }

    private String formatTime(int hour, int minute) {
        String amPm = (hour >= 12) ? "PM" : "AM";
        int displayHour = (hour > 12) ? (hour - 12) : (hour == 0 ? 12 : hour);
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm);
    }

    private void generateGreedySchedule() {
        // 1. Sort sessions by finish time
        List<Session> sortedSessions = new ArrayList<>(allSessions);
        Collections.sort(sortedSessions, new Comparator<Session>() {
            @Override
            public int compare(Session s1, Session s2) {
                return Integer.compare(s1.finish, s2.finish);
            }
        });

        List<Session> selected = new ArrayList<>();
        List<Session> rejected = new ArrayList<>();

        // 2. Greedy Selection
        if (!sortedSessions.isEmpty()) {
            selected.add(sortedSessions.get(0));
            int lastFinishTime = sortedSessions.get(0).finish;

            for (int i = 1; i < sortedSessions.size(); i++) {
                Session current = sortedSessions.get(i);
                if (current.start >= lastFinishTime) {
                    selected.add(current);
                    lastFinishTime = current.finish;
                } else {
                    rejected.add(current);
                }
            }
        }

        // 3. Display Results
        StringBuilder selectedStr = new StringBuilder("✅ Selected Sessions:\n");
        for (Session s : selected) {
            selectedStr.append(s.name).append(" (").append(s.timeString).append(")\n");
        }

        StringBuilder rejectedStr = new StringBuilder("\n❌ Rejected Sessions:\n");
        for (Session s : rejected) {
            rejectedStr.append(s.name).append(" (").append(s.timeString).append(")\n");
        }

        binding.tvResult.setText(selectedStr.toString() + rejectedStr.toString());
        binding.tvResult.setVisibility(View.VISIBLE);
    }
}