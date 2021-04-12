package com.example.quarantine_monitor;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

public class Info extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));
    }
}