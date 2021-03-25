package com.example.quarantine_monitor;

public class UserInfoHelper {
    private static String userId;
    private static Double[] coordinates = new Double[2];

    public static String getUserId() {return userId;}
    public static void setUserId(String userId) {
        com.example.quarantine_monitor.UserInfoHelper.userId = userId;
    }

    public static Double[] getLocation() {return coordinates;}
    public static void setLocation(Double[] currentLocation){
        com.example.quarantine_monitor.UserInfoHelper.coordinates = currentLocation;
    }
}
