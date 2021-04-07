package com.example.quarantine_monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.service.controls.Control;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.example.quarantine_monitor.BluetoothConnection;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.GATT;

public class BluetoothConnectionActivity extends AppCompatActivity {
//    BluetoothManager bluetoothManager;
//    private BluetoothAdapter BA = null;
//    private Set Devices;
//
//    private Set<BluetoothDevice> pairedDevices;
//    private Set<BluetoothDevice> PD;
//    private BluetoothDevice bluetoothDevice;
//    private BluetoothSocket bluetoothSocket;
//    private List<BluetoothDevice> connectedDevices;
//    ArrayAdapter<String> pairedDevices_ArrayAdapter;

//    private boolean isBtConnected = false;

    ListView devicelist;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter BA = null;
    private Set<BluetoothDevice> Devices;

    private String address = null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

//    private boolean ConnectSuccess = false;

    // singleton class to keep track of bluetooth connection
    BluetoothConnection isBTConnected = BluetoothConnection.getInstance();
    BluetoothConnectionRFS rfsBTConnection = BluetoothConnectionRFS.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect);
        getSupportActionBar().hide();

        if(!isBTConnected.isConnected()) {
            Context context = this;

            BA = BluetoothAdapter.getDefaultAdapter();
            pairedDevices();

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            this.registerReceiver(broadcastReceiver, filter);
        }
        else {
            setContentView(R.layout.activity_bt_connected);
        }
    }

    private void pairedDevices() {
        Devices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        if(Devices.size() > 0) {
            for(BluetoothDevice bt : Devices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found", Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist = findViewById(R.id.paired_bluetooth_device_list);
        devicelist.setAdapter(adapter);

        devicelist.setOnItemClickListener(myListListener);
    }

    private AdapterView.OnItemClickListener myListListener = new AdapterView.OnItemClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView)view).getText().toString();
            address = info.substring(info.length() - 17);
            String[] infoArr = info.split("\n");

            if(!infoArr[0].equals("hc01.com HC-05")) {
                Toast.makeText(getApplicationContext(), "Incorrect Device Selected\nPlease connect to hc01.com HC-05",    Toast.LENGTH_SHORT).show();
            }
            else {
                if (!isBTConnected.isConnected()) {
                    new ConnectBT().execute();
                } else {
                    setContentView(R.layout.activity_bt_connected);
                }
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        BluetoothDevice device;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Device is now Connected",    Toast.LENGTH_SHORT).show();

                Intent bluetoothDisconnectionActivityIntent = new Intent(BluetoothConnectionActivity.this, BluetoothDisconnectionActivity.class);
                startActivity(bluetoothDisconnectionActivityIntent);

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Device Not Connected",       Toast.LENGTH_SHORT).show();

//                setContentView(R.layout.activity_bt_not_connected);
//                getPairedDevices();
            }
        }
    };

    private boolean bluetoothConnect(){
        try{
            rfsBTConnection.getBTSocket().connect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Connecting... Please Wait",    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (btSocket == null || !isBTConnected.isConnected()) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();

                    BluetoothDevice hc = myBluetooth.getRemoteDevice(address);

                    btSocket = hc.createInsecureRfcommSocketToServiceRecord(MY_UUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    rfsBTConnection.setBluetoothDevice(hc);
                    rfsBTConnection.setBTSocket(btSocket);

                    if(bluetoothConnect()){
                        isBTConnected.connect();
                    }

                }
            } catch (IOException e) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        BluetoothConnectionRFS rfsBTConnection = BluetoothConnectionRFS.getInstance();
                        try {
                            rfsBTConnection.getBTSocket().close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Error Detected & Connection Failed\nPlease make sure the Bluetooth Device is Turned on and within range", Toast.LENGTH_SHORT).show();
                    }
                });

//                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!isBTConnected.isConnected()) {
                Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
//            else {
////                Toast.makeText(getApplicationContext(), "Connected. Please do not disconnect", Toast.LENGTH_SHORT).show();
//                isBTConnected.connect();
//            }
        }
    }

//    private void connect() {
//        try {
//            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
//            service.connect(socket);
//        } catch (Exception e) {
//            onSerialConnectError(e);
//        }
//    }
//
//    public String getLocalBluetoothName(){
//        if(BA == null){
//            BA = BluetoothAdapter.getDefaultAdapter();
//        }
//        String name = BA.getName();
//        if(name == null){
//            System.out.println("Name is null!");
//            name = BA.getAddress();
//        }
//        return name;
//    }

//    public void getPairedDevices(){
//        pairedDevices = BA.getBondedDevices();
//
//        pairedDevices_ArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_connect);
//        if(pairedDevices.size()>0){
//            for(BluetoothDevice device : pairedDevices){
//                pairedDevices_ArrayAdapter.add(device.getName()+"\n"+device.getAddress());
//            }
//        }
//        pairedDevices_ListView = findViewById(R.id.PDlistview);
//        pairedDevices_ListView.setAdapter(pairedDevices_ArrayAdapter);
////        pairedDevices_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                String i = ((TextView) view).getText().toString();
////                String address = i.substring(i.length() - 17);
////                deviceToConnectTo = BA.getRemoteDevice(address);
//////                connectToDevice(deviceToConnectTo);
////            }
////        });
//    }
//
////    public void connectToDevice(BluetoothDevice device){
////        try {
////            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
////            BA.cancelDiscovery();
////        } catch (IOException e){}
////
////        try{
////            bluetoothSocket.connect();
////        } catch (IOException e){
////            Toast.makeText(getApplicationContext(), "Error Connecting To Device",       Toast.LENGTH_SHORT).show();
////            try{
////                bluetoothSocket.close();
////            } catch (IOException exception){}
////        }
////    }
}
