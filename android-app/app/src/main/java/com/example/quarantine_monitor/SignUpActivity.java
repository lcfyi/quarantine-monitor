package com.example.quarantine_monitor;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.location.LocationManager.GPS_PROVIDER;

public class SignUpActivity extends AppCompatActivity implements LocationListener {
    private RequestQueue queue;
    private final int REQUEST_PERMISSION_LOCATION=1;
    private Double coordinates[];
    final private static String TAG = "SignUpActivity";
    private EditText usernameText;
    private EditText passwordText;
    private EditText baseStationIdText;
    private Button signUpButton;
    private LocationManager locationManager;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private SimpleDateFormat dateFormat;
    private boolean disableBackButton = false;
    private String token;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        queue = Volley.newRequestQueue(this);

        signUpButton = (Button) findViewById(R.id.btn_signUp);
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){signUp();}
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log and toast
                        Log.d(TAG, token);

                    }
                });

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);
        baseStationIdText = (EditText) findViewById(R.id.input_baseStationId);
        startTimeSpinner = (Spinner) findViewById(R.id.startTime_spinner);
        endTimeSpinner = (Spinner) findViewById(R.id.endTime_spinner);
        dateFormat = new SimpleDateFormat("HH:mm");

        coordinates = new Double [2];

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
        Log.d(TAG, "location manager running");
    }

    /*
     * @desc: this function sets the local location variables with the correct coordinates whenever
     * location has been changed.
     * */
    @Override
    public void onLocationChanged(@NonNull Location location){
        Log.d(TAG, coordinates.toString());
        coordinates[0] = location.getLongitude();
        coordinates[1] = location.getLatitude();
    }

    /*
     * @desc: this function is called after permissions have been granted. It shows toasts based
     * on what has happened.
     * */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SignUpActivity.this, "Permission Granted, Thank You!", Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
                } else {
                    Toast.makeText(SignUpActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Returning to login page");
                    Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
                break;
            default:
                break;
        }
    }

    private void signUp(){
        Log.d(TAG, "signup pressed");
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        String startTimeString = startTimeSpinner.getSelectedItem().toString();
        String endTimeString = endTimeSpinner.getSelectedItem().toString();
        String baseStationIdString = baseStationIdText.getText().toString();
        Integer baseStationId = 0;

        try{
            UserInfoHelper.setBaseStationId(Integer.parseInt(baseStationIdString));
            baseStationId = UserInfoHelper.getBaseStationId();
        }
        catch(NumberFormatException e){
            Toast.makeText(this,"Base Station ID input is not a number", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray coordinatesArray = new JSONArray();
        coordinatesArray.put(coordinates[0]);
        coordinatesArray.put(coordinates[1]);


        Log.d(TAG, coordinatesArray.toString());

        if(username.equals("") || password.equals("")){
            Toast.makeText(this,"Username or Password Empty", Toast.LENGTH_SHORT).show();
        }
        else if (startTimeString.equals(null) || endTimeString.equals(null)){
            Toast.makeText(this,"Please input proper start and end time", Toast.LENGTH_SHORT).show();
        }
        else{
            Date startTime = null;
            Date endTime = null;
            try {
                startTime = dateFormat.parse(startTimeString);
                endTime = dateFormat.parse(endTimeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.d(TAG, String.valueOf(startTime.getHours()));
            Log.d(TAG, String.valueOf(endTime.getHours()));

            if(endTime.getHours() - startTime.getHours() < 1){
                Toast.makeText(this,"Start time must occur before End time", Toast.LENGTH_SHORT).show();
            } else if (endTime.getTime() - startTime.getTime() < 10*60*6000) {
                Toast.makeText(this, "Availability range must be atleast 10 hours", Toast.LENGTH_SHORT).show();
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
                    userInfo.put("stationid", baseStationId);
                    userInfo.put("availability", setTime(startTime, endTime));

                    // Possible race condition but token should be retrieved before this statement
                    userInfo.put("token", token);
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
                            UserInfoHelper.setEndtime((long) response.get("endTime"));
                            UserInfoHelper.setAdmin((Boolean) response.get("admin"));

                            UserInfoHelper.createCookieFile(getApplicationContext());

                            Intent bluetoothIntent = new Intent(SignUpActivity.this, BluetoothConnectionActivity.class);
                            bluetoothIntent.putExtra("SignUpWorkflow", "True");
                            startActivity(bluetoothIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        Toast.makeText(SignUpActivity.this,"Username Already Exists", Toast.LENGTH_SHORT).show();
                    }
                });

                // Add the request to the RequestQueue
                queue.add(jsonObjectRequest);
            }

        }
    }

    @Override
    public void onBackPressed(){
        // if user presses back button before pressing signup button
        if(!disableBackButton) {
            Log.d(TAG, "Returning to login page");
            Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        else {

        }
    }

    private JSONArray setTime(Date startTime, Date endTime){

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        int hourOffset = Math.abs(offsetInMillis / 3600000);
        int minuteOffset = Math.abs((offsetInMillis / 60000) % 60);
        double startHour = startTime.getHours();
        double startMinute = startTime.getMinutes();
        double endHour = endTime.getHours();
        double endMinute = endTime.getMinutes();

        if(offsetInMillis >= 0){
            startHour += hourOffset;
            startMinute += minuteOffset;
            endHour += hourOffset;
            endMinute += minuteOffset;
        }
        else{
            startHour -= hourOffset;
            startMinute -= minuteOffset;
            endHour -= hourOffset;
            endMinute -= minuteOffset;
        }


        Double start = startHour * 6 + Math.floor(startMinute / 10);
        Double end = endHour* 6 + Math.floor(endMinute / 10);

        JSONArray timeArray = new JSONArray();
        timeArray.put(start);
        timeArray.put(end);

        return timeArray;
    }
}
