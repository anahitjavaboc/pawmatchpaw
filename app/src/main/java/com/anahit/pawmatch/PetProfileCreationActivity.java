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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetProfileCreationActivity extends AppCompatActivity {
    private static final String TAG = "PetProfileCreation";
    private ImageView petImageView;
    private Button uploadImageButton, saveProfileButton;
    private EditText petNameEditText, petAgeEditText, petBreedEditText, petBioEditText;
    private Uri filePath;
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

        String petId = UUID.randomUUID().toString();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "pet");
        metadata.put("name", name);
        metadata.put("age", age);
        metadata.put("breed", breed);
        metadata.put("bio", bio);

        String requestId = MediaManager.get().upload(filePath)
                .unsigned("3-pawmatch") // Your unsigned upload preset
                .option("public_id", "pets/" + petId)
                .option("context", metadata)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Uploading pet profile...");
                        Toast.makeText(PetProfileCreationActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Pet profile uploaded successfully: " + resultData);
                        Toast.makeText(PetProfileCreationActivity.this, "Pet profile saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PetProfileCreationActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        String errorMessage = "Upload failed: RequestId=" + requestId +
                                ", Description=" + (error.getDescription() != null ? error.getDescription() : "N/A") +
                                ", Code=" + (error.getCode() != null ? error.getCode() : "N/A");
                        Log.e(TAG, errorMessage); // Simplified to avoid Throwable issue
                        Toast.makeText(PetProfileCreationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Optional: Show upload progress
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Handle retry logic if needed
                    }
                })
                .dispatch();
    }
}