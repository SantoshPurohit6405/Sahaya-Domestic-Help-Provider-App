package com.example.sahayaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find Views
        ImageView splashImage = findViewById(R.id.splashImage);
        TextView appName = findViewById(R.id.appName);

        // Load Animations
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply Animations
        splashImage.startAnimation(bounce);

        // Delay app name animation to start after logo animation
        new Handler().postDelayed(() -> appName.startAnimation(fadeIn), 1500);

        // Stay on splash screen for 3 seconds before switching to Login
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3 seconds
    }
}


