package com.example.sahayaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RoleSelection extends AppCompatActivity {
    private Button btnCustomer, btnServiceProvider;
    private SharedPreferences sharedPreferences;
    private String phone;
    private ImageView roleImage;
    private TextView roleText;
    private Animation fadeIn, slideUp;

    private static final String BASE_URL = "http://192.168.0.103:3000"; // Update with actual server IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roleselection);

        initializeUI();
        loadAnimations();
        retrievePhoneFromSharedPreferences(); // ✅ Retrieve correct phone number

        // Set click listeners for role selection
        btnCustomer.setOnClickListener(v -> updateRole("Customer", LoginActivity.class));
        btnServiceProvider.setOnClickListener(v -> updateRole("Service Provider", ServiceSelection.class));
    }

    private void initializeUI() {
        sharedPreferences = getSharedPreferences("SahayaPrefs", Context.MODE_PRIVATE);

        btnCustomer = findViewById(R.id.btnCustomer);
        btnServiceProvider = findViewById(R.id.btnServiceProvider);
        roleImage = findViewById(R.id.RoleImage);
        roleText = findViewById(R.id.RoleName);
    }

    private void loadAnimations() {
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_long);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_long);

        roleImage.startAnimation(fadeIn);
        roleText.startAnimation(slideUp);
    }

    private void retrievePhoneFromSharedPreferences() {
        phone = sharedPreferences.getString("phone", "");

        if (phone.isEmpty()) {
            Log.e("DEBUG_ROLE_SELECTION", "❌ Phone is NULL or empty! Cannot proceed.");
            Toast.makeText(this, "Error: Phone number missing!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d("DEBUG_ROLE_SELECTION", "✅ Phone retrieved: " + phone);
        }
    }

    private void updateRole(String role, Class<?> nextActivity) {
        if (phone == null || phone.isEmpty()) {
            Log.e("DEBUG_ROLE_SELECTION", "❌ Phone number missing!");
            Toast.makeText(this, "Phone number missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DEBUG_ROLE_SELECTION", "🔄 Updating role: " + role + " for phone: " + phone);

        String url = BASE_URL + "/select-role";

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("phone", phone);
            requestData.put("role", role);
        } catch (JSONException e) {
            Log.e("RoleSelection", "❌ JSON Error: " + e.getMessage());
            return;
        }

        Log.d("DEBUG_ROLE_SELECTION", "📤 JSON Request: " + requestData.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    Log.d("RoleSelection", "✅ Server Response: " + response.toString());

                    boolean success = response.optString("status", "").equals("success");
                    if (success) {
                        sharedPreferences.edit().putString("role", role).apply();
                        Log.d("DEBUG_ROLE_SELECTION", "✅ Role saved: " + role);

                        startActivity(new Intent(RoleSelection.this, nextActivity));
                        finish();
                    } else {
                        String message = response.optString("error", "Role update failed");
                        Log.e("RoleSelection", "❌ Error: " + message);
                        Toast.makeText(RoleSelection.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("RoleSelection", "❌ Volley Error: " + error.toString());
                    Toast.makeText(RoleSelection.this, "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }
}













