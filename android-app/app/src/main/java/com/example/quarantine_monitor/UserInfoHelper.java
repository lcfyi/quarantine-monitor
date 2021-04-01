package com.example.quarantine_monitor;

public class UserInfoHelper {
    private static String userId;
    private static Double[] coordinates = new Double[2];
    private static Boolean admin;
    private static long endTime;

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
}
