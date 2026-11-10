package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.class_routine_manager.databinding.ActivitySetLiveAlertBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetLiveAlertActivity extends AppCompatActivity {

    private ActivitySetLiveAlertBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetLiveAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnSendLiveAlert.setOnClickListener(v -> {
            String message = binding.etLiveMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Realtime Database-e data pathano
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("LiveAlert");

            myRef.setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SetLiveAlertActivity.this, "Live Alert Sent Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Page close kore dashboard e jabe
                } else {
                    Toast.makeText(SetLiveAlertActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}