package com.example.quarantine_monitor;

public class UserInfoHelper {
    private static String userId;
    public static String getUserId() {return userId;}
    public static void setUserId(String userId) {
        com.example.quarantine_monitor.UserInfoHelper.userId = userId;
    }
}
