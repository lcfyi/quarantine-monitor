package com.example.quarantine_monitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WifiAndBluetoothSetupActivity extends AppCompatActivity {
    public static final int BUFFER_SIZE = 1;
    public static final int FLUSH_BUFFER_SIZE = 300;

    boolean signUpFlag = false;
    private EditText wifiName = null;
    private EditText wifiPW = null;
    private Button submitButton = null;
    private BluetoothSocket BTSocket = null;
    private BluetoothDevice BTDevice = null;
    private BluetoothThreadHelper BTThreadHelper = BluetoothThreadHelper.getInstance();
    private BluetoothConnectionRFS rfsBTDevice = BluetoothConnectionRFS.getInstance();
    private BluetoothConnection BTConnection = BluetoothConnection.getInstance();

    private final String serverAddr = "http://qmonitor-306302.wl.r.appspot.com/stations";
    private final String ping = "ping";
    private final String setWifiNameCommand = "set-ssid";
    private final String setWifiPWCommand = "set-pass";
    private final String setServerAddrCommand = "set-addr";
    private final String setFlagForFacialVerification = "set-face";
    private final String setWifi = "ini-rset";
    private final String getWifiStatus = "get-stat";
    private final String setBaseStationId = "set-base";
    private final String setWifiInterval = "set-inter";
    private final String setAccelSensitivity = "set-accel";
    private final String accelSensitivity = "70";
    private final String wifiInterval = "5";
    private final int shortDelay = 250;
    private final int longDelay = 15000;
    private final String newline = "\n";

    private BufferedReader in = null;

    byte[] buffer = new byte[2];
    int len = -1;

    String TAG = "wifi & bluetooth setup";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_and_bluetooth_setup);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("SignUpWorkflow").equals("True")) {
            signUpFlag = true;
        }

        wifiName = (EditText) findViewById(R.id.input_wifiName);
        wifiPW = (EditText) findViewById(R.id.input_wifiPassword);
        submitButton = (Button) findViewById(R.id.buttonWifiInfoSubmit);

        BTDevice = rfsBTDevice.getBluetoothDevice();
        BTSocket = rfsBTDevice.getBTSocket();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new submitWifiInfo().execute();
            }
        });
    }

    private class submitWifiInfo extends AsyncTask<Void, Void, Void> {
        String wifi, pw;

        @Override
        protected Void doInBackground(Void... voids) {
                    wifi = wifiName.getText().toString();
                    pw = wifiPW.getText().toString();

//            wifi = "TELUS2742";
//            pw = "3pxdm9h5dd";

            runOnUiThread(new Runnable() {
                public void run() {
                    if(wifi.matches("") || pw.matches("")) {
                        Toast.makeText(getApplicationContext(), "Please enter your wifi name and password", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getApplicationContext(), "Connecting to your Wifi.\nPlease Wait...", Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                    wifi = wifiName.getText().toString();
                    pw = wifiPW.getText().toString();

//            wifi = "TELUS2742";
//            pw = "3pxdm9h5dd";

            if(BTSocket != null) {
                try {
                    connectWithServer();
                    BTSocket.getOutputStream().write((setWifiNameCommand + " " + wifi + newline).getBytes());
                    SystemClock.sleep(shortDelay);
                    flush();
                    BTSocket.getOutputStream().write((setWifiPWCommand + " " + pw + newline).getBytes());
                    SystemClock.sleep(shortDelay);
                    flush();
                    BTSocket.getOutputStream().write((setServerAddrCommand + " " + serverAddr + newline).getBytes());
                    SystemClock.sleep(shortDelay);
                    flush();
                    BTSocket.getOutputStream().write((setBaseStationId + " " + UserInfoHelper.getBaseStationId().toString() + newline).getBytes());
                    SystemClock.sleep(shortDelay);
                    flush();
                    BTSocket.getOutputStream().write((setWifiInterval + " " + wifiInterval + newline).getBytes());
                    SystemClock.sleep(shortDelay);
                    flush();
                    BTSocket.getOutputStream().write((setAccelSensitivity + " " + accelSensitivity + newline).getBytes());
                    SystemClock.sleep(shortDelay);
                    flush();
                    BTSocket.getOutputStream().write((setWifi + newline).getBytes());
                    Toast.makeText(getApplicationContext(), "Configuration sent, waiting for connection...", Toast.LENGTH_LONG).show();

                    SystemClock.sleep(longDelay / 5);
                    flush();

                    int MAX_TRIES = 70;
                    int tries = 0;
                    String response;

                    do {
                        BTSocket.getOutputStream().write((getWifiStatus + newline).getBytes());
                        SystemClock.sleep(shortDelay * 2);
                        response = readFromInputPort();
                        Log.i(TAG, "response is: " + response);
                        tries++;
                    } while (!response.equals("1") && tries < MAX_TRIES);

                    if(response.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
                        if(signUpFlag){
                            Intent facialVerificationActivityIntent = new Intent(WifiAndBluetoothSetupActivity.this, DetectorActivity.class);
                            facialVerificationActivityIntent.putExtra("SignUpWorkflow", "True");
                            facialVerificationActivityIntent.putExtra("TestWorkflow", "False");
                            startActivity(facialVerificationActivityIntent);
                        }
                        else {
                            Intent mainActivityIntent = new Intent(WifiAndBluetoothSetupActivity.this, MainActivity.class);
                            mainActivityIntent.putExtra("SignUpWorkflow", "False");
                            startActivity(mainActivityIntent);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Connection Did not happen.\nPlease make sure you entered the wifi credentials correctly", Toast.LENGTH_LONG).show();
//                        Intent mainActivityIntent = new Intent(WifiAndBluetoothSetupActivity.this, WifiAndBluetoothSetupActivity.class);
//                        if(signUpFlag){
//                            mainActivityIntent.putExtra("SignUpWorkflow", "True");
//                        }
//                        else {
//                            mainActivityIntent.putExtra("SignUpWorkflow", "False");
//                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG WHILE SETTING WIFI", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void flush() {
        try {
            String message = "";
            int charsRead = 0;
            char[] buffer = new char[FLUSH_BUFFER_SIZE];

            in.read(buffer);

        } catch (IOException e) {
            Log.i(TAG, "flush: DID NOT WORK");
        }
    }

    private String readFromInputPort() {
        try {
            String message = "";
            int charsRead = 0;
            char[] buffer = new char[BUFFER_SIZE];

            in.read(buffer);
            message += new String(buffer);

            Log.i(TAG, "readFromInputPort: mesage is "+message);
            return message;
        } catch (IOException e) {
            return "Error receiving response:  " + e.getMessage();
        }
    }

    private void connectWithServer() {
        try {
            if (BTSocket != null) {
                in = new BufferedReader(new InputStreamReader(BTSocket.getInputStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}