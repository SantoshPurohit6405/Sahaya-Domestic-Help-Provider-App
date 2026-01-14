package com.example.sahayaapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.List;

public class AdminProviders extends AppCompatActivity {

    private RecyclerView recyclerView1;
    private ProviderAdapter providerAdapter;
    private List<Provider> providerList;
    private static final String API_URL = "http://192.168.0.108:3000/providers"; // ✅ Make sure this is correct

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_providers); // ✅ Ensure this layout exists

        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        providerList = new ArrayList<>();
        providerAdapter = new ProviderAdapter(providerList);
        recyclerView1.setAdapter(providerAdapter);

        fetchProviders();
    }

    private void fetchProviders() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // ✅ Extract providers array
                            JSONArray providersArray = response.getJSONArray("providers");
                            providerList.clear();

                            for (int i = 0; i < providersArray.length(); i++) {
                                JSONObject provider = providersArray.getJSONObject(i);

                                int providerId = provider.getInt("provider_id");
                                String providerName = provider.getString("provider_name");
                                String experience = provider.getString("experience");
                                String createdAt = provider.getString("created_at");
                                String services = provider.optString("services", "N/A");
                                String phone = provider.optString("phone", "N/A");
                                String email = provider.optString("email", "N/A");

                                providerList.add(new Provider(providerId, providerName, phone, email, services, experience, createdAt));
                            }

                            providerAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e("JSONError", "Parsing error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error: " + error.toString());
                        Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}





