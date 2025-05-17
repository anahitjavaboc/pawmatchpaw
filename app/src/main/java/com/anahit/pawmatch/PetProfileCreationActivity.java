package com.anahit.pawmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PetProfileCreationActivity extends AppCompatActivity {
    private static final String TAG = "PetProfileCreation";
    private ImageView petImageView;
    private Button uploadImageButton, saveProfileButton;
    private EditText petNameEditText, petAgeEditText, petBreedEditText, petBioEditText;
    private Uri filePath;
    private String imageUrl;
    private DatabaseReference databaseReference;
    private String ownerId;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    filePath = uri;
                    Glide.with(this).load(uri).into(petImageView);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_pet);

        // Initialize Cloudinary using BuildConfig
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
        config.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);
        MediaManager.init(this, config);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get ownerId from intent
        ownerId = getIntent().getStringExtra("ownerId");
        if (ownerId == null) {
            Toast.makeText(this, "Error: Owner ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        petImageView = findViewById(R.id.petImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        petNameEditText = findViewById(R.id.petNameEditText);
        petAgeEditText = findViewById(R.id.petAgeEditText);
        petBreedEditText = findViewById(R.id.petBreedEditText);
        petBioEditText = findViewById(R.id.petBioEditText);

        // Handle image upload
        uploadImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Handle save and continue
        saveProfileButton.setOnClickListener(v -> savePetProfile());
    }

    private void savePetProfile() {
        String petName = petNameEditText.getText().toString().trim();
        String petAgeStr = petAgeEditText.getText().toString().trim();
        String petBreed = petBreedEditText.getText().toString().trim();
        String petBio = petBioEditText.getText().toString().trim();

        // Validate inputs
        if (petName.isEmpty() || petAgeStr.isEmpty() || petBreed.isEmpty() || petBio.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed: Empty fields");
            return;
        }

        if (filePath == null) {
            Toast.makeText(this, "Please upload a pet photo", Toast.LENGTH_SHORT).show();
            return;
        }

        int petAge;
        try {
            petAge = Integer.parseInt(petAgeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid age", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Invalid age format: " + e.getMessage());
            return;
        }

        String petId = databaseReference.child("pets").push().getKey();
        if (petId == null) {
            Toast.makeText(this, "Error: Unable to generate pet ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Cloudinary
        Toast.makeText(this, "Uploading pet profile...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(filePath)
                .unsigned("3-pawmatch")
                .option("public_id", "pets/" + petId)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started for requestId: " + requestId);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        imageUrl = resultData.get("url").toString();
                        Log.d(TAG, "Pet profile image uploaded successfully: " + imageUrl);
                        savePetToFirebase(petId, petName, petAge, petBreed, petBio);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload failed: " + error.getDescription());
                        Toast.makeText(PetProfileCreationActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
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
    }

    private void savePetToFirebase(String petId, String petName, int petAge, String petBreed, String petBio) {
        Map<String, Object> petData = new HashMap<>();
        petData.put("petId", petId);
        petData.put("ownerId", ownerId);
        petData.put("name", petName);
        petData.put("age", petAge);
        petData.put("breed", petBreed);
        petData.put("bio", petBio);
        petData.put("imageUrl", imageUrl);

        // Save pet under owner's node and global pets node
        databaseReference.child("users").child(ownerId).child("pets").child(petId).setValue(petId)
                .addOnSuccessListener(aVoid -> {
                    databaseReference.child("pets").child(petId).setValue(petData)
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "Pet profile saved to Firebase successfully");
                                Toast.makeText(this, "Pet profile saved!", Toast.LENGTH_SHORT).show();
                                // Navigate to MainActivity (or your next screen)
                                Intent intent = new Intent(PetProfileCreationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to save pet profile to Firebase: " + e.getMessage());
                                Toast.makeText(this, "Failed to save pet profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to link pet to owner in Firebase: " + e.getMessage());
                    Toast.makeText(this, "Failed to save pet profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}