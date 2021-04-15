package com.example.quarantine_monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;

// This activity is responsible for handling bluetooth disconnection and wifi connection
public class BluetoothDisconnectionActivity extends AppCompatActivity {
    boolean errorFound = false;
    Button disconnectButton;
    Button mainMenuButton;
    Button wifiSetupButton;
    InputStream BTInputStream = null;
    OutputStream BTOutputStream = null;
    BluetoothAdapter BTAdapter = null;
    BluetoothSocket BTSocket = null;
    BluetoothDevice BTDevice = null;
    BluetoothManager BTManager = null;

    boolean signUpFlag = false;

    BluetoothThreadHelper BTThreadHelper = BluetoothThreadHelper.getInstance();
    BluetoothConnectionRFS rfsBTDevice = BluetoothConnectionRFS.getInstance();
    BluetoothConnection BTConnection = BluetoothConnection.getInstance();

    final String TAG = "tag";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("SignUpWorkflow").equals("True")) {
            signUpFlag = true;
        }
        setContentView(R.layout.activity_bt_connected);

        BTDevice = rfsBTDevice.getBluetoothDevice();
        BTSocket = rfsBTDevice.getBTSocket();

        disconnectButton = (Button) findViewById(R.id.BT_disconnect_button);
        disconnectButton.setOnClickListener(v -> BTdisconnect());

        mainMenuButton = (Button) findViewById(R.id.main_menu_button);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
            }
        });

        wifiSetupButton = (Button) findViewById(R.id.wifi_setup_button);
        wifiSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWifiSetupActivity();
            }
        });

    }

    // New Intent created to go back to the home/main page
    public void goToMainMenu() {
        Intent mainMenuActivityIntent = new Intent(BluetoothDisconnectionActivity.this, MainActivity.class);
        startActivity(mainMenuActivityIntent);
        finish();
    }

    // New Intent created to go to the wifi setup page
    public void goToWifiSetupActivity() {
        Intent wifiSetupActivityIntent = new Intent(BluetoothDisconnectionActivity.this, WifiAndBluetoothSetupActivity.class);
        startActivity(wifiSetupActivityIntent);
    }

    // Function call to properly destroy the bluetooth connection
    // It first destroys the bluetooth pinging thread
    // It then destroys the Input Stream, Output Stream, and BT Socket 
    // Last, if all is well, it moves to the next intent (either bluetooth menu or 
    // facial verification page - depends on the user's workflow)
    public void BTdisconnect() {
        BTThreadHelper.reset();
        BTThreadHelper.destroyThread();
        try {
            BTInputStream = BTSocket.getInputStream();
            BTOutputStream = BTSocket.getOutputStream();
        } catch (Exception e) {
            errorFound = true;
            Toast.makeText(getApplicationContext(), "Input and Output Streams not open", Toast.LENGTH_SHORT).show();
        }

        if(BTInputStream != null) {
            try {
                BTInputStream.close();
            } catch (Exception e) {
                errorFound = true;
                Toast.makeText(getApplicationContext(), "Input stream not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTInputStream = null;
        }

        if(BTOutputStream != null) {
            try {
                BTOutputStream.close();
            } catch (Exception e) {
                errorFound = true;
                Toast.makeText(getApplicationContext(), "Output stream not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTOutputStream = null;
        }

        if(BTSocket != null) {
            try {
                BTSocket.close();
            } catch (Exception e) {
                errorFound = true;
                Toast.makeText(getApplicationContext(), "Socket not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTSocket = null;
        }

        if(errorFound == false && BTSocket == null && BTInputStream == null && BTOutputStream == null) {
            BTConnection.disconnect();

            Intent bluetoothConnectionActivityIntent = new Intent(BluetoothDisconnectionActivity.this, BluetoothConnectionActivity.class);
            bluetoothConnectionActivityIntent.putExtra("SignUpWorkflow", "False");
            startActivity(bluetoothConnectionActivityIntent);
            finish();
        }
    }

    // Setting to prevent the user from going back to the previous menu during the signup process
    @Override
    public void onBackPressed(){
        // create a boolean to chec
        if(!signUpFlag){
            Intent homePageIntent = new Intent(BluetoothDisconnectionActivity.this, MainActivity.class);
            startActivity(homePageIntent);
        }
        else {
            // do nothing
        }
    }
}
