package com.example.quarantine_monitor;

import android.app.Activity;
import android.content.Context;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

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

    // Declare a variable for the cluster manager.
    private ClusterManager<GroupClusterItem> clusterManager;

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
        setUpClusterer();
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
            // NOTE: that only one of individual marker icons and
            String title = "Time recorded: " + new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH).format(entry.getKey());
            float timeSinceStart = entry.getKey().getTime() - userStats.unixStartTime;
            float totalTime = userStats.unixEndTime - userStats.unixStartTime;
            float hue_percentage =  timeSinceStart / totalTime;

            MarkerOptions marker = new MarkerOptions();
            LatLng coordinates = new LatLng(entry.getValue()[1], entry.getValue()[0]);
            marker.position(coordinates);
            marker.title(title);
            marker.zIndex(zIndex++);
            marker.icon(BitmapDescriptorFactory.defaultMarker(140f*hue_percentage));

            builder.include(marker.getPosition());
            //mMap.addMarker(marker);
            GroupClusterItem item = new GroupClusterItem(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle(), marker.getSnippet(), marker.getIcon());
            clusterManager.addItem(item);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<GroupClusterItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setRenderer(new ClusterRenderer(getApplicationContext(), mMap, clusterManager));
    }
}

class GroupClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    BitmapDescriptor icon;

    public GroupClusterItem(double lat, double lng, String title, String snippet, BitmapDescriptor ic) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.icon = ic;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }
}

class ClusterRenderer extends DefaultClusterRenderer<GroupClusterItem> {

    public ClusterRenderer(Context context, GoogleMap map,
                       ClusterManager<GroupClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(GroupClusterItem item, MarkerOptions markerOptions) {

        markerOptions.icon(item.getIcon());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
