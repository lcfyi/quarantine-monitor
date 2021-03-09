package com.example.quarantine_monitor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.location.LocationManager.GPS_PROVIDER;

public class SignUpActivity extends AppCompatActivity implements LocationListener {
    private RequestQueue queue;
    private final int REQUEST_PERMISSION_LOCATION=1;
    private Double coordinates[];
    final private static String TAG = "SignUpActivity";
    private EditText usernameText;
    private EditText passwordText;
    private Button signUpButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        queue = Volley.newRequestQueue(this);

        coordinates = new Double [2];

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showExplanation("Allow location access?", "In order to use this application"  +
                            "we need to be able to access your location, for quarantine monitoring purposes.",
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);

        signUpButton = (Button) findViewById(R.id.btn_signUp);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){signUp();}
        });

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);

    }

    /*
     * @desc: this function sets the local location variables with the correct coordinates whenever
     * location has been changed.
     * */
    @Override
    public void onLocationChanged(@NonNull Location location){
        coordinates[0] = location.getLongitude();
        coordinates[1] = location.getLatitude();
    }

    /*
     * @desc: this function will show an explanation of why we need location permissions on this device.
     * Upon yes it will attempt to ask for permissions, and upon no it will return the user to the home page.
     * */
    private void showExplanation(String title, String message, String[] permissions, final int permissionCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permissions, permissionCode);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Returning to login page");
                        Intent homePageIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(homePageIntent);
                    }
                });
        builder.create().show();
    }

    /*
     * @desc: this function requests the location permissions from the user.
     * */
    private void requestPermission(String[] permissions, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                permissions, permissionRequestCode);
    }

    private void signUp(){
        Log.d(TAG, "signup pressed");
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        JSONArray coordinatesArray = new JSONArray();
        coordinatesArray.put(coordinates[0]);
        coordinatesArray.put(coordinates[1]);

        Log.d(TAG, coordinatesArray.toString());

        if(username.equals("") || password.equals("")){
            Toast.makeText(this,"username or password empty", Toast.LENGTH_SHORT).show();
        }
        else{
            String URL = "https://qmonitor-306302.wl.r.appspot.com/users";
            JSONObject userInfo = new JSONObject();
            try{
                Log.d(TAG, String.valueOf(coordinates[0]));
                Log.d(TAG, String.valueOf(coordinates[1]));
                //create json body to put into request
                userInfo.put("username", username);
                userInfo.put("password", password);
                userInfo.put("coordinates", coordinatesArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, userInfo.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, userInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    try {
                        UserInfoHelper.setUserId(response.get("_id").toString());
                        Intent homePageIntent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(homePageIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                    Toast.makeText(SignUpActivity.this,"username exists already", Toast.LENGTH_SHORT).show();
                }
            });

            // Add the request to the RequestQueue
            queue.add(jsonObjectRequest);

        }
    }


}
