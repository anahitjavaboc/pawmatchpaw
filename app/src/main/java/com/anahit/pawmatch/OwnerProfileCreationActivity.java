package com.anahit.pawmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class OwnerProfileCreationActivity extends AppCompatActivity {
    private static final String TAG = "OwnerProfileCreation";
    private ImageView ownerImageView;
    private EditText ownerNameEditText, ownerAgeEditText;
    private Spinner ownerGenderSpinner;
    private Button uploadOwnerImageButton, saveOwnerProfileButton;
    private Uri filePath;
    private String imageUrl;
    private DatabaseReference databaseReference;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    filePath = uri;
                    Glide.with(this).load(uri).into(ownerImageView);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile_creation);

        // Initialize Cloudinary using BuildConfig
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
        config.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);
        MediaManager.init(this, config);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_LONG).show();
            // Optionally, redirect to a login activity
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        ownerImageView = findViewById(R.id.ownerImageView);
        uploadOwnerImageButton = findViewById(R.id.uploadOwnerImageButton);
        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        ownerAgeEditText = findViewById(R.id.ownerAgeEditText);
        ownerGenderSpinner = findViewById(R.id.ownerGenderSpinner);
        saveOwnerProfileButton = findViewById(R.id.saveOwnerProfileButton);

        // Setup gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownerGenderSpinner.setAdapter(adapter);

        // Handle image upload
        uploadOwnerImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Handle save and continue
        saveOwnerProfileButton.setOnClickListener(v -> saveOwnerProfile());
    }

    private void saveOwnerProfile() {
        String name = ownerNameEditText.getText().toString().trim();
        String ageStr = ownerAgeEditText.getText().toString().trim();
        String gender = ownerGenderSpinner.getSelectedItem().toString();

        // Validate inputs
        if (name.isEmpty() || ageStr.isEmpty() || gender.equals("Select Gender")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed: Empty fields or invalid gender selection");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid age", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Invalid age format: " + e.getMessage());
            return;
        }

        // Use Firebase Auth user ID as ownerId
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // If an image is selected, upload to Cloudinary
        if (filePath != null) {
            Toast.makeText(this, "Uploading owner profile...", Toast.LENGTH_SHORT).show();
            MediaManager.get().upload(filePath)
                    .unsigned("3-pawmatch")
                    .option("public_id", "owners/" + ownerId)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Upload started for requestId: " + requestId);
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            imageUrl = resultData.get("url").toString();
                            Log.d(TAG, "Owner profile image uploaded successfully: " + imageUrl);
                            saveOwnerToFirebase(ownerId, name, age, gender);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload failed: " + error.getDescription());
                            Toast.makeText(OwnerProfileCreationActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            float progress = (bytes / (float) totalBytes) * 100;
                            Log.d(TAG, "Upload progress: " + progress + "%");
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.w(TAG, "Upload rescheduled: " + error.getDescription());
                        }
                    })
                    .dispatch();
        } else {
            // No image selected, proceed without image URL
            saveOwnerToFirebase(ownerId, name, age, gender);
        }
    }

    private void saveOwnerToFirebase(String ownerId, String name, int age, String gender) {
        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("name", name);
        ownerData.put("age", age);
        ownerData.put("gender", gender);
        if (imageUrl != null) {
            ownerData.put("imageUrl", imageUrl);
        }

        databaseReference.child("users").child(ownerId).setValue(ownerData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Owner profile saved to Firebase successfully");
                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                    // Navigate to PetProfileCreationActivity
                    Intent intent = new Intent(OwnerProfileCreationActivity.this, PetProfileCreationActivity.class);
                    intent.putExtra("ownerId", ownerId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save owner profile to Firebase: " + e.getMessage());
                    Toast.makeText(this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}