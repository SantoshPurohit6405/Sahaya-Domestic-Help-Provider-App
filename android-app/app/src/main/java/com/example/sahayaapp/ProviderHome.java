package com.example.sahayaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProviderHome extends AppCompatActivity {

    CardView Home, Profile, Settings, Services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.providerhome);

        // Initialize CardViews
        Home = findViewById(R.id.pHome);
        Profile = findViewById(R.id.pProfile);
        Settings = findViewById(R.id.pSettings);
        Services = findViewById(R.id.pServices);

        // Navigate to Profile Page
        Profile.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHome.this, ProviderProfile.class);
            startActivity(intent);
        });

        // Navigate to Services Page
        Services.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderHome.this, Requests.class);
            startActivity(intent);
        });

        // Navigate to Update Profile Page
        Settings.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId != -1) {
                Log.d("ProviderHome", "✅ Opening UpdateProfile with User ID: " + userId);
                Intent intent = new Intent(ProviderHome.this, UpdateProfile.class);
                startActivity(intent);
            } else {
                Log.e("ProviderHome", "❌ User ID not found in SharedPreferences!");
                Toast.makeText(ProviderHome.this, "Error: User ID not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkActiveService();
    }

    // Method to check active service for the provider
    private void checkActiveService() {
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        String providerName = sharedPreferences.getString("username", null); // ✅ Fetching username

        if (providerName == null || providerName.isEmpty()) {
            Log.e("ProviderHome", "❌ Provider name not found in SharedPreferences!");
            return;
        }

        String url = "http://192.168.0.103:3000/active-service-by-name/" + providerName;  // API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("service")) {
                            JSONObject service = response.getJSONObject("service");
                            String serviceName = service.getString("service_name");
                            String customerName = service.getString("customer_name");
                            String status = service.getString("status");

                            if (status.equals("accepted")) {
                                Log.d("ProviderHome", "✅ Active service accepted. Redirecting...");
                                Intent intent = new Intent(ProviderHome.this, ProviderSuccess.class);
                                intent.putExtra("customerName", customerName);
                                intent.putExtra("serviceName", serviceName);
                                startActivity(intent);
                            }
                        } else {
                            Log.d("ProviderHome", "ℹ️ No active service found for provider.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProviderHome.this, "Error parsing service data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ProviderHome.this, "Error fetching active service.", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }
}



