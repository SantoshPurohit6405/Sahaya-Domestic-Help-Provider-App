package com.example.sahayaapp;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    // Correct endpoint format without extra '/'
    @Headers("Content-Type: application/json")  // Ensures proper JSON request format
    @POST("signup")  // Endpoint path should not start with '/' unless needed
    Call<SignupResponse> signup(@Body SignupRequest request);

    Call<LoginResponse> loginUser(LoginRequest loginRequest);
}


