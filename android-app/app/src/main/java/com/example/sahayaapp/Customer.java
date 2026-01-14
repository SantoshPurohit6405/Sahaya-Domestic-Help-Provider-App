package com.example.sahayaapp;

import androidx.annotation.NonNull;

public class Customer {
    private int id;
    private String username;
    private String phoneNumber;
    private String email;
    private String createdAt;

    public Customer(int id, String username, String phoneNumber, String email, String createdAt) {
        this.id = id;
        this.username = (username != null) ? username : "Unknown";
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "N/A";
        this.email = (email != null) ? email : "N/A";
        this.createdAt = (createdAt != null) ? createdAt : "Unknown";
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getCreatedAt() { return createdAt; }
}








