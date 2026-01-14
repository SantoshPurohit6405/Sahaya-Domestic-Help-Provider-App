package com.example.sahayaapp;

public class Provider {
    private int id;
    private String providerName;
    private String phone;
    private String email;
    private String services;
    private String experience;
    private String createdAt;

    public Provider(int id, String providerName, String phone, String email, String services, String experience, String createdAt) {
        this.id = id;
        this.providerName = providerName;
        this.phone = phone;
        this.email = email;
        this.services = services;
        this.experience = experience;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getProviderName() { return providerName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getServices() { return services; }
    public String getExperience() { return experience; }
    public String getCreatedAt() { return createdAt; }
}



