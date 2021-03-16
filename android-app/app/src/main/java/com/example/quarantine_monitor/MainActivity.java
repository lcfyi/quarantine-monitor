package com.example.quarantine_monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        startService(new Intent(this, BackgroundLocationService.class));

        CardView facialVerificationCard = (CardView) findViewById(R.id.id_verification_card);
        facialVerificationCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent facialVerificationPageIntent = new Intent(MainActivity.this, FacialVerificationActivity.class);
                startActivity(facialVerificationPageIntent);
            }
        });

        CardView bluetoothConnectionCard = (CardView) findViewById(R.id.bluetooth_connection_card);
        bluetoothConnectionCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent bluetoothConnectPage = new Intent(MainActivity.this, BluetoothConnectionActivity.class);
                startActivity(bluetoothConnectPage);
            }
        });

        CardView profileCard = (CardView) findViewById(R.id.profile_card);
        profileCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent profilePage = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profilePage);
            }
        });

        CardView settingsCard = (CardView) findViewById(R.id.settings_card);
        settingsCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent settingsPage = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsPage);
            }
        });


    }
}