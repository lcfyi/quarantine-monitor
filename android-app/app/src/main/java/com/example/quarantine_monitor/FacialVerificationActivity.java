package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FacialVerificationActivity extends AppCompatActivity {
    private boolean fromSignupProcess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_verification);
        getSupportActionBar().hide();
    }

    // todo - @naomi, make some boolean check to see if the facial verification came from signup process, if yes, make fromSignupProcess = true
    @Override
    public void onBackPressed(){
        // create a boolean to chec
        if(!fromSignupProcess){
            Intent homePageIntent = new Intent(FacialVerificationActivity.this, MainActivity.class);
            startActivity(homePageIntent);
        }
        else {
            // do nothing
        }
    }
}
