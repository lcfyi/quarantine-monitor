package com.example.quarantine_monitor;

public class BluetoothThreadHelper {
    private static BluetoothThreadHelper single_instance = null;
    private volatile BluetoothThread btThread = new BluetoothThread();

    public static BluetoothThreadHelper getInstance() {
        if(single_instance == null) {
            single_instance = new BluetoothThreadHelper();
        }
        return single_instance;
    }

    public void reset() {
        single_instance = null;
    }

    public void createThread() {
        btThread.start();
    }

    public void destroyThread() {
        btThread.interrupt();
    }
}
