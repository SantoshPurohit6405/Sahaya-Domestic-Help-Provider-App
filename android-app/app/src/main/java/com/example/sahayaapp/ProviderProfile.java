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

public class ProviderProfile extends AppCompatActivity {

    private TextView tvProviderId, tvUserId, tvProviderName, tvProviderPhone, tvProviderEmail,

     tvProviderExperience, tvProviderCreatedAt;
    private Button logout;
    private static final String BASE_API_URL = "http://192.168.0.103:3000/providerprofile/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_profile); // Ensure this XML file exists

        // Initialize UI Elements
        logout = findViewById(R.id.logoutButton2);
        tvProviderId = findViewById(R.id.provider_id);
        tvUserId = findViewById(R.id.provideruser_id);
        tvProviderName = findViewById(R.id.provider_name);
        tvProviderPhone = findViewById(R.id.provider_phone);
        tvProviderEmail = findViewById(R.id.provider_email);
        tvProviderExperience = findViewById(R.id.provider_experience);
        tvProviderCreatedAt = findViewById(R.id.provider_created_at);

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "No logged-in provider found!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } else {
            fetchProviderDetails(userId);
        }

        // Logout Button Click
        logout.setOnClickListener(v -> logoutUser());
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            Log.d("ProviderProfile", "🔄 Refreshing profile after update...");
            fetchProviderDetails(userId); // ✅ Always fetch latest data from API
        } else {
            Log.e("ProviderProfile", "❌ User ID missing. Redirecting to login...");
            redirectToLogin();
        }
    }

    // Fetch Provider Profile from API
    private void fetchProviderDetails(int userId) {
        String fullUrl = BASE_API_URL + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Log.d("ProviderProfile", "🌍 Fetching provider data from URL: " + fullUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null,
                response -> {
                    Log.d("ProviderProfile", "✅ API Response: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject provider = response.getJSONObject("provider");

                            // Fetch values safely
                            int providerId = provider.optInt("provider_id", -1);
                            int userIdFromAPI = provider.optInt("user_id", -1);
                            String name = provider.optString("provider_name", "N/A");
                            String phone = provider.optString("phone", "N/A");
                            String email = provider.optString("email", "N/A");
                            String services = provider.optString("services", "N/A");
                            String experience = provider.optString("experience", "N/A");
                            String createdAt = provider.optString("created_at", "N/A");

                            // Display data
                            tvProviderId.setText("Provider ID: " + providerId);
                            tvUserId.setText("User ID: " + userIdFromAPI);
                            tvProviderName.setText("Name: " + name);
                            tvProviderPhone.setText("Phone: " + phone);
                            tvProviderEmail.setText("Email: " + email);
                            tvProviderExperience.setText("Experience: " + experience);
                            tvProviderCreatedAt.setText("Joined On: " + createdAt);
                        } else {
                            Toast.makeText(ProviderProfile.this, "No provider found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ProviderProfile", "❌ JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(ProviderProfile.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ProviderProfile", "❌ API Error: " + error.toString());
                    Toast.makeText(ProviderProfile.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    // Redirect User to Login Page
    private void redirectToLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // ✅ Ensure all user session data is cleared
        editor.apply();

        Intent intent = new Intent(ProviderProfile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Logout and Clear SharedPreferences
    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // ✅ Ensure logout clears the correct session data
        editor.apply();

        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }
}


