package com.example.sahayaapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CustomerSearch extends AppCompatActivity {

    CardView HouseHelp, HomeCook, Driver, Plumber, Electrician, Ward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customersearch); // Make sure this layout has correct CardView IDs

        // Initialize card views
        HouseHelp = findViewById(R.id.HouseHelp);
        HomeCook = findViewById(R.id.HomeCook);
        Driver = findViewById(R.id.HomeDriver);
        Plumber = findViewById(R.id.Plumber);
        Electrician = findViewById(R.id.Electrician);
        Ward = findViewById(R.id.Ward);

        // Set click listeners to launch respective Activity
        HouseHelp.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerSearch.this, HouseHelp.class); // You must create HouseHelp.java
            startActivity(intent);
        });

        HomeCook.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerSearch.this, Cook.class); // You must create Cook.java
            startActivity(intent);
        });

        Driver.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerSearch.this, Driver.class); // You must create Driver.java
            startActivity(intent);
        });

        Plumber.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerSearch.this, Plumber.class); // You must create Plumber.java
            startActivity(intent);
        });

        Electrician.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerSearch.this, Electrician.class); // You must create Electrician.java
            startActivity(intent);
        });

        Ward.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerSearch.this, Ward.class); // Already created
            startActivity(intent);
        });
    }
}


