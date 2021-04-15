package com.example.quarantine_monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 *  This class stores information about active users which is populated for admins
 *  and used throughout the admin dashboard
 */
public class ActiveUserStats {
    public String uuid = null;
    public String shortenedUuid = null;
    public Long unixEndTime;
    public Long unixStartTime;
    public Double[] lastCoords = new Double[2];
    public LinkedHashMap<Date, Boolean> statusMap = new LinkedHashMap<>();
    public LinkedHashMap<Date, Double[]>  coordinateMap = new LinkedHashMap<>();
    public LinkedHashMap<Date, Boolean> testStatusMap = new LinkedHashMap<>();

    public ActiveUserStats (String id) {
        uuid = id;
        shortenedUuid = id.substring(0, 8);
    }

    public String getShortenedUuid(){
        return shortenedUuid;
    }
}
