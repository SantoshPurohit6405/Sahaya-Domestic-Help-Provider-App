package com.example.sahayaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminPanel extends AppCompatActivity {

    private TextView tvAdminId, tvAdminName, tvAdminEmail, tvAdminPhone, tvAdminLastLogin;
    private Button logout;
    private static final String API_URL = "http://192.168.0.108:3000/admin"; // Replace with your actual backend IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_panel);

        // Initialize UI components
        tvAdminId = findViewById(R.id.admin_id);
        tvAdminName = findViewById(R.id.admin_name);
        tvAdminEmail = findViewById(R.id.admin_email);
        tvAdminPhone = findViewById(R.id.admin_phone);
        tvAdminLastLogin = findViewById(R.id.admin_lastlogin);
        logout = findViewById(R.id.logoutButton);

        // Fetch admin details
        getAdminDetails();

        // Handle logout button click
        logout.setOnClickListener(v -> logoutAdmin());
    }

    private void getAdminDetails() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        Log.d("AdminPanel", "Server Response: " + response.toString());

                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONObject admin = response.getJSONObject("admin"); // Get the admin object

                            int id = admin.getInt("id");
                            String username = admin.getString("username");
                            String email = admin.getString("email");
                            String phone = admin.getString("phone");
                            String lastLogin = admin.optString("last_login", "No record"); // Handle null values

                            // Display data in UI
                            tvAdminId.setText("ID: " + id);
                            tvAdminName.setText("Name: " + username);
                            tvAdminEmail.setText("Email: " + email);
                            tvAdminPhone.setText("Phone: " + phone);
                            tvAdminLastLogin.setText("Last Login: " + lastLogin);
                        } else {
                            Toast.makeText(AdminPanel.this, "No admin found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("AdminPanel", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(AdminPanel.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("AdminPanel", "Network Error: " + error.toString());
                    Toast.makeText(AdminPanel.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void logoutAdmin() {
        // Clear session (if using SharedPreferences, add logic here)
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent intent = new Intent(AdminPanel.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}



