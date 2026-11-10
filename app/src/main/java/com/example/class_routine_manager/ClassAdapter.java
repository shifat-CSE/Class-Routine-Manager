package com.example.class_routine_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassRoutine> classList;
    private Context context;

    public ClassAdapter(Context context, List<ClassRoutine> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassRoutine routine = classList.get(position);

        holder.tvSubjectName.setText(routine.getSubject());
        holder.tvDetails.setText(routine.getCode() + " | " + routine.getTeacher());
        holder.tvTimeRoom.setText(routine.getRoom() + " | " + routine.getStartTime() + " - " + routine.getEndTime() + " (" + routine.getDay() + ")");
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvDetails, tvTimeRoom;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvTimeRoom = itemView.findViewById(R.id.tvTimeRoom);
        }
    }
}