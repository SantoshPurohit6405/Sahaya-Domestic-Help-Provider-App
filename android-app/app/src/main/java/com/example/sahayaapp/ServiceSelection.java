package com.example.sahayaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceSelection extends AppCompatActivity {

    private CheckBox cbDomesticHelp, cbPersonalDriver, cbHomeCook, cbElectrician, cbPlumber, cbWardPerson;
    private RadioGroup experienceGroup;
    private Button btnFinish;
    private SharedPreferences sharedPreferences;
    private String phone, username;
    private static final String BASE_URL = "http://192.168.0.103:3000"; // Replace with your API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serviceselection);

        initializeUI();
        retrieveUserDetails();

        btnFinish.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                handleFinishButton();
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeUI() {
        cbDomesticHelp = findViewById(R.id.cbDomesticHelp);
        cbPersonalDriver = findViewById(R.id.cbPersonalDriver);
        cbHomeCook = findViewById(R.id.cbHomeCook);
        cbElectrician = findViewById(R.id.cbElectrician);
        cbPlumber = findViewById(R.id.cbPlumber);
        cbWardPerson = findViewById(R.id.cbWardPerson);

        experienceGroup = findViewById(R.id.experienceGroup);
        btnFinish = findViewById(R.id.btnFinish);
    }

    private void retrieveUserDetails() {
        sharedPreferences = getSharedPreferences("SahayaPrefs", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        username = sharedPreferences.getString("username", "");

        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Error: Phone number missing!", Toast.LENGTH_LONG).show();
            Log.e("ServiceSelection", "Phone number is missing from SharedPreferences.");
            finish();
        } else {
            Log.d("ServiceSelection", "Phone retrieved: " + phone);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void handleFinishButton() {
        ArrayList<String> selectedServices = new ArrayList<>();

        if (cbDomesticHelp.isChecked()) selectedServices.add("Domestic Help");
        if (cbPersonalDriver.isChecked()) selectedServices.add("Personal Driver");
        if (cbHomeCook.isChecked()) selectedServices.add("Home Cook");
        if (cbElectrician.isChecked()) selectedServices.add("Electrician");
        if (cbPlumber.isChecked()) selectedServices.add("Plumber");
        if (cbWardPerson.isChecked()) selectedServices.add("Ward Person");

        if (selectedServices.isEmpty()) {
            Toast.makeText(this, "Please select at least one service.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedServices.size() > 2) {
            Toast.makeText(this, "You can select only up to 2 services.", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedExperienceId = experienceGroup.getCheckedRadioButtonId();
        if (selectedExperienceId == -1) {
            Toast.makeText(this, "Please select your experience level.", Toast.LENGTH_SHORT).show();
            return;
        }
        int experience = (selectedExperienceId == R.id.rbExperienced) ? 1 : 0;

        sendServiceSelectionRequest(selectedServices, experience);
    }

    private void sendServiceSelectionRequest(ArrayList<String> selectedServices, int experience) {
        String url = BASE_URL + "/select-services";

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("phone", phone);
            requestData.put("provider_name", username);
            requestData.put("services", new JSONArray(selectedServices));
            requestData.put("experience", experience);

            Log.d("ServiceSelection", "Sending Request: " + requestData.toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                    response -> {
                        Log.d("ServiceSelection", "Server Response: " + response.toString());
                        if (response.optString("status", "").equals("success")) {
                            Toast.makeText(ServiceSelection.this, "Services saved successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ServiceSelection.this, LoginActivity.class));
                            finish();
                        } else {
                            String message = response.optString("error", "Something went wrong");
                            Toast.makeText(ServiceSelection.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("ServiceSelection", "Volley Error: " + error.toString());
                        Toast.makeText(ServiceSelection.this, "Error saving services.", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(jsonRequest);

        } catch (JSONException e) {
            Log.e("ServiceSelection", "JSON Error: " + e.getMessage());
        }
    }
}








