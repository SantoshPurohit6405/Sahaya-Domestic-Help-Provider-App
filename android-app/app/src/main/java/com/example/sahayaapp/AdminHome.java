package com.example.sahayaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminHome extends AppCompatActivity {

    CardView Customers,Providers,Admin,Services;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);


        // Initialize CardView
        Customers = findViewById(R.id.cardViewCustomers);
        Providers=findViewById(R.id.cardViewProviders);
        Admin=findViewById(R.id.cardViewAdmin);
        Services=findViewById(R.id.Services);


        // Set Click Listener
        Customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Customers Page
                Intent intent = new Intent(AdminHome.this, AdminCustomer.class);
                startActivity(intent);
            }
        });
        Providers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Provider's Page
                Intent intent = new Intent(AdminHome.this, AdminProviders.class);
                startActivity(intent);
            }
        });
        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Provider's Page
                Intent intent = new Intent(AdminHome.this, AdminPanel.class);
                startActivity(intent);
            }
        });

        Services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Services Page
                Intent intent = new Intent(AdminHome.this, CompletedBookingsActivity.class);
                startActivity(intent);
            }
        });

    }
}


