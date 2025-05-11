package com.anahit.pawmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetProfileCreationActivity extends AppCompatActivity {
    private ImageView petImageView;
    private Button uploadImageButton, saveProfileButton;
    private EditText petNameEditText, petAgeEditText, petBreedEditText, petBioEditText;
    private Uri filePath;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_pet);

        // Initialize Cloudinary with your credentials
        Map config = new HashMap();
        config.put("cloud_name", "dmmjc18z9");
        config.put("api_key", "161134658645382");
        config.put("api_secret", "byMd4Ixvx794mNH06dZsNTkfZco");
        MediaManager.init(this, config);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        petImageView = findViewById(R.id.petImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        petNameEditText = findViewById(R.id.petNameEditText);
        petAgeEditText = findViewById(R.id.petAgeEditText);
        petBreedEditText = findViewById(R.id.petBreedEditText);
        petBioEditText = findViewById(R.id.petBioEditText);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                petImageView.setImageURI(filePath);
            }
        });

        uploadImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        saveProfileButton.setOnClickListener(v -> savePetProfile());
    }

    private void savePetProfile() {
        String name = petNameEditText.getText().toString().trim();
        String ageStr = petAgeEditText.getText().toString().trim();
        String breed = petBreedEditText.getText().toString().trim();
        String bio = petBioEditText.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || breed.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (filePath == null) {
            Toast.makeText(this, "Please upload a pet photo", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Cloudinary
        String userId = mAuth.getCurrentUser().getUid();
        String petId = UUID.randomUUID().toString();
        String requestId = MediaManager.get().upload(filePath)
                .unsigned("3-pawmatch") // Your unsigned upload preset
                .option("folder", "pets/" + userId)
                .option("public_id", petId)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(PetProfileCreationActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Optional: Show upload progress
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String photoUrl = (String) resultData.get("secure_url");
                        savePetData(userId, petId, name, age, breed, bio, photoUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(PetProfileCreationActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Handle retry logic if needed
                    }
                })
                .dispatch();
    }

    private void savePetData(String userId, String petId, String name, int age, String breed, String bio, String photoUrl) {
        DatabaseReference petRef = mDatabase.child("users").child(userId).child("pets").child(petId);

        Map<String, Object> petData = new HashMap<>();
        petData.put("name", name);
        petData.put("age", age);
        petData.put("breed", breed);
        petData.put("bio", bio);
        petData.put("photoUrl", photoUrl);

        petRef.setValue(petData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PetProfileCreationActivity.this, "Pet profile saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PetProfileCreationActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PetProfileCreationActivity.this, "Failed to save pet profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}