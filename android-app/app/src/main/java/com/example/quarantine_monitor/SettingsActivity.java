package com.example.quarantine_monitor;

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

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RequestQueue queue = VolleyQueue.getInstance(this.getApplicationContext()).
                getRequestQueue();

        Button signoutButton = (Button) findViewById(R.id.btn_signOut);
        signoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
}
