package com.example.quarantine_monitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.os.SystemClock;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Arrays;

public class WifiAndBluetoothSetupActivity extends AppCompatActivity {
    boolean signUpFlag = false;
    private EditText wifiName = null;
    private EditText wifiPW = null;
    private Button submitButton = null;
    private Button wifiNameSubmitButton = null;
    private Button wifiPWSubmitButton = null;
    private Button serverButton = null;
    private BluetoothSocket BTSocket = null;
    private BluetoothDevice BTDevice = null;
    private BluetoothConnectionRFS rfsBTDevice = BluetoothConnectionRFS.getInstance();
    private BluetoothConnection BTConnection = BluetoothConnection.getInstance();

    private final String serverAddr = "https://d.lc.fyi";
    private final String ping = "ping";
    private final String setWifiNameCommand = "set-ssid";
    private final String setWifiPWCommand = "set-pass";
    private final String setServerAddrCommand = "set-addr";
    private final String setFlagForFacialVerification = "set-face";
    private final String setWifi = "ini-rset";
    private final String getWifiStatus = "get-stat";
    private final String newline = "\n";

    byte[] buffer = new byte[1024];
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
//        wifiNameSubmitButton = (Button) findViewById(R.id.buttonWifiNameSubmit);
//        wifiPWSubmitButton = (Button) findViewById(R.id.buttonWifiPWSubmit);
//        serverButton = (Button) findViewById(R.id.buttonServerSubmit);

        BTDevice = rfsBTDevice.getBluetoothDevice();
        BTSocket = rfsBTDevice.getBTSocket();

//        wifiNameSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitWifiInfo(1);
//            }
//        });
//
//        wifiPWSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitWifiInfo(2);
//            }
//        });
//
//        serverButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitWifiInfo(4);
//            }
//        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWifiInfo();
            }
        });
    }

    private void submitWifiInfo() {
//        switch (key) {
//            case 1 :
//                String wifi = wifiName.getText().toString();
//                if(wifi.matches("")) {
//                    Toast.makeText(this, "Please enter the wifi name", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else {
//                    if(BTSocket != null) {
//                        try {
//                            BTSocket.getOutputStream().write((setWifiNameCommand + " " + wifi + newline).getBytes());
//                        } catch (IOException e) {
//                            Toast.makeText(this, "SOMETHING WENT WRONG WHILE SETTING WIFI", Toast.LENGTH_SHORT).show();
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                break;
//            case 2 :
//                String pw = wifiPW.getText().toString();
//                if(pw.matches("")) {
//                    Toast.makeText(this, "Please enter the wifi password", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else {
//                    if(BTSocket != null) {
//                        try {
//                            BTSocket.getOutputStream().write((setWifiPWCommand + " " + pw + newline).getBytes());
//                        } catch (IOException e) {
//                            Toast.makeText(this, "SOMETHING WENT WRONG WHILE SETTING WIFI", Toast.LENGTH_SHORT).show();
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                break;
//            case 3:
//                try {
//                    BTSocket.getOutputStream().write((setWifi + newline).getBytes());
//                } catch (IOException e) {
//                    Toast.makeText(this, "SOMETHING WENT WRONG WHILE SETTING WIFI", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//                break;
//            case 4:
//                try {
//                    BTSocket.getOutputStream().write((setServerAddrCommand + " " + serverAddr + newline).getBytes());
//                } catch (IOException e) {
//                    Toast.makeText(this, "SOMETHING WENT WRONG WHILE SETTING WIFI", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//                break;
//            default:
//                break;
//        }

//        String wifi = wifiName.getText().toString();
//        String pw = wifiPW.getText().toString();
        Toast.makeText(this, "Connecting to your Wifi.\nPlease Wait...", Toast.LENGTH_LONG).show();

        submitButton.setEnabled(false);

        String wifi = "TELUS2742";
        String pw = "3pxdm9h5dd";

        if(wifi.matches("") || pw.matches("")) {
            Toast.makeText(this, "Please enter your wifi name and password", Toast.LENGTH_LONG).show();
            return;
        }
        if(BTSocket != null) {
            try {
                BTSocket.getOutputStream().write((setWifiNameCommand + " " + wifi + newline).getBytes());
                SystemClock.sleep(1500);
                BTSocket.getOutputStream().write((setWifiPWCommand + " " + pw + newline).getBytes());
                SystemClock.sleep(1500);
                BTSocket.getOutputStream().write((setServerAddrCommand + " " + serverAddr + newline).getBytes());
                SystemClock.sleep(1500);
                BTSocket.getOutputStream().write((setWifi + newline).getBytes());
                SystemClock.sleep(10000);

                long startTime = System.currentTimeMillis();
                BTSocket.getOutputStream().write((getWifiStatus + newline).getBytes());
                //noinspection InfiniteLoopStatement
                while (len == -1 && (System.currentTimeMillis()-startTime)<10000) {
                    len = BTSocket.getInputStream().read(buffer);
                    byte[] data = Arrays.copyOf(buffer, len);
                    Log.i(TAG, "len is " + len);
                    Log.i(TAG, "data is" + data);
                }
                if(len > 1) {
                    Toast.makeText(this, "Connection Successful", Toast.LENGTH_LONG).show();
                    submitButton.setEnabled(true);
                    setupPinging();
                    if(signUpFlag){
                        Intent facialVerificationActivityIntent = new Intent(WifiAndBluetoothSetupActivity.this, DetectorActivity.class);
                        facialVerificationActivityIntent.putExtra("SignUpWorkflow", "True");;
                        startActivity(facialVerificationActivityIntent);
                    }
                    else {
                        Intent mainActivityIntent = new Intent(WifiAndBluetoothSetupActivity.this, MainActivity.class);
                        mainActivityIntent.putExtra("SignUpWorkflow", "False");;
                        startActivity(mainActivityIntent);
                    }
                }
                else {
                    Toast.makeText(this, "Connection Did not happen.\nPlease make sure you entered the wifi credentials correctly", Toast.LENGTH_LONG).show();
                    submitButton.setEnabled(true);
                }
            } catch (IOException e) {
                Toast.makeText(this, "SOMETHING WENT WRONG WHILE SETTING WIFI", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void setupPinging() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(UserInfoHelper.getFvResult()) {
                        UserInfoHelper.setFvResult(false);
                        try{
                            BTSocket.getOutputStream().write((setFlagForFacialVerification + newline).getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            BTSocket.getOutputStream().write((ping + newline).getBytes());
                            Log.i(TAG, "setupPinging: "+ ping);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    SystemClock.sleep(2500);
                }
            }
        }).start();
    }
}
