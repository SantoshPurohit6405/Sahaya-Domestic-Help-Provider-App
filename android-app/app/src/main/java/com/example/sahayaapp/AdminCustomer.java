package com.example.sahayaapp;

import android.os.Bundle;
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

public class AdminCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private List<Customer> customerList;
    private static final String API_URL = "http://192.168.0.103:3000/customers"; // ✅ Replace with your API URL

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_customers); // ✅ Make sure this layout exists

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerList);
        recyclerView.setAdapter(customerAdapter);

        fetchCustomers();
    }

    private void fetchCustomers() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONArray customersArray = response.getJSONArray("customers");
                                customerList.clear(); // ✅ Clear old data before adding new ones

                                for (int i = 0; i < customersArray.length(); i++) {
                                    JSONObject customer = customersArray.getJSONObject(i);
                                    int id = customer.getInt("id");
                                    String username = customer.getString("username");
                                    String phone = customer.getString("phoneNumber");
                                    String email = customer.getString("email");
                                    String createdAt = customer.getString("created_at");

                                    customerList.add(new Customer(id, username, phone, email, createdAt));
                                }

                                customerAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "No customers found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to load data: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
















