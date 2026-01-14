package com.example.sahayaapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProviderListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CProviderAdapter adapter;
    List<ProviderModel> providerList = new ArrayList<>();
    String selectedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        recyclerView = findViewById(R.id.recyclerViewProviders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedService = getIntent().getStringExtra("service_name");

        Log.d("ProviderListActivity", "Selected service: " + selectedService);

        if (selectedService != null && !selectedService.isEmpty()) {
            fetchProviders(selectedService);
        } else {
            Log.e("ProviderListActivity", "Service name is null or empty");
            Toast.makeText(this, "Invalid service selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProviders(String serviceName) {
        String url = "http://192.168.0.103:3000/providers/by-service/" + serviceName;

        Log.d("ProviderListActivity", "Fetching providers from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API Response", response.toString());

                        if (response.length() == 0) {
                            Toast.makeText(this, "No providers found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // Check if all required fields exist before accessing
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            String phone = obj.getString("phone");
                            String service_name = obj.getString("service_name");
                            String experience = obj.getString("experience");

                            ProviderModel provider = new ProviderModel(id, name, phone, service_name, experience);
                            providerList.add(provider);
                        }

                        if (!providerList.isEmpty()) {
                            adapter = new CProviderAdapter(providerList, this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "No providers available for this service", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error loading providers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}









