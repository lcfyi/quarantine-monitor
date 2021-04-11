package com.example.quarantine_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/*
    Screen to represent the dashboard to view statistics per user
 */
public class StatisticsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ViewPager viewPager;
    PagerDataAdapter adapter;
    HashSet<String> queriedUuids = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Spinner user_spinner = (Spinner) findViewById(R.id.user_spinner);
        user_spinner.setOnItemSelectedListener(this);
        user_spinner.setPrompt("User");

        //Creating the ArrayAdapter instance having the user list
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item_text, UserInfoHelper.stats.keySet().toArray());
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        user_spinner.setAdapter(aa);

        // Creates the view pager adapter
        adapter = new PagerDataAdapter(this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPadding(20, 0, 20, 0);
    }

    @Override
    public void onBackPressed(){
        Intent homePage = new Intent(StatisticsActivity.this, AdminMainActivity.class);
        startActivity(homePage);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        // When the user is changed, we query the server and receive information for that user, update the model
        String shortUuid = arg0.getItemAtPosition(position).toString();
        String uuid = UserInfoHelper.stats.get(shortUuid).uuid;

        if (queriedUuids.contains(shortUuid)) {
            adapter.setModel(UserInfoHelper.stats.get(shortUuid));
            viewPager.setAdapter(adapter);
        } else {
            fetchAndPopulateTests(uuid, shortUuid); //TODO: add labels
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) { }

    /*
        Fetches test information for user via GET query and notifies dataset changed to the viewpager
     */
    private void fetchAndPopulateTests(String uuid, String shortUuid) {

        // Get the user object fields for requested user id
        String URL = "https://qmonitor-306302.wl.r.appspot.com/tests/?userid=" + uuid;
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            ActiveUserStats stats = UserInfoHelper.stats.get(shortUuid);
                            stats.testStatusMap.clear();
                            // Populate location map information
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                Date date = new Date(obj.getLong("time"));
                                if (obj.getInt("status") == 1) {
                                    stats.testStatusMap.put(date, true);
                                } else {
                                    stats.testStatusMap.put(date, false);
                                }
                            }
                            UserInfoHelper.stats.put(shortUuid, stats);

                            queriedUuids.add(shortUuid);
                            adapter.setModel(stats);
                            viewPager.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        };
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue
        VolleyQueue.getInstance(StatisticsActivity.this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
