package com.example.class_routine_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import com.example.class_routine_manager.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String userId;
    private String base64Image = "";

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();

        // TOMAR SCREENSHOT THEKE HUBOHU LINGK TA EKHANE BOSANO HOLO
        String dbUrl = "https://classroutinemanager-a41e2-default-rtdb.asia-southeast1.firebasedatabase.app/";
        dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("Profiles");

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            binding.etEmail.setText(mAuth.getCurrentUser().getEmail());
            loadProfileData(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.ivProfileImage.setOnClickListener(v -> openGallery());

        binding.btnSaveProfile.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Name and Phone cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Success")
                    .setMessage("Profile Saved Successfully!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        saveDataToFirebase(name, phone);
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void saveDataToFirebase(String name, String phone) {
        String email = binding.etEmail.getText().toString().trim();
        String role = binding.etRole.getText().toString().trim();
        String department = binding.etDepartment.getText().toString().trim();

        if (role.isEmpty()) role = "Student";

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("email", email);
        profileData.put("role", role);
        profileData.put("phone", phone);
        profileData.put("department", department);
        profileData.put("image", base64Image == null ? "" : base64Image);

        dbRef.child(userId).setValue(profileData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Realtime Database e Save hoyeche!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                Toast.makeText(ProfileActivity.this, "Database Error: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap resizedBitmap = getResizedBitmap(bitmap, 300);
                binding.ivProfileImage.setImageBitmap(resizedBitmap);
                base64Image = encodeImage(resizedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap decodeImage(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    private void loadProfileData(String uid) {
        dbRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String name = task.getResult().child("name").getValue(String.class);
                String role = task.getResult().child("role").getValue(String.class);
                String phone = task.getResult().child("phone").getValue(String.class);
                String department = task.getResult().child("department").getValue(String.class);
                String imageStr = task.getResult().child("image").getValue(String.class);

                if (name != null) binding.etName.setText(name);
                if (role != null) binding.etRole.setText(role);
                if (phone != null) binding.etPhone.setText(phone);
                if (department != null) binding.etDepartment.setText(department);

                if (imageStr != null && !imageStr.isEmpty()) {
                    base64Image = imageStr;
                    Bitmap bitmap = decodeImage(imageStr);
                    if (bitmap != null) binding.ivProfileImage.setImageBitmap(bitmap);
                }
            }
        });
    }
}