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

public class CustomerHome extends AppCompatActivity {

    CardView Home, Profile, Settings, Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        // Initialize CardViews
        Home = findViewById(R.id.CustomerHome);
        Profile = findViewById(R.id.CustomerProfile);
        Settings = findViewById(R.id.CustomerSettings);
        Search = findViewById(R.id.CustomerSearch);

        // Navigate to Profile Page
        Profile.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHome.this, CustomerProfile.class);
            startActivity(intent);
        });

        // Navigate to Search Page
        Search.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHome.this, CustomerSearch.class);
            startActivity(intent);
        });

        // Navigate to Update Profile Page
        Settings.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId != -1) {
                Log.d("CustomerHome", "✅ Opening UpdateProfile with User ID: " + userId);
                Intent intent = new Intent(CustomerHome.this, UpdateProfile.class);
                startActivity(intent);
            } else {
                Log.e("CustomerHome", "❌ User ID not found in SharedPreferences!");
                Toast.makeText(CustomerHome.this, "Error: User ID not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkActiveService();
    }

    private void checkActiveService() {
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        String customerName = sharedPreferences.getString("username", null); // ✅ Fetching username

        if (customerName == null || customerName.isEmpty()) {
            Log.e("CustomerHome", "❌ Customer name not found in SharedPreferences!");
            return;
        }

        String url = "http://192.168.0.103:3000/active-service-by-name/" + customerName;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("service")) {
                            JSONObject service = response.getJSONObject("service");
                            String serviceName = service.getString("service_name");
                            String providerName = service.getString("provider_name");
                            String status = service.getString("status");

                            if (status.equals("accepted")) {
                                Log.d("CustomerHome", "✅ Booking accepted. Redirecting...");
                                Intent intent = new Intent(CustomerHome.this, BookingSuccessActivity.class);
                                intent.putExtra("providerName", providerName);
                                intent.putExtra("serviceName", serviceName);
                                startActivity(intent);
                            }
                        } else {
                            Log.d("CustomerHome", "ℹ️ No active bookings found.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CustomerHome.this, "Error parsing booking data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(CustomerHome.this, "Error fetching active service.", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }
}




