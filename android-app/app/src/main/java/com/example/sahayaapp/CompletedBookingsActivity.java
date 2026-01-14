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

public class CompletedBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CompletedbookingAdapter adapter;
    private List<CompletedBooking> completedBookingList;
    private static final String PROVIDER_NAME = "Rakesh"; // ✅ Change to dynamic if needed
    private static final String API_URL = "http://192.168.0.108:3000/api/completed-bookings/" + PROVIDER_NAME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_services); // ✅ Make sure this layout exists

        recyclerView = findViewById(R.id.recyclerView11);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        completedBookingList = new ArrayList<>();
        adapter = new CompletedbookingAdapter(completedBookingList);
        recyclerView.setAdapter(adapter);

        fetchCompletedBookings();
    }

    private void fetchCompletedBookings() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray array = response.getJSONArray("completedBookings");
                                completedBookingList.clear();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject booking = array.getJSONObject(i);
                                    int serviceId = booking.getInt("service_id");
                                    String serviceName = booking.getString("service_name");
                                    String customerName = booking.getString("customer_name");
                                    String providerName = booking.getString("provider_name");
                                    String bookingDate = booking.getString("booking_date");
                                    String completedAt = booking.getString("completed_at");

                                    completedBookingList.add(new CompletedBooking(serviceId, serviceName, customerName, providerName, bookingDate, completedAt));
                                }

                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "No completed bookings found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
}

