package com.example.quarantine_monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

/*
    This activity is where admins can view the plot maps for various users that they are tracking
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private HashSet<String> queriedUuids = new HashSet<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_map);

        Spinner user_spinner = (Spinner) findViewById(R.id.user_spinner);
        user_spinner.setOnItemSelectedListener(this);
        user_spinner.setPrompt("User");

        //Creating the ArrayAdapter instance having the user list
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item_text, UserInfoHelper.stats.keySet().toArray());
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        user_spinner.setAdapter(aa);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed(){
        Intent homePage = new Intent(MapsActivity.this, AdminMainActivity.class);
        startActivity(homePage);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Initialize the map to Vancouver
        LatLng vancouver = new LatLng(49.28, -123.12);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vancouver, 14.0f));
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long l) {
        // When the user is changed, we query the server and receive information for that user, update the model
        String shortUuid = arg0.getItemAtPosition(position).toString();
        String uuid = UserInfoHelper.stats.get(shortUuid).uuid;

        if (!queriedUuids.contains(shortUuid)) {
            fetchAndPopulateUserLocationMap(uuid, shortUuid);
        } else {
            drawMarkers(shortUuid);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void drawMarkers(String shortUuid) {
        ActiveUserStats userStats = UserInfoHelper.stats.get(shortUuid);

        // Create markers for all the user recorded locations
        int totalEntries = userStats.coordinateMap.size();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int zIndex = 0;

        for (Map.Entry<Date, Double[]> entry : userStats.coordinateMap.entrySet()) {

            String title = "Time recorded: " + new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH).format(entry.getKey());
            double hue_percentage = (entry.getKey().getTime() - userStats.unixStartTime) / (userStats.unixEndTime - userStats.unixStartTime); //TODO: Color the markers on a time scale

            MarkerOptions marker = new MarkerOptions();
            LatLng coordinates = new LatLng(entry.getValue()[1], entry.getValue()[0]);
            marker.position(coordinates);
            marker.title(title);
            marker.zIndex(zIndex++);

            builder.include(marker.getPosition());
            mMap.addMarker(marker);
        }

        mMap.setOnMarkerClickListener(this);

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.20); // offset from edges of the map 20% of screen

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    /*
        Fetches user location map for each user
     */
    private void fetchAndPopulateUserLocationMap(String user, String shortUuid) {

        // Get the user object fields for requested user id
        String URL = "https://qmonitor-306302.wl.r.appspot.com/users/" + user + "/plotmap";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            ActiveUserStats stats = UserInfoHelper.stats.get(shortUuid);
                            stats.coordinateMap.clear();
                            stats.statusMap.clear();
                            // Populate location map information
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                Date date = new Date(obj.getLong("time"));

                                JSONArray coords = obj.getJSONArray("coordinates");
                                stats.coordinateMap.put(date, new Double[]{coords.getDouble(0), coords.getDouble(1)});
                                stats.statusMap.put(date, obj.getBoolean("status"));
                            }
                            UserInfoHelper.stats.put(shortUuid, stats);
                            drawMarkers(shortUuid);

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
        VolleyQueue.getInstance(MapsActivity.this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
