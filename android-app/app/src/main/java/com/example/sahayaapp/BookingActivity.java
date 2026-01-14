package com.example.sahayaapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BookingActivity extends AppCompatActivity {

    private TextView providerNameTextView, serviceNameTextView;
    private Button bookNowButton;

    private String providerName, serviceName;
    private int providerId, serviceId;

    private String customerId, customerName, customerEmail, customerPhone;

    private ProgressDialog progressDialog;

    private static final long PROVIDER_RESPONSE_TIMEOUT = 2 * 60 * 1000; // 5 minutes
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_card); // Make sure this layout exists and is correct

        initializeViews();
        loadUserFromSharedPrefs();
        receiveIntentData();
        populateBookingInfo();

        bookNowButton.setOnClickListener(v -> showConfirmationDialog());
    }

    private void initializeViews() {
        providerNameTextView = findViewById(R.id.providerName);
        serviceNameTextView = findViewById(R.id.providerService);
        bookNowButton = findViewById(R.id.bookNowBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing booking...");
        progressDialog.setCancelable(false);

        timeoutHandler = new Handler();
    }

    private void loadUserFromSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        customerId = prefs.getString("user_id", null);
        customerName = prefs.getString("user_name", null);
        customerEmail = prefs.getString("user_email", null);
        customerPhone = prefs.getString("user_phone", null);
    }

    private void receiveIntentData() {
        Intent intent = getIntent();
        providerName = intent.getStringExtra("provider_name");
        serviceName = intent.getStringExtra("service_name");
        providerId = intent.getIntExtra("provider_id", -1);
        serviceId = intent.getIntExtra("service_id", -1);
    }

    private void populateBookingInfo() {
        providerNameTextView.setText("Provider: " + providerName);
        serviceNameTextView.setText("Service: " + serviceName);
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Booking")
                .setMessage("Do you want to book this service provider?")
                .setPositiveButton("Yes", (dialog, which) -> sendBookingRequest())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendBookingRequest() {
        progressDialog.show();

        String url = "http://192.168.0.103:3000/create-booking"; // Replace with your actual server URL

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject bookingData = new JSONObject();

        try {
            // Send customer info
            bookingData.put("customer_id", customerId);
            bookingData.put("customer_name", customerName);
            bookingData.put("customer_email", customerEmail);
            bookingData.put("customer_phone", customerPhone);

            // Send provider info (only ID; backend will fetch name, phone, email)
            bookingData.put("provider_id", providerId);

            // Service details
            bookingData.put("service_id", serviceId);
            bookingData.put("service_name", serviceName);

            // Status
            bookingData.put("status", "pending");

        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Error creating booking request!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                bookingData,
                response -> {
                    startProviderResponseTimeout();
                    handleProviderResponse(response);
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMessage = "Booking failed!";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String data = new String(error.networkResponse.data);
                            JSONObject errObj = new JSONObject(data);
                            errorMessage = errObj.optString("message", errorMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void handleProviderResponse(JSONObject response) {
        progressDialog.dismiss();

        String status = response.optString("status", "pending");

        if ("accepted".equalsIgnoreCase(status)) {
            navigateToSuccessScreen();
        } else if ("rejected".equalsIgnoreCase(status)) {
            showMessage("Service Rejected by provider.");
        } else {
            showMessage("Waiting for provider's response...");
        }
    }

    private void startProviderResponseTimeout() {
        timeoutRunnable = () -> {
            showMessage("Service Rejected - Provider did not respond in time.");
        };

        timeoutHandler.postDelayed(timeoutRunnable, PROVIDER_RESPONSE_TIMEOUT);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        if (message.contains("Rejected")) {
            Intent intent = new Intent(BookingActivity.this, CustomerSearch.class);
            startActivity(intent);
            finish();
        }
    }

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(this, BookingSuccessActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
        super.onDestroy();
    }
}















