package com.example.sahayaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class SignUpActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.0.103:3000";
    private EditText username, phoneNumber, email, password, confirmPassword;
    private Button signUpButton;
    private TextView loginRedirect;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeUI();
        sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        setClickListeners();
    }

    private void initializeUI() {
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUpButton = findViewById(R.id.signUpButton);
        loginRedirect = findViewById(R.id.tvLogin);
    }

    private void setClickListeners() {
        signUpButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                registerUser();
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String user = username.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String tempEmail = email.getText().toString().trim();

        if (tempEmail.isEmpty()) {
            tempEmail = phone + "@sahayaapp.com";
        }

        final String emailInput = tempEmail;
        String pass = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (!validateInputs(user, phone, pass, confirmPass)) return;

        sendSignupRequest(user, phone, emailInput, pass);
    }

    private boolean validateInputs(String user, String phone, String pass, String confirmPass) {
        if (user.isEmpty()) {
            username.setError("Username is required");
            username.requestFocus();
            return false;
        }

        if (!phone.matches("^[6-9][0-9]{9}$")) {
            phoneNumber.setError("Enter a valid Indian phone number");
            phoneNumber.requestFocus();
            return false;
        }

        if (pass.isEmpty() || pass.length() < 6 || !pass.matches(".*[A-Z].*")) {
            password.setError("Password must be at least 6 characters with 1 uppercase letter");
            password.requestFocus();
            return false;
        }

        if (!pass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match");
            confirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void sendSignupRequest(String user, String phone, String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "/signup",
                response -> handleSignupResponse(response, phone, user),
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("SIGNUP_ERROR", "Error Data: " + errorData);
                        Toast.makeText(SignUpActivity.this, "Signup Failed: " + errorData, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("phone", phone);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void handleSignupResponse(String response, String phone, String username) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.optString("status", "error");
            String message = jsonResponse.optString("message", "Unknown error");

            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

            if (status.equalsIgnoreCase("success")) {
                // ✅ Save phone number and username in SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("SahayaPrefs", Context.MODE_PRIVATE).edit();
                editor.putString("phone", phone);
                editor.putString("username", username);
                editor.apply();

                Log.d("SIGNUP_DEBUG", "✅ Phone stored in SharedPreferences: " + phone);
                Log.d("SIGNUP_DEBUG", "✅ Username stored in SharedPreferences: " + username);

                // ✅ Pass phone & username to RoleSelection Activity
                Intent intent = new Intent(SignUpActivity.this, RoleSelection.class);
                intent.putExtra("phone", phone);
                intent.putExtra("username", username);
                intent.putExtra("role", jsonResponse.optString("role", ""));
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", "❌ Error parsing response: " + e.getMessage());
            Toast.makeText(SignUpActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}













