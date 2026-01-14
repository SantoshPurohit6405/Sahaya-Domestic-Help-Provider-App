package com.example.sahayaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CompletedbookingAdapter extends RecyclerView.Adapter<CompletedbookingAdapter.ViewHolder> {

    private List<CompletedBooking> bookingList;

    public CompletedbookingAdapter(List<CompletedBooking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public CompletedbookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_services, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedbookingAdapter.ViewHolder holder, int position) {
        CompletedBooking booking = bookingList.get(position);

        holder.serviceId.setText("Service ID: " + booking.getServiceId());
        holder.serviceName.setText("Service: " + booking.getServiceName());
        holder.customerName.setText("Customer: " + booking.getCustomerName());
        holder.providerName.setText("Provider: " + booking.getProviderName());
        holder.bookingDate.setText("Booked on: " + booking.getBookingDate());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceId, serviceName, customerName, providerName, bookingDate, completedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceId = itemView.findViewById(R.id.service_id);
            serviceName = itemView.findViewById(R.id.service_name);
            customerName = itemView.findViewById(R.id.customer_name);
            providerName = itemView.findViewById(R.id.provider_name);
            bookingDate = itemView.findViewById(R.id.booking_date);
        }
    }
}

