package com.example.quarantine_monitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

import java.util.Date;

import static android.location.LocationManager.GPS_PROVIDER;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    final private static String TAG = "PushNotification";
    final private static int KEY_REQUEST_LOC = 0;
    final private static int KEY_VERIFY_IDEN = 1;
    final private static int KEY_ALERT_ADMIN = 2;
    Handler mHandler;

    @Override
    public void onMessageReceived(RemoteMessage message) {

        if(message == null || message.getData().get("key") == null){
            Log.d(TAG, "message received is invalid");
            return;
        }

        switch(Integer.parseInt(message.getData().get("key"))) {
            case KEY_REQUEST_LOC:
                submitLocation();
                break;
            case KEY_VERIFY_IDEN:
                super.onMessageReceived(message);
                Intent fvi = new Intent(getApplicationContext(), DetectorActivity.class);
                sendNotification(message, fvi);
                break;
            case KEY_ALERT_ADMIN:
                super.onMessageReceived(message);
                Intent hpi = new Intent(getApplicationContext(), MainActivity.class); //TODO: link to admin page
                sendNotification(message, hpi);
                break;
            default:
                return;
        }
    }

    @Override
    public void onNewToken(String token){
        String URL = "https://qmonitor-306302.wl.r.appspot.com/users/" + UserInfoHelper.getUserId();
        Log.d(TAG, URL);
        JSONObject userInfo = new JSONObject();
        try{
            //create json body to put into request
            userInfo.put("token", token);
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
        VolleyQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void submitLocation() {
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
        VolleyQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /*
        Handles notifications received in background and foreground and link it to the intent specified
     */
    private void sendNotification(RemoteMessage message, Intent ii) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notify_001");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);
        Bitmap licon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

        // Create notification and send to device
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentTitle(message.getData().get("title"));
        mBuilder.setContentText(message.getData().get("body"));
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getData().get("body")));
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setLargeIcon(licon);
        mBuilder.setTimeoutAfter(60*10*1000);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel for QMonitor",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        // Generate unique id
        int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        manager.notify(id, mBuilder.build());
    }

}

