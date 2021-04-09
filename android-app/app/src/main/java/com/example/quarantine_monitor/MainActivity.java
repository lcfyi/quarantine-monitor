package com.example.quarantine_monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, BackgroundLocationService.class));

        updateHeader();

        CardView facialVerificationCard = (CardView) findViewById(R.id.id_verification_card);
        facialVerificationCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent facialVerificationPageIntent = new Intent(MainActivity.this, DetectorActivity.class);
                facialVerificationPageIntent.putExtra("SignUpWorkflow", "False");
                facialVerificationPageIntent.putExtra("TestWorkflow", "False");
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

    @Override
    public void onBackPressed(){
        updateHeader();
    }

    /*
     *  Creates the message for the header text view which displays the time period remaining
     */
    private void updateHeader() {
        TextView daysCounter = findViewById(R.id.daysLeft);
        String message;
        if (!UserInfoHelper.getAdmin()) {
            long curUt = System.currentTimeMillis();
            if (curUt < UserInfoHelper.getEndtime()) {
                // Convert the difference from milliseconds to days left
                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
                String greeting;

                if (timeOfDay >= 0 && timeOfDay < 12) {
                    greeting = "Good morning";
                } else if (timeOfDay >= 12 && timeOfDay < 16) {
                    greeting = "Good afternoon";
                } else {
                    greeting = "Good evening";
                }

                long millisRemaining = UserInfoHelper.getEndtime() - curUt;
                int hours   = (int) ((millisRemaining / (1000*60*60)) % 24);
                int days = (int) ((millisRemaining / (1000*60*60*24)) % 30);
                message = String.format(greeting + ", you have %02d days %d hours left in your quarantine.", days, hours);
            } else {
                message = "Congratulations! You have completed your quarantine period.";
            }

        } else {
            message = "ADMIN";
        }
        daysCounter.setText(message);
        //TODO: Link to plotting page here
    }
}