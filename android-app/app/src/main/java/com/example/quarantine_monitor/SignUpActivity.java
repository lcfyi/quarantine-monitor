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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.location.LocationManager.GPS_PROVIDER;

public class SignUpActivity extends Activity implements LocationListener {
    private RequestQueue queue;
    private final int REQUEST_PERMISSION_LOCATION=1;
    private Map<String, Double> locationDetails;
    private Intent findPlayersIntent;
    final private static String TAG = "SignUpActivity";
    private EditText usernameText;
    private EditText passwordText;
    private Button signUpButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        locationDetails = new HashMap<>();
        queue = Volley.newRequestQueue(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showExplanation("Allow location access?", "In order to use this application"  +
                            "we need to be able to access your location, for quarantine monitoring purposes.",
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);

        signUpButton = (Button) findViewById(R.id.btn_signup);
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
    public void onLocationChanged(@NonNull Location location) {
        locationDetails.put("longitude", location.getLongitude());
        locationDetails.put("latitude", location.getLatitude());
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


    //TODO: implement signup volley request
    private void signUp(){

    }


}
