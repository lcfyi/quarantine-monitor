package com.example.quarantine_monitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private BluetoothThreadHelper BTThreadHelper = BluetoothThreadHelper.getInstance();
    private BluetoothConnectionRFS rfsBTDevice = BluetoothConnectionRFS.getInstance();
    private BluetoothConnection BTConnection = BluetoothConnection.getInstance();
    private BluetoothSocket BTSocket = null;
    private BluetoothDevice BTDevice = null;
    private InputStream BTInputStream = null;
    private OutputStream BTOutputStream = null;
    private boolean errorFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RequestQueue queue = VolleyQueue.getInstance(this.getApplicationContext()).
                getRequestQueue();

        BTDevice = rfsBTDevice.getBluetoothDevice();
        BTSocket = rfsBTDevice.getBTSocket();

        Button signoutButton = (Button) findViewById(R.id.btn_signOut);
        signoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Delete user's pinging thread - (properly disconnect from bt)
                BTdisconnect();

                // Delete the cookie file in the app-specific folder
                UserInfoHelper.deleteCookieFile(getApplicationContext());

                // Delete the stored device token on the server
                String URL = "https://qmonitor-306302.wl.r.appspot.com/users/" + UserInfoHelper.getUserId() + "/devicetoken";
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Delete cookie file
                                Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        Toast.makeText(SettingsActivity.this,"Error Signing Out", Toast.LENGTH_SHORT).show();
                    }
                });
                // Add the request to the RequestQueue
                VolleyQueue.getInstance(SettingsActivity.this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
    }

    public void BTdisconnect() {
        BTThreadHelper.reset();
        BTThreadHelper.destroyThread();
        try {
            BTInputStream = BTSocket.getInputStream();
            BTOutputStream = BTSocket.getOutputStream();
        } catch (Exception e) {
            errorFound = true;
            Log.i(TAG, "Input and Output Streams not open");
//            Toast.makeText(getApplicationContext(), "Input and Output Streams not open", Toast.LENGTH_SHORT).show();
        }

        if(BTInputStream != null) {
            try {
                BTInputStream.close();
            } catch (Exception e) {
                errorFound = true;
                Log.i(TAG, "Input stream not correctly closed");
//                Toast.makeText(getApplicationContext(), "Input stream not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTInputStream = null;
        }

        if(BTOutputStream != null) {
            try {
                BTOutputStream.close();
            } catch (Exception e) {
                errorFound = true;
                Log.i(TAG, "Output stream not correctly closed");
//                Toast.makeText(getApplicationContext(), "Output stream not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTOutputStream = null;
        }

        if(BTSocket != null) {
            try {
                BTSocket.close();
            } catch (Exception e) {
                errorFound = true;
                Log.i(TAG, "Socket not correctly closed");
//                Toast.makeText(getApplicationContext(), "Socket not correctly closed", Toast.LENGTH_SHORT).show();
            }
            BTSocket = null;
        }

        if(errorFound == false && BTSocket == null && BTInputStream == null && BTOutputStream == null) {
            BTConnection.disconnect();
            finish();
        }
    }
}
