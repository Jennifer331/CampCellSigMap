package com.example.campcellsigmap.utils;

public class Config {
    public static final int MARKER_DIAMETER = 20;
    public static final String TRIP_FOLDER = "trip_records";
    public static final String SITE_FOLDER = "site_records";
    public static final String FILENAME = "filename";
    public static final String ALL = "all";

//    public static final int SITE_SAMPLE_INTERVAL = 1000 * 60;//1min
    public static final String SITE_SAMPLE_INTERVAL_DEFAULT = "60000";//0.5s
    public static final String TRIP_SAMPLE_INTERVAL_DEFAULT = "500";//0.5s

    public static final String TIME_SPLITTER = ":";

    public static final String TRIP_PREF_KEY = "trip_pref";
    public static final String SITE_PREF_KEY = "site_pref";

    public enum Mode {
        LEVEL,
        RSRP,
        ASU
    }
}
