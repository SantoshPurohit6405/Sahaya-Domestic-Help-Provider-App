package com.example.sahayaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CustomerProfile extends AppCompatActivity {

    private TextView tvCustomerId, tvCustomerName, tvCustomerEmail, tvCustomerPhone, tvCustomerCreatedAt;
    private Button logout;
    private static final String BASE_API_URL = "http://192.168.0.103:3000/customerprofile/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile);

        // Initialize UI Elements
        logout = findViewById(R.id.logoutButton1);
        tvCustomerId = findViewById(R.id.customer_id);
        tvCustomerName = findViewById(R.id.customer_name);
        tvCustomerEmail = findViewById(R.id.customer_email);
        tvCustomerPhone = findViewById(R.id.customer_phone);
        tvCustomerCreatedAt = findViewById(R.id.customer_created_at);

        // Retrieve customer session data
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1); // Ensure correct key
        String token = sharedPreferences.getString("jwt_token", null);

// Debugging logs
        Log.d("CustomerProfile", "🛠 Stored Customer ID: " + userId);
        Log.d("CustomerProfile", "🛠 Stored Token: " + token);

        if (userId == -1 || token == null) {
            Toast.makeText(this, "No logged-in customer found!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } else {
            fetchCustomerDetails(userId, token);
        }

        // Logout Button Click
        logout.setOnClickListener(v -> logoutUser());
    }

    // Fetch Customer Profile from API
    private void fetchCustomerDetails(int customerId, String token) {
        String fullUrl = BASE_API_URL + "?id=" + customerId; // Ensure ID is sent in query params
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Log.d("CustomerProfile", "🌍 Fetching data from URL: " + fullUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null,
                response -> {
                    Log.d("CustomerProfile", "✅ API Response: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject customer = response.getJSONObject("customer");

                            int id = customer.optInt("id", -1);
                            String username = customer.optString("username", "N/A");
                            String email = customer.optString("email", "N/A");
                            String phone = customer.optString("phone", "N/A");
                            String createdAt = customer.optString("created_at", "N/A");

                            // Display fetched data
                            tvCustomerId.setText("ID: " + id);
                            tvCustomerName.setText("Name: " + username);
                            tvCustomerEmail.setText("Email: " + email);
                            tvCustomerPhone.setText("Phone: " + phone);
                            tvCustomerCreatedAt.setText("Joined On: " + createdAt);
                        } else {
                            Toast.makeText(CustomerProfile.this, "⚠️ No customer found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("CustomerProfile", "❌ Error parsing response: " + e.getMessage());
                        Toast.makeText(CustomerProfile.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CustomerProfile", "❌ API Error: " + error.toString());
                    Toast.makeText(CustomerProfile.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    // Redirect User to Login Page
    private void redirectToLogin() {
        Intent intent = new Intent(CustomerProfile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Logout and Clear SharedPreferences
    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear user data
        editor.apply();

        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }
}





