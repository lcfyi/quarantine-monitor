package com.example.quarantine_monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, BackgroundLocationService.class));

        CardView facialVerificationCard = (CardView) findViewById(R.id.id_verification_card);
        facialVerificationCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent facialVerificationPageIntent = new Intent(MainActivity.this, DetectorActivity.class);
                startActivity(facialVerificationPageIntent);
            }
        });

        CardView bluetoothConnectionCard = (CardView) findViewById(R.id.bluetooth_connection_card);
        bluetoothConnectionCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent bluetoothConnectPage = new Intent(MainActivity.this, BluetoothConnectionActivity.class);
                bluetoothConnectPage.putExtra("SignUpWorkflow", "False");
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