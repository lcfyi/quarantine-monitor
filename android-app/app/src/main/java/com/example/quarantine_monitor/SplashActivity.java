package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Bypasses login page if previously logged in
        if (UserInfoHelper.containsCookieFile(getApplicationContext())) {
            String data = UserInfoHelper.readCookieFile(getApplicationContext());

            // Get the user object fields for requested user id
            // Delete the stored device token on the server
            String URL = "https://qmonitor-306302.wl.r.appspot.com/users/" + data;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                UserInfoHelper.setUserId(response.get("_id").toString());
                                UserInfoHelper.setEndtime((long) response.get("endTime"));
                                UserInfoHelper.setAdmin((Boolean) response.get("admin"));

                                // Create the cookie file to store user login state
                                UserInfoHelper.createCookieFile(getApplicationContext());

                                Intent homePageIntent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(homePageIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            };
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());

                    // Delete the cookie file and refresh the login page
                    UserInfoHelper.deleteCookieFile(getApplicationContext());
                    Toast.makeText(SplashActivity.this,"Error Logging In", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            });

            // Add the request to the RequestQueue
            VolleyQueue.getInstance(SplashActivity.this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}
