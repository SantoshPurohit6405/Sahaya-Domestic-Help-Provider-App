package com.example.sahayaapp;

public class CompletedBooking {
    private int serviceId;
    private String serviceName;
    private String customerName;
    private String providerName;
    private String bookingDate;
    private String completedAt;

    public CompletedBooking(int serviceId, String serviceName, String customerName, String providerName, String bookingDate, String completedAt) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.customerName = customerName;
        this.providerName = providerName;
        this.bookingDate = bookingDate;
        this.completedAt = completedAt;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getCompletedAt() {
        return completedAt;
    }
}
