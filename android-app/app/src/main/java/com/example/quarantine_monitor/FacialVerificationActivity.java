package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FacialVerificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_verification);
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed(){
        Intent homePageIntent = new Intent(FacialVerificationActivity.this, MainActivity.class);
        startActivity(homePageIntent);
    }
}
