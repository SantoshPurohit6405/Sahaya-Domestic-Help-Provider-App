package com.example.sahayaapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity {

    private TextView noRequestsTextView;
    private RecyclerView recyclerView;
    private ProviderRequestAdapter adapter;
    private List<ProviderRequest> requestList;
    private int providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requests);

        // Initialize views
        noRequestsTextView = findViewById(R.id.noRequestsTextView);
        noRequestsTextView.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.requestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        requestList = new ArrayList<>();
        adapter = new ProviderRequestAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        // Fetch user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            fetchProviderIdFromUserId(userId);
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            Log.e("RequestsActivity", "Invalid User ID: Not found in SharedPreferences.");
        }
    }

    private void fetchProviderIdFromUserId(int userId) {
        String url = "http://192.168.0.103:3000/api/service-providers/by-user/" + userId;
        Log.d("RequestsActivity", "Fetching Provider ID from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("id")) {
                            providerId = response.getInt("id");
                            Log.d("RequestsActivity", "Fetched Provider ID: " + providerId);
                            fetchPendingRequests(providerId);
                        } else {
                            Log.e("RequestsActivity", "Provider ID not found in response.");
                            Toast.makeText(this, "Provider not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("RequestsActivity", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing provider data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("RequestsActivity", "Volley error: " + error.toString());
                    Toast.makeText(this, "Failed to get provider ID", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void fetchPendingRequests(int providerId) {
        String url = "http://192.168.0.108:3000/api/bookings/pending/" + providerId;
        Log.d("RequestsActivity", "Fetching pending requests from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("data")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            Log.d("RequestsActivity", "Received pending request data: " + dataArray.length() + " items");
                            parsePendingRequests(dataArray);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("RequestsActivity", "No data found in response.");
                            showNoRequestsMessage();
                        }
                    } catch (JSONException e) {
                        Log.e("RequestsActivity", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(this, "Failed to parse request data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMsg = "Error fetching pending requests";
                    if (error.networkResponse != null) {
                        errorMsg += " | Code: " + error.networkResponse.statusCode;
                    }
                    Log.e("VolleyError", errorMsg);
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonObjectRequest);
    }

    private void parsePendingRequests(JSONArray dataArray) {
        requestList.clear();

        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);

                int serviceId = obj.optInt("service_id", -1);
                String serviceName = obj.optString("service_name", "N/A");
                String status = obj.optString("status", "N/A");
                String providerName = obj.optString("provider_name", "N/A");
                String customerName = obj.optString("customer_name", "N/A");
                String customerEmail = obj.optString("customer_email", "N/A");
                String customerPhone = obj.optString("customer_phone", "N/A");
                String providerEmail = obj.optString("provider_email", "N/A");
                String providerPhone = obj.optString("provider_phone", "N/A");
                String bookingDate = obj.optString("booking_date", "N/A");
                String bookingTime = obj.optString("booking_time", "N/A");

                // Save the service ID in SharedPreferences when a request is selected
                SharedPreferences sharedPreferences = getSharedPreferences("SahayaPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("service_id", serviceId); // Save the selected service ID
                editor.apply();

                ProviderRequest request = new ProviderRequest(serviceId, providerName, serviceName, status);
                request.setCustomerName(customerName);
                request.setCustomerEmail(customerEmail);
                request.setCustomerPhone(customerPhone);
                request.setProviderEmail(providerEmail);
                request.setProviderPhone(providerPhone);
                request.setBookingDate(bookingDate);
                request.setBookingTime(bookingTime);

                requestList.add(request);
            }

            if (requestList.isEmpty()) {
                showNoRequestsMessage();
            } else {
                showRequestsList();
            }

        } catch (JSONException e) {
            Log.e("RequestsActivity", "Parsing error: " + e.getMessage());
            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoRequestsMessage() {
        noRequestsTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showRequestsList() {
        noRequestsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}









