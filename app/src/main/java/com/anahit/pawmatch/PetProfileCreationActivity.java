package com.anahit.pawmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.cloudinary.utils.ObjectUtils;



public class PetProfileCreationActivity extends AppCompatActivity {
    private static final String TAG = "PetProfileCreation";
    private ImageView petImageView;
    private Button uploadImageButton, saveProfileButton;
    private EditText petNameEditText, petAgeEditText, petBreedEditText, petBioEditText;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_pet);

        // Initialize Cloudinary
        Map<String, Object> config = new HashMap<>();
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

        uploadImageButton.setOnClickListener(v -> selectImage());
        saveProfileButton.setOnClickListener(v -> savePetProfile());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            petImageView.setImageURI(filePath);
        }
    }

    private void savePetProfile() {
        if (filePath == null) {
            Toast.makeText(this, "Please upload a pet photo", Toast.LENGTH_SHORT).show();
            return;
        }

        String petId = UUID.randomUUID().toString();

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
                        Log.d(TAG, "Pet profile uploaded successfully.");
                        Toast.makeText(PetProfileCreationActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity after success
                        Intent intent = new Intent(PetProfileCreationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload failed: " + error.getDescription());
                        Toast.makeText(PetProfileCreationActivity.this, "Upload failed! Please try again.", Toast.LENGTH_LONG).show();
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
    public void uploadImage(Uri imageUri) {
        Cloudinary cloudinary = CloudinaryConfig.getCloudinary();
        try {
            Map uploadResult = cloudinary.uploader().upload(imageUri.getPath(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "public_id", "pet_image_" + System.currentTimeMillis()
            ));
            String imageUrl = uploadResult.get("secure_url").toString();
            // Save imageUrl to Realtime Database with pet data
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

