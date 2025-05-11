package com.anahit.pawmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize logoutButton with findViewById
        logoutButton = findViewById(R.id.logoutButton);

        // Set OnClickListener for logoutButton
        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            // Log or handle the case where logoutButton is not found
            android.util.Log.e("MainActivity", "logoutButton not found in activity_main layout");
        }
    }
}