package com.example.sahayaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class ProviderRequestAdapter extends RecyclerView.Adapter<ProviderRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<ProviderRequest> requestList;

    public ProviderRequestAdapter(Context context, List<ProviderRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, serviceName, serviceId, customerPhone, customerEmail;
        Button accept, reject;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerNameTextView);
            serviceName = itemView.findViewById(R.id.serviceNameTextView);
            serviceId = itemView.findViewById(R.id.serviceIdTextView);
            customerPhone = itemView.findViewById(R.id.phoneTextView);
            customerEmail = itemView.findViewById(R.id.emailTextView);
            accept = itemView.findViewById(R.id.acceptButton);
            reject = itemView.findViewById(R.id.rejectButton);
        }
    }

    @Override
    public ProviderRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.provider_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProviderRequestAdapter.ViewHolder holder, int position) {
        ProviderRequest request = requestList.get(position);

        holder.customerName.setText("Customer: " + request.getCustomerName());
        holder.serviceName.setText("Service: " + request.getServiceName());
        holder.serviceId.setText("Service ID: " + request.getServiceId());
        holder.customerPhone.setText("Phone: " + request.getCustomerPhone());
        holder.customerEmail.setText("Email: " + request.getCustomerEmail());

        // Accept button logic
        holder.accept.setOnClickListener(v -> {
            String bookingId = String.valueOf(request.getServiceId()); // Or use getBookingId() if available
            String url = "http://192.168.0.103:3000/accept-booking/" + bookingId;
            sendAcceptRequest(url, position, bookingId);
        });

        // Reject button logic
        holder.reject.setOnClickListener(v -> {
            String bookingId = String.valueOf(request.getServiceId());
            String url = "http://192.168.0.103:3000/reject-booking/" + bookingId;
            sendRejectRequest(url, position);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // Accept booking request
    private void sendAcceptRequest(String url, int position, String bookingId) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.PUT, url,
                response -> {
                    requestList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, requestList.size());

                    // Save service_id to SharedPreferences when accepted
                    SharedPreferences sharedPreferences = context.getSharedPreferences("SahayaPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("service_id", Integer.parseInt(bookingId));  // Save the service ID
                    editor.apply(); // Don't forget to apply the changes

                    // Debugging: Log to ensure that the service_id is saved
                    Log.d("ProviderRequestAdapter", "Service ID saved: " + bookingId);

                    Toast.makeText(context, "Service Accepted Successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to ProviderSuccess page
                    Intent intent = new Intent(context, ProviderSuccess.class);
                    intent.putExtra("bookingId", bookingId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Needed if starting from non-activity
                    context.startActivity(intent);
                },
                error -> {
                    Toast.makeText(context, "Error Accepting Service: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }


    // Reject booking request
    private void sendRejectRequest(String url, int position) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    requestList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, requestList.size());

                    Toast.makeText(context, "Service Rejected Successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(context, "Error Rejecting Service: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}









