package com.example.quarantine_monitor;

// Singleton class to keep track of the bluetooth connection status
public class BluetoothConnection {
    private static BluetoothConnection single_instance = null;

    public boolean isConnected;

    private BluetoothConnection() {
        isConnected = false;
    }

    public static BluetoothConnection getInstance() {
        if(single_instance == null) {
            single_instance = new BluetoothConnection();
        }
        return single_instance;
    }

    // connect bluetooth
    public void connect() {
        isConnected = true;
    }

    // disconnect bluetooth
    public void disconnect() {
        isConnected = false;
    }

    // retrive connection value
    public boolean isConnected() {
        return isConnected;
    }
}
