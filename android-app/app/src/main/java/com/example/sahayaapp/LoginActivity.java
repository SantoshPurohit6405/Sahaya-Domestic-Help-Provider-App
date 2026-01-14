package com.example.sahayaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.0.103:3000"; // Change to correct backend IP
    private EditText etIdentifier, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUI();
        setListeners();
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
    }

    private void initializeUI() {
        etIdentifier = findViewById(R.id.phoneNumber);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        tvSignUp = findViewById(R.id.tvSignUp);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }

    private void loginUser() {
        String identifier = etIdentifier.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(identifier, password)) return;

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "/login",
                this::handleResponse,
                this::handleError) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("identifier", identifier);
                params.put("password", password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean validateInputs(String identifier, String password) {
        if (identifier.isEmpty()) {
            etIdentifier.setError("Phone or Username is required");
            etIdentifier.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void handleResponse(String response) {
        progressDialog.dismiss();
        Log.d("LoginActivity", "Raw Response from Server: " + response); // Log full response

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.optString("status", "error");

            if ("success".equalsIgnoreCase(status)) {
                String userRole = jsonResponse.optString("role", "").trim();
                String token = jsonResponse.optString("token", "").trim();
                String userId = jsonResponse.optString("user_id", "").trim();
                String userName = jsonResponse.optString("username", "").trim(); // Get username from response

                Log.d("LoginActivity", "User Role: " + userRole);
                Log.d("LoginActivity", "Token: " + token);
                Log.d("LoginActivity", "User ID: " + userId);
                Log.d("LoginActivity", "Username: " + userName);
                Log.d("LoginActivity", "📢 Server Response: " + response);

                saveUserSession(token, userRole, userId, userName); // Pass username to session
                Toast.makeText(this, "Welcome to Sahaya!", Toast.LENGTH_SHORT).show();
                redirectToHome(userRole);
            } else {
                String errorMsg = jsonResponse.optString("error", "Invalid login credentials");
                Log.e("LoginActivity", "Login Failed: " + errorMsg);
                showError(errorMsg);
            }
        } catch (JSONException e) {
            Log.e("LoginActivity", "JSON Parsing Error: " + e.getMessage());
            showError("Unexpected server response.");
        }
    }

    private void handleError(VolleyError error) {
        progressDialog.dismiss();
        Log.e("LoginActivity", "Volley Error: " + error.toString());
        showError("Network error. Please try again.");
    }

    private void saveUserSession(String token, String role, String userId, String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwt_token", token);
        editor.putString("user_role", role);
        editor.putString("username", userName); // Save username in SharedPreferences
        editor.putInt("user_id", Integer.parseInt(userId)); // Ensure integer is stored correctly
        editor.apply();
        Log.d("DEBUG", "User ID and Username saved: " + userId + ", " + userName);

        // Debugging logs
        Log.d("LoginActivity", "✅ Stored User ID: " + userId);
        Log.d("LoginActivity", "✅ Stored Role: " + role);
        Log.d("LoginActivity", "✅ Stored Username: " + userName);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void redirectToHome(String role) {
        Intent intent;

        // Debugging logs to verify role
        Log.d("LoginActivity", "🔄 Redirecting User Role: [" + role + "]");

        if (role == null || role.isEmpty()) {
            Log.e("LoginActivity", "❌ Role is NULL or EMPTY, redirecting to CustomerHome by default.");
            intent = new Intent(this, CustomerHome.class);
        } else {
            switch (role.trim().toLowerCase()) { // Trim extra spaces and convert to lowercase
                case "admin":
                    intent = new Intent(this, AdminHome.class);
                    break;
                case "service provider":  // Match the exact API response
                case "service_provider": // Check both possible role names
                case "provider":
                    intent = new Intent(this, ProviderHome.class);
                    break;
                case "customer":
                default:
                    intent = new Intent(this, CustomerHome.class);
                    break;
            }
        }

        startActivity(intent);
        finish();
    }
}

























