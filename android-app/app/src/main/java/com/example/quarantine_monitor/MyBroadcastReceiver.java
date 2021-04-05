package com.example.quarantine_monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//Class to start location service on boot
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("startup", "background service started");
        Intent startServiceIntent = new Intent(context, BackgroundLocationService.class);
        context.startService(startServiceIntent);
    }
}
