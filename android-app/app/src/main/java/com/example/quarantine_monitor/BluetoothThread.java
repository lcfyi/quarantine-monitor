package com.example.quarantine_monitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;

// Separate thread class to handle bluetooth pinging sent to the De1
// Pinging is necessary to keep the connection alive
public class BluetoothThread extends Thread {
    String TAG = "bluetooth thread";
    private BluetoothSocket BTSocket = null;
    private BluetoothDevice BTDevice = null;
    private BluetoothConnectionRFS rfsBTDevice = BluetoothConnectionRFS.getInstance();
    private BluetoothConnection BTConnection = BluetoothConnection.getInstance();
    private volatile boolean running = true;

    protected void onCreate(Bundle savedInstanceState) {
        BTDevice = rfsBTDevice.getBluetoothDevice();
        BTSocket = rfsBTDevice.getBTSocket();
    }

    public void terminate(){
        running = false;
    }

    // Send random pings at 2.5second intervals OR
    // If facial verification is set, then send facial verification information instead of the ping
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            BTSocket = rfsBTDevice.getBTSocket();
            if (UserInfoHelper.getFvResult()) {
                UserInfoHelper.setFvResult(false);
                try {
                    BTSocket.getOutputStream().write(("set-face\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Log.i(TAG, "setupPinging: ping");
                    BTSocket.getOutputStream().write(("ping\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SystemClock.sleep(2500);
        }
        return;
    }
}
