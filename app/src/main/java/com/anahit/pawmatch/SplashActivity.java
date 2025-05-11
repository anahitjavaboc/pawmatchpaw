package com.anahit.pawmatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Set the splash screen layout

        // Use Handler with Looper to delay the redirection
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Already logged in → go to MainActivity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    // Not logged in → go to LoginActivity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish(); // Close SplashActivity
            }
        }, SPLASH_DELAY);
    }
}