package com.example.sahayaapp;

public class ProviderModel {

    private int id;
    private String name;
    private String phone;
    private String email;
    private String serviceName;
    private int serviceId;
    private String experience;

    // Constructor with required fields for Provider
    public ProviderModel(int id, String name, String phone, String serviceName, String experience) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.serviceName = serviceName;
        this.experience = experience;
    }

    // Optional: Constructor with all fields, including email and serviceId
    public ProviderModel(int id, String name, String phone, String email, String serviceName, int serviceId, String experience) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.experience = experience;
    }

    // Getters and setters for each field

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}







