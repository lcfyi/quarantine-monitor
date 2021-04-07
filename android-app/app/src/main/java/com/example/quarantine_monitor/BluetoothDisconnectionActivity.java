package com.example.quarantine_monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static android.bluetooth.BluetoothProfile.GATT;

public class BluetoothDisconnectionActivity extends AppCompatActivity {
    Button disconnectButton;
    InputStream BTInputStream = null;
    OutputStream BTOutputStream = null;
    BluetoothAdapter BTAdapter = null;
    BluetoothSocket BTSocket = null;
    BluetoothDevice BTDevice = null;
    BluetoothManager BTManager = null;

    BluetoothConnectionRFS rfsBTDevice = BluetoothConnectionRFS.getInstance();
    BluetoothConnection BTConnection = BluetoothConnection.getInstance();

    final String TAG = "tag";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_connected);
        getSupportActionBar().hide();

//        BTManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
//        List<BluetoothDevice> connected = BTManager.getConnectedDevices(GATT);
//        Log.i("Connected Devices: ", connected.size() + "");

        BTDevice = rfsBTDevice.getBluetoothDevice();
        BTSocket = rfsBTDevice.getBTSocket();

        disconnectButton = (Button) findViewById(R.id.BT_disconnect_button);
        disconnectButton.setOnClickListener(v -> BTdisconnect());
    }

    public void BTdisconnect() {
        try {
            BTInputStream = BTSocket.getInputStream();
            BTOutputStream = BTSocket.getOutputStream();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Input and Output Streams not open", Toast.LENGTH_SHORT).show();
        }

        if(BTInputStream != null) {
            try {
                BTInputStream.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Input stream not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTInputStream = null;
        }

        if(BTOutputStream != null) {
            try {
                BTOutputStream.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Output stream not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTOutputStream = null;
        }

        if(BTSocket != null) {
            try {
                BTSocket.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Socket not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTSocket = null;
        }

        Toast.makeText(getApplicationContext(), "BT Device Successfully Disconnected", Toast.LENGTH_SHORT).show();

        BTConnection.disconnect();

        Intent bluetoothConnectionActivityIntent = new Intent(BluetoothDisconnectionActivity.this, BluetoothConnectionActivity.class);
        startActivity(bluetoothConnectionActivityIntent);
    }
}
