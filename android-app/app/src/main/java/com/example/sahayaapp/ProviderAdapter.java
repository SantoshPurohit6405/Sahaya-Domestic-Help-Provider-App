package com.example.sahayaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {
    private List<Provider> providerList;

    public ProviderAdapter(List<Provider> providerList) {
        this.providerList = providerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_providers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Provider provider = providerList.get(position);
        holder.providerName.setText("Provider: " + provider.getProviderName());
        holder.phone.setText("Phone: " + provider.getPhone());
        holder.email.setText("Email: " + provider.getEmail());
        holder.services.setText("Services: " + provider.getServices());
        holder.experience.setText("Experience: " + provider.getExperience());
        holder.createdAt.setText("Created At: " + provider.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView providerName, phone, email, services, experience, createdAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            providerName = itemView.findViewById(R.id.provider_name);
            phone = itemView.findViewById(R.id.provider_phone);
            email = itemView.findViewById(R.id.provider_email);
            services = itemView.findViewById(R.id.provider_services);
            experience = itemView.findViewById(R.id.provider_experience);
            createdAt = itemView.findViewById(R.id.provider_created_at);
        }
    }
}


