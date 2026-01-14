package com.example.sahayaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    private List<Customer> customerList;

    public CustomerAdapter(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.id.setText("Id:"+customer.getId());
        holder.username.setText("Username: " + customer.getUsername());
        holder.phone.setText("Phone: " + customer.getPhoneNumber());
        holder.email.setText("Email: " + customer.getEmail());
        holder.createdAt.setText("Created At: " + customer.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id,username, phone, email, createdAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.customer_id);
            username = itemView.findViewById(R.id.customer_username);
            phone = itemView.findViewById(R.id.customer_phone);
            email = itemView.findViewById(R.id.customer_email);
            createdAt = itemView.findViewById(R.id.customer_created_at);
        }
    }
}










