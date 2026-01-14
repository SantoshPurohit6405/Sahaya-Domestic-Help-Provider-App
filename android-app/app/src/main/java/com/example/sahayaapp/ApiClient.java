package com.example.sahayaapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Base URL for your API (ensure it's correctly formatted)
    private static final String BASE_URL = "http://192.168.0.108:3000"; // Ensure the URL ends with '/'

    // Retrofit instance (Singleton pattern)
    private static Retrofit retrofit = null;

    // Method to get the Retrofit client
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Base URL must end with '/'
                    .addConverterFactory(GsonConverterFactory.create()) // For JSON data handling
                    .build();
        }
        return retrofit;
    }
}


