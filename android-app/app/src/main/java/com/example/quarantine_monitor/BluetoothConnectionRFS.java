package com.example.quarantine_monitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

// Singleton Class to keep track of the bluetooth device and bluetooth socket itself
public class BluetoothConnectionRFS {
    private static BluetoothConnectionRFS single_instance = null;

    public BluetoothDevice btDevice;
    public BluetoothSocket btSocket;

    private BluetoothConnectionRFS() {
        btDevice = null;
        btSocket = null;
    }

    public static BluetoothConnectionRFS getInstance() {
        if(single_instance == null) {
            single_instance = new BluetoothConnectionRFS();
        }
        return single_instance;
    }

    // connect bluetooth
    public void setBluetoothDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return btDevice;
    }

    public void setBTSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public BluetoothSocket getBTSocket() {
        return btSocket;
    }
}
