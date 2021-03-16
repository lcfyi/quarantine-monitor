package com.example.quarantine_monitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.location.LocationManager.GPS_PROVIDER;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    final private static String TAG = "PushNotification";
    Handler mHandler;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        RequestQueue queue = Volley.newRequestQueue(this);

        if(message != null){
            Log.d(TAG, message.toString());
        }
        Log.d(TAG, "message received");

        Double[] coordinates = UserInfoHelper.getLocation();

        JSONArray coordinatesArray = new JSONArray();
        coordinatesArray.put(coordinates[0]);
        coordinatesArray.put(coordinates[1]);

        String URL = "https://qmonitor-306302.wl.r.appspot.com/users/" + UserInfoHelper.getUserId();
        Log.d(TAG, URL);
        JSONObject userInfo = new JSONObject();
        try{
            Log.d(TAG, String.valueOf(coordinates[0]));
            Log.d(TAG, String.valueOf(coordinates[1]));
            //create json body to put into request
            userInfo.put("coordinates", coordinatesArray);
            Log.d(TAG, userInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, userInfo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onNewToken(String token){
        //todo: should i set a volley request here?
        Log.d(TAG, token);
    }


}

