package com.example.sahayaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class ProviderSuccess extends AppCompatActivity {

    private TextView successMessage, confirmMessage;
    private Button goToHome;
    private String serviceId;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_success);

        successMessage = findViewById(R.id.successMessage1);
        confirmMessage = findViewById(R.id.confirmationMessage1);
        goToHome = findViewById(R.id.EndService);
        queue = Volley.newRequestQueue(this);

        fetchServiceDetails(); // Initial check
    }

    private void fetchServiceDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        String providerName = sharedPreferences.getString("username", null);

        if (providerName == null || providerName.isEmpty()) {
            Log.e("ProviderSuccess", "❌ Provider name not found in SharedPreferences!");
            return;
        }

        String url = "http://192.168.0.103:3000/active-service-by-name/" + providerName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("service")) {
                            JSONObject service = response.getJSONObject("service");
                            serviceId = service.getString("service_id");
                            String serviceName = service.getString("service_name");
                            String customerName = service.getString("customer_name");
                            String status = service.getString("status");

                            if (status.equals("completed")) {
                                // Service already completed, redirect to home
                                Toast.makeText(this, "Service already completed.", Toast.LENGTH_SHORT).show();
                                goHome();
                            } else if (status.equals("accepted")) {
                                // Show UI and allow ending the service
                                successMessage.setText("You are now providing the service to " + customerName);
                                confirmMessage.setText("Service: " + serviceName);

                                goToHome.setOnClickListener(v -> endService(serviceId));
                            }
                        } else {
                            Log.d("ProviderSuccess", "ℹ️ No active service found for provider.");
                            Toast.makeText(this, "No active service found.", Toast.LENGTH_SHORT).show();
                            goHome();  // Redirect to home if service is not active
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing service data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error fetching service.", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

    private void endService(String serviceId) {
        String url = "http://192.168.0.103:3000/end-service/" + serviceId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
                response -> {
                    try {
                        if (response.has("message")) {
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            // Check status again and navigate if completed
                            fetchServiceDetails();
                        } else {
                            Toast.makeText(this, "Failed to end service.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error completing service.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error completing service.", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

    private void goHome() {
        Intent intent = new Intent(ProviderSuccess.this, ProviderHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current screen
    }
}








