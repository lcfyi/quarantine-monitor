package com.example.quarantine_monitor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;

/*
    Manages global information about the user
 */
public class UserInfoHelper {
    private static String userId;
    private static Double[] coordinates = new Double[2];
    private static Boolean admin;
    private static long endTime;
    public static LinkedHashMap<String, ActiveUserStats> stats = new LinkedHashMap<>();

    private static final String cookieFileName = "config.txt";

    public static String getUserId() {return userId;}
    public static void setUserId(String userId) {
        com.example.quarantine_monitor.UserInfoHelper.userId = userId;
    }

    public static Double[] getLocation() {return coordinates;}
    public static void setLocation(Double[] currentLocation){
        com.example.quarantine_monitor.UserInfoHelper.coordinates = currentLocation;
    }

    public static Boolean getAdmin() {return admin;}
    public static void setAdmin(Boolean admin) {
        com.example.quarantine_monitor.UserInfoHelper.admin = admin;
    }

    public static long getEndtime() {return endTime;}
    public static void setEndtime(long endTime) {
        com.example.quarantine_monitor.UserInfoHelper.endTime = endTime;
    }

    /*
        Checks if the cookie file containing the user id exists
     */
    public static boolean containsCookieFile(Context ctx) {
        String[] files = ctx.fileList();
        return Arrays.asList(files).contains(cookieFileName);
    }

    /*
        Creates a cookie file containing the user id
     */
    public static void createCookieFile(Context ctx) {
        try {
            OutputStreamWriter stream = new OutputStreamWriter(ctx.openFileOutput(cookieFileName, Context.MODE_PRIVATE));
            stream.write(getUserId());
            stream.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /*
        Creates a cookie file containing the user id
     */
    public static String readCookieFile(Context ctx) {
        try {
            FileInputStream fis = ctx.openFileInput(cookieFileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            Log.e("Exception", "File read failed: " + e.toString());
            return "";
        }
    }

    /*
        Used for signing out, deletes the cookie file
     */
    public static void deleteCookieFile(Context ctx) {
        File file = new File(ctx.getFilesDir() + "/" + cookieFileName);
        file.delete();
    }
}
