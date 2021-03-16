package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BluetoothConnectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        getSupportActionBar().hide();

        CardView bluetoothCard = (CardView) findViewById(R.id.bluetooth_connection_card);
        bluetoothCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mainPageIntent = new Intent(BluetoothConnectionActivity.this, MainActivity.class);
                startActivity(mainPageIntent);
            }
        });

    }
}
