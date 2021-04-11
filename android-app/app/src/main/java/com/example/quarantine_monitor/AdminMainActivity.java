package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/*
    Represents the home page for admins
 */
public class AdminMainActivity extends AppCompatActivity {

    private static final String TAG = AdminMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminhome);

        getActiveUsers();

        CardView statisticsCard = (CardView) findViewById(R.id.dashboard_card);
        statisticsCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent statsIntent = new Intent(AdminMainActivity.this, StatisticsActivity.class);
                startActivity(statsIntent);
            }
        });

        CardView notifLogCard = (CardView) findViewById(R.id.location_history_card);
        notifLogCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent notifLogPage = new Intent(AdminMainActivity.this, MapsActivity.class);
                startActivity(notifLogPage);
            }
        });

        CardView settingsCard = (CardView) findViewById(R.id.settings_card);
        settingsCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent settingsPage = new Intent(AdminMainActivity.this, SettingsActivity.class);
                startActivity(settingsPage);
            }
        });

    }

    @Override
    public void onBackPressed(){

    }

    /*
     *  Creates the message for the header text view which displays the number of active users
     */
    private void updateHeader(int activeUsers) {

        TextView greetingBox = findViewById(R.id.greeting);
        String message = null;
        if (UserInfoHelper.getAdmin()) {
            // Convert the difference from milliseconds to days left
            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
            String greeting;

            if (timeOfDay >= 0 && timeOfDay < 12) {
                greeting = "Good morning";
            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                greeting = "Good afternoon";
            } else {
                greeting = "Good evening";
            }

            message = String.format(greeting + ", you are currently monitoring %d quarantined users.", activeUsers);

        }
        greetingBox.setText(message);
    }

    /*
        Performs volley query to fetch users monitored by the admin
     */
    private void getActiveUsers() {
        // Get the number of active users object fields for admin
        int active = 0;
        String URL = "https://qmonitor-306302.wl.r.appspot.com/users/1/active";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, response.toString());
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String uuid = obj.get("_id").toString();
                                ActiveUserStats statsObj = new ActiveUserStats(obj.get("_id").toString());

                                JSONArray coords = obj.getJSONArray("lastCoords");
                                statsObj.lastCoords = new Double[]{coords.getDouble(0), coords.getDouble(1)};
                                statsObj.unixStartTime = obj.getLong("startTime");
                                statsObj.unixEndTime = obj.getLong("endTime");
                                UserInfoHelper.stats.put(uuid.substring(0,8), statsObj);
                            }

                            updateHeader(response.length());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            updateHeader(0);
                        };
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
                updateHeader(0);
            }
        });

        // Add the request to the RequestQueue
        VolleyQueue.getInstance(AdminMainActivity.this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

}
