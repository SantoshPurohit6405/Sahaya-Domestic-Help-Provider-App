package com.example.sahayaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CProviderAdapter extends RecyclerView.Adapter<CProviderAdapter.CProviderViewHolder> {

    private final List<ProviderModel> providerList;
    private final Context context;

    public CProviderAdapter(List<ProviderModel> providerList, Context context) {
        this.providerList = providerList;
        this.context = context;
    }

    @Override
    public CProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.provider_item, parent, false);
        return new CProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CProviderViewHolder holder, int position) {
        ProviderModel provider = providerList.get(position);

        holder.nameTextView.setText("Name: " + provider.getName());
        holder.phoneTextView.setText("Phone: " + provider.getPhone());
        holder.serviceNameTextView.setText("Service: " + provider.getServiceName());
        holder.experienceTextView.setText("Experience: " + provider.getExperience());

        holder.cardView.setOnClickListener(v -> {
            try {
                // Fetch customer details from SharedPreferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                int customerId = prefs.getInt("user_id", -1);
                String customerName = prefs.getString("user_name", "N/A");
                String customerEmail = prefs.getString("user_email", "N/A");
                String customerPhone = prefs.getString("user_phone", "N/A");

                // Start BookingActivity with provider and customer data
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("provider_id", provider.getId());
                intent.putExtra("provider_name", provider.getName());
                intent.putExtra("provider_phone", provider.getPhone());
                intent.putExtra("provider_email", provider.getEmail()); // ensure this exists in ProviderModel
                intent.putExtra("service_name", provider.getServiceName());
                intent.putExtra("service_id", provider.getServiceId()); // make sure this is in your model
                intent.putExtra("experience", provider.getExperience());

                // Customer data
                intent.putExtra("customer_id", customerId);
                intent.putExtra("customer_name", customerName);
                intent.putExtra("customer_email", customerEmail);
                intent.putExtra("customer_phone", customerPhone);

                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class CProviderViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, phoneTextView, serviceNameTextView, experienceTextView;
        CardView cardView;

        public CProviderViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.providerName);
            phoneTextView = itemView.findViewById(R.id.providerPhone);
            serviceNameTextView = itemView.findViewById(R.id.providerService);
            experienceTextView = itemView.findViewById(R.id.providerExperience);
            cardView = itemView.findViewById(R.id.cardViewProvider); // your layout must include this
        }
    }
}







