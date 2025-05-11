package com.anahit.pawmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import java.util.HashMap;
import java.util.Map;

public class OwnerProfileCreationActivity extends AppCompatActivity {
    private static final String TAG = "OwnerProfileCreation";
    private EditText ownerNameEditText, ownerAgeEditText;
    private Spinner ownerGenderSpinner;
    private Button saveOwnerProfileButton;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile_creation);

        // Initialize Cloudinary with your credentials
        Map config = new HashMap();
        config.put("cloud_name", "dmmjc18z9");
        config.put("api_key", "161134658645382");
        config.put("api_secret", "byMd4Ixvx794mNH06dZsNTkfZco");
        MediaManager.init(this, config);

        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        ownerAgeEditText = findViewById(R.id.ownerAgeEditText);
        ownerGenderSpinner = findViewById(R.id.ownerGenderSpinner);
        saveOwnerProfileButton = findViewById(R.id.saveOwnerProfileButton);

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownerGenderSpinner.setAdapter(adapter);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                // Optionally update an ImageView if added to the layout
            }
        });

        saveOwnerProfileButton.setOnClickListener(v -> saveOwnerProfile());
    }

    private void saveOwnerProfile() {
        String name = ownerNameEditText.getText().toString().trim();
        String ageStr = ownerAgeEditText.getText().toString().trim();
        String gender = ownerGenderSpinner.getSelectedItem().toString();

        if (name.isEmpty() || ageStr.isEmpty() || gender.equals("Select Gender")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed: Empty fields or invalid gender selection");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed: Invalid age format - " + e.getMessage());
            return;
        }

        String ownerId = java.util.UUID.randomUUID().toString();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "owner");
        metadata.put("name", name);
        metadata.put("age", age);
        metadata.put("gender", gender);

        // Use a placeholder image or uploaded filePath
        Uri uploadUri = filePath != null ? filePath : Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_owner_placeholder);
        String requestId = MediaManager.get().upload(uploadUri)
                .unsigned("3-pawmatch") // Your unsigned upload preset
                .option("public_id", "owners/" + ownerId)
                .option("context", metadata)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Uploading owner profile...");
                        Toast.makeText(OwnerProfileCreationActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Owner profile uploaded successfully: " + resultData);
                        Toast.makeText(OwnerProfileCreationActivity.this, "Owner profile saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OwnerProfileCreationActivity.this, PetProfileCreationActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        String errorMessage = "Upload failed: RequestId=" + requestId +
                                ", Description=" + (error.getDescription() != null ? error.getDescription() : "N/A") +
                                ", Code=" + (error.getCode() != null ? error.getCode() : "N/A");
                        Log.e(TAG, errorMessage); // Simplified to avoid Throwable issue
                        Toast.makeText(OwnerProfileCreationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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