package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    final private static String TAG = "LoginActivity";
    public String user_Id;
    private RequestQueue queue;
    private Button loginButton;
    private EditText usernameText;
    private EditText passwordText;
    private TextView signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RequestQueue queue = Volley.newRequestQueue(this);

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){login();}
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

    //TODO: integrate firebase + get device registration token, atm it's blank.
    public void login(){
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if(username.equals("") || password.equals("")){
            Toast.makeText(this,"username or password empty", Toast.LENGTH_SHORT).show();
        }
        else{
            String URL = "https://qmonitor-306120.wl.r.appspot.com/users/login";
            JSONObject userInfo = new JSONObject();
            try{
                //create json body to put into request
                userInfo.put("username", username);
                userInfo.put("password", password);
                userInfo.put("deviceToken", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, userInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    try {
                        UserInfoHelper.setUserId(response.get("userid").toString());
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
                    Toast.makeText(LoginActivity.this,"username or password incorrect", Toast.LENGTH_SHORT).show();
                }
            });

            // Add the request to the RequestQueue
            queue.add(jsonObjectRequest);

        }
    }
}

