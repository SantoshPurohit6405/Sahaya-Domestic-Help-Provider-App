package com.example.sahayaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingSuccessActivity extends AppCompatActivity {

    private ImageView tickImage;
    private TextView bookingMessage, confirmationMessage;
    private Button backToHomeBtn, chatWithProviderBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        tickImage = findViewById(R.id.successIcon);
        bookingMessage = findViewById(R.id.successMessage);
        confirmationMessage = findViewById(R.id.confirmationMessage);
        backToHomeBtn = findViewById(R.id.backToHomeBtn);
        chatWithProviderBtn = findViewById(R.id.chatWithProviderBtn);

        // Initially hide all views except the tick image (for splash effect)
        bookingMessage.setVisibility(View.INVISIBLE);
        confirmationMessage.setVisibility(View.INVISIBLE);
        backToHomeBtn.setVisibility(View.INVISIBLE);
        chatWithProviderBtn.setVisibility(View.INVISIBLE);

        // Simulate splash effect with 1.5 seconds delay
        new Handler().postDelayed(() -> {
            bookingMessage.setVisibility(View.VISIBLE);
            confirmationMessage.setVisibility(View.VISIBLE);
            backToHomeBtn.setVisibility(View.VISIBLE);
            chatWithProviderBtn.setVisibility(View.VISIBLE);
        }, 1500);

        // Retrieve provider info passed from previous activity
        Intent intent = getIntent();
        String providerId = intent.getStringExtra("providerId");
        String providerName = intent.getStringExtra("providerName");

        // Button to return to home
        backToHomeBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(BookingSuccessActivity.this, CustomerHome.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        // Button to chat with provider
        chatWithProviderBtn.setOnClickListener(v -> {
            Intent chatIntent = new Intent(BookingSuccessActivity.this, CustomerHome.class);
            chatIntent.putExtra("providerId", providerId);
            chatIntent.putExtra("providerName", providerName);
            startActivity(chatIntent);
        });
    }
}

