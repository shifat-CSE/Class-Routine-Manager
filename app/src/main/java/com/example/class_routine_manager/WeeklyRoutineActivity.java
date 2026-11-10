package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.class_routine_manager.databinding.ActivityWeeklyRoutineBinding;
import java.util.ArrayList;
import java.util.List;

public class WeeklyRoutineActivity extends AppCompatActivity {

    private ActivityWeeklyRoutineBinding binding;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        db = new DatabaseHelper(this);

        buildWeeklyTable();
    }

    private void buildWeeklyTable() {
        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        List<ClassRoutine> allClasses = db.getAllClasses();

        // Protidin er jonno ekta kore Column (LinearLayout) banabo
        for (String day : days) {
            LinearLayout dayColumn = new LinearLayout(this);
            dayColumn.setOrientation(LinearLayout.VERTICAL);
            dayColumn.setPadding(8, 8, 8, 8);
            dayColumn.setBackground(ContextCompat.getDrawable(this, android.R.drawable.editbox_background_normal));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(8, 0, 8, 0);
            dayColumn.setLayoutParams(params);

            // Day Header
            TextView tvDay = new TextView(this);
            tvDay.setText(day);
            tvDay.setTextSize(18f);
            tvDay.setTypeface(null, Typeface.BOLD);
            tvDay.setTextColor(Color.parseColor("#4F46E5"));
            tvDay.setGravity(Gravity.CENTER);
            tvDay.setPadding(0, 0, 0, 16);
            dayColumn.addView(tvDay);

            // Oi diner class gulo add korbo
            for (ClassRoutine routine : allClasses) {
                if (routine.getDay().equalsIgnoreCase(day)) {
                    TextView tvClass = new TextView(this);
                    tvClass.setText(routine.getStartTime() + " - " + routine.getEndTime() + "\n" + routine.getSubject() + "\n(Room: " + routine.getRoom() + ")");
                    tvClass.setTextSize(14f);
                    tvClass.setPadding(12, 12, 12, 12);
                    tvClass.setTextColor(Color.parseColor("#333333"));
                    tvClass.setBackground(ContextCompat.getDrawable(this, android.R.drawable.editbox_background));

                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textParams.setMargins(0, 0, 0, 12);
                    tvClass.setLayoutParams(textParams);

                    dayColumn.addView(tvClass);
                }
            }

            binding.weeklyRoutineContainer.addView(dayColumn);
        }
    }
}