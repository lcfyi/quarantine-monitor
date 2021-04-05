package com.example.quarantine_monitor;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
 *  A global singleton class to store the Volley HTTP request queue among all the activities and fragments
 */ 
public class VolleyQueue {
    private static VolleyQueue instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VolleyQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyQueue getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
