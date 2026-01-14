package com.example.sahayaapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Driver extends AppCompatActivity {

    RecyclerView recyclerView;
    CProviderAdapter adapter;
    List<ProviderModel> providerList = new ArrayList<>();
    String selectedService = "Personal Driver"; // The service to filter providers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver);

        selectedService = getIntent().getStringExtra("service_name");

        if (selectedService == null || selectedService.isEmpty()) {
            selectedService = "Personal Driver"; // Fallback
        }

        recyclerView = findViewById(R.id.recyclerViewProviders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchProviders(selectedService); // Fetch provider data for the selected service
    }

    private void fetchProviders(String serviceName) {
        // Use your local IP address or server IP as needed
        String url = "http://192.168.0.103:3000/providers/by-service/" + serviceName;


        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            ProviderModel provider = new ProviderModel(
                                    obj.getInt("id"),
                                    obj.getString("provider_name"),
                                    obj.getString("phone"),
                                    obj.getString("service_name"),
                                    obj.getString("experience") // Include experience field
                            );
                            providerList.add(provider);
                        }

                        adapter = new CProviderAdapter(providerList, this);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing provider data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load providers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}
