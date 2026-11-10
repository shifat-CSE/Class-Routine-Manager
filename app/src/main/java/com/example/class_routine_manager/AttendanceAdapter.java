package com.example.class_routine_manager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private Context context;
    private List<AttendanceCourse> courseList;

    public AttendanceAdapter(Context context, List<AttendanceCourse> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceCourse course = courseList.get(position);
        holder.tvCourseName.setText(course.getCourseName());
        holder.tvAttendanceStats.setText("Attended: " + course.getPresentClasses() + " / Total: " + course.getTotalClasses());

        // Click korle Calculator Activity te jabe (ID pathabe)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttendanceCalculatorActivity.class);
            intent.putExtra("COURSE_ID", course.getId());
            intent.putExtra("COURSE_NAME", course.getCourseName());
            intent.putExtra("TOTAL_CLASSES", course.getTotalClasses());
            intent.putExtra("PRESENT_CLASSES", course.getPresentClasses());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvAttendanceStats;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvAttendanceStats = itemView.findViewById(R.id.tvAttendanceStats);
        }
    }
}