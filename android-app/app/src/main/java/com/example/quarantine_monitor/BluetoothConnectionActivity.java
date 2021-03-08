package com.example.quarantine_monitor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothConnectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        getSupportActionBar().hide();
    }
}
