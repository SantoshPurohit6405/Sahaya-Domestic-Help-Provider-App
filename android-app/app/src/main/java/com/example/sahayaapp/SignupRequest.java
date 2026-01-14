package com.example.sahayaapp;

import com.google.gson.annotations.SerializedName;

public class SignupRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("password")
    private String password;

    @SerializedName("phone_number")
    private String phoneNumber;  // Renamed to follow Java naming conventions

    @SerializedName("role")
    private String role;

    // Constructor
    public SignupRequest(String name, String password, String phoneNumber, String role) {
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters for better data access
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }

    // Setters for flexibility
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRole(String role) {
        this.role = role;
    }
}


