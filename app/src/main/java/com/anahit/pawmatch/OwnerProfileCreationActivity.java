package com.anahit.pawmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class OwnerProfileCreationActivity extends AppCompatActivity {
    private EditText ownerNameEditText, ownerAgeEditText;
    private Spinner ownerGenderSpinner;
    private Button saveOwnerProfileButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile_creation);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        ownerAgeEditText = findViewById(R.id.ownerAgeEditText);
        ownerGenderSpinner = findViewById(R.id.ownerGenderSpinner);
        saveOwnerProfileButton = findViewById(R.id.saveOwnerProfileButton);

        // Set up gender spinner
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
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("name", name);
        ownerData.put("age", age);
        ownerData.put("gender", gender);

        mDatabase.child("users").child(userId).setValue(ownerData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(OwnerProfileCreationActivity.this, "Owner profile saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OwnerProfileCreationActivity.this, PetProfileCreationActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OwnerProfileCreationActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}