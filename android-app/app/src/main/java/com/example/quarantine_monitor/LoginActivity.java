package com.example.quarantine_monitor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    final private static String TAG = "LoginActivity";
    public String user_Id;
    private Button loginButton;
    private EditText usernameText;
    private EditText passwordText;
    private TextView signUpLink;
    private String token;
    private final int REQUEST_PERMISSION_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},1);

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

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){loginButton();}
        });

        usernameText = (EditText) findViewById(R.id.input_username);
        passwordText = (EditText) findViewById(R.id.input_password);

        signUpLink = (TextView) findViewById(R.id.link_signup);
        signUpLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signUpPageIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpPageIntent);
            }
        });
    }

    /*
     * @desc: this function requests the location permissions from the user.
     * */
    private void requestPermission(String[] permissions, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                permissions, permissionRequestCode);
    }

    public void loginButton(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
            return;
        }

        login();
    }

    public void login(){
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String URL = "https://qmonitor-306302.wl.r.appspot.com/users/login";
        JSONObject userInfo = new JSONObject();

        Log.d(TAG, "attempting login");

        if(username.equals("") || password.equals("")){
            Toast.makeText(this,"Username or Password Empty", Toast.LENGTH_SHORT).show();
        }
        else{
            try{
                //create json body to put into request
                userInfo.put("username", username);
                userInfo.put("password", password);
                userInfo.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, userInfo,
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

                                Intent homePageIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(homePageIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                    Toast.makeText(LoginActivity.this,"Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d(TAG, userInfo.toString());
            // Add the request to the RequestQueue
            VolleyQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);

        }
    }

    @Override
    public void onBackPressed(){ }
}

