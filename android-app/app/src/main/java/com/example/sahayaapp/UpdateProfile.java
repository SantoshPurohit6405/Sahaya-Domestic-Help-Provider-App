package com.example.sahayaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etPassword;
    private Button btnUpdate;
    private int userId;
    private String token, userRole;
    private static final String BASE_API_URL = "http://192.168.0.103:3000/update-profile/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateprofile);

        // Initialize UI Elements
        etUsername = findViewById(R.id.editName);
        etEmail = findViewById(R.id.editEmail);
        etPhone = findViewById(R.id.editPhone);
        etPassword = findViewById(R.id.editPassword);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Retrieve user ID and token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1); // Retrieve user ID as int
        token = sharedPreferences.getString("jwt_token", null); // Retrieve token
        userRole = sharedPreferences.getString("user_role", ""); // Retrieve user role

        // Debugging logs
        Log.d("UpdateProfile", "🛠 Retrieved User ID: " + userId);
        Log.d("UpdateProfile", "🛠 Stored Token: " + (token != null ? token : "No token found"));
        Log.d("UpdateProfile", "🛠 User Role: " + userRole);

        if (userId == -1 || token == null) {
            Log.e("UpdateProfile", "❌ ERROR: User ID or Token missing from SharedPreferences!");
            Toast.makeText(this, "Error: User not authenticated. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Close activity to prevent crashes
            return;
        }

        // Set Update Button Click Listener
        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        final String username = etUsername.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (username.isEmpty() && email.isEmpty() && phone.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "At least one field must be updated", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullUrl = BASE_API_URL + userId; // ✅ Send user ID in URL
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("UpdateProfile", "🌍 Sending request to: " + fullUrl);
        Toast.makeText(this, "Updating profile...", Toast.LENGTH_SHORT).show(); // Show loading toast

        StringRequest request = new StringRequest(Request.Method.PUT, fullUrl,
                response -> {
                    Log.d("UpdateProfile", "✅ API Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");

                        // ✅ Store updated data in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        if (!username.isEmpty()) editor.putString("username", username);
                        if (!email.isEmpty()) editor.putString("email", email);
                        if (!phone.isEmpty()) editor.putString("phone", phone);
                        editor.apply();

                        Toast.makeText(UpdateProfile.this, message, Toast.LENGTH_SHORT).show();

                        // ✅ Redirect user to their respective profile page after successful update
                        if (userRole.equals("Customer")) {
                            Intent intent = new Intent(UpdateProfile.this, CustomerProfile.class);
                            startActivity(intent);
                        } else if (userRole.equals("Service Provider")) {
                            Intent intent = new Intent(UpdateProfile.this, ProviderProfile.class);
                            startActivity(intent);
                        } else {
                            Log.e("UpdateProfile", "❌ Unknown role: " + userRole);
                        }
                        finish(); // Close UpdateProfile activity

                    } catch (JSONException e) {
                        Log.e("UpdateProfile", "❌ Error parsing response: " + e.getMessage());
                        Toast.makeText(UpdateProfile.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("UpdateProfile", "❌ API Error: " + error.toString());
                    Toast.makeText(UpdateProfile.this, "Error updating profile!", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!username.isEmpty()) params.put("username", username);
                if (!email.isEmpty()) params.put("email", email);
                if (!phone.isEmpty()) params.put("phone", phone);
                if (!password.isEmpty()) params.put("password", password);
                return params;
            }
        };

        queue.add(request);
    }
}












