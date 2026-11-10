package com.example.class_routine_manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Map<String, Object>> noticeList;

    public NotificationAdapter(List<Map<String, Object>> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> notice = noticeList.get(position);

        holder.tvCourse.setText(String.valueOf(notice.get("course")));
        holder.tvDesc.setText(String.valueOf(notice.get("description")));

        String date = notice.get("date") != null ? notice.get("date").toString() : "";
        String time = notice.get("time") != null ? notice.get("time").toString() : "";
        holder.tvDateTime.setText("📅 " + date + " | ⏰ " + time);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvDesc, tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tvNoticeCourse);
            tvDesc = itemView.findViewById(R.id.tvNoticeDesc);
            tvDateTime = itemView.findViewById(R.id.tvNoticeDateTime);
        }
    }
}