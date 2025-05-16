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
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OwnerProfileCreationActivity extends AppCompatActivity {
    private static final String TAG = "OwnerProfileCreation";
    private EditText ownerNameEditText, ownerAgeEditText;
    private Spinner ownerGenderSpinner;
    private Button saveOwnerProfileButton;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile_creation);

        // Initialize Cloudinary
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dmmjc18z9");
        config.put("api_key", "161134658645382");
        config.put("api_secret", "byMd4Ixvx794mNH06dZsNTkfZco");
        MediaManager.init(this, config);

        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        ownerAgeEditText = findViewById(R.id.ownerAgeEditText);
        ownerGenderSpinner = findViewById(R.id.ownerGenderSpinner);
        saveOwnerProfileButton = findViewById(R.id.saveOwnerProfileButton);

        // Setup gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownerGenderSpinner.setAdapter(adapter);

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
            Toast.makeText(this, "Enter a valid age", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Invalid age format: " + e.getMessage());
            return;
        }

        String ownerId = UUID.randomUUID().toString();
        Uri uploadUri = filePath != null ? filePath : Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_owner_placeholder);

        Toast.makeText(this, "Uploading owner profile...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(uploadUri)
                .unsigned("3-pawmatch")
                .option("public_id", "owners/" + ownerId)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started for requestId: " + requestId);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Owner profile uploaded successfully.");
                        Toast.makeText(OwnerProfileCreationActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();

                        // Ensure navigation happens immediately after success
                        Intent intent = new Intent(OwnerProfileCreationActivity.this, PetProfileCreationActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload failed: " + error.getDescription());
                        Toast.makeText(OwnerProfileCreationActivity.this, "Upload failed! Please try again.", Toast.LENGTH_LONG).show();
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
}
