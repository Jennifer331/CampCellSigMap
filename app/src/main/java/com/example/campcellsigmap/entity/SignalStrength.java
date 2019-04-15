package com.example.campcellsigmap.entity;


import java.util.Calendar;
import java.util.Date;

public class SignalStrength {
    private double mLatitude;
    private double mLongitude;
    private int mLevel;//0..4
    private int mDbm;
    private int mAsu;
    private Date mDate;


    public SignalStrength(double latitude, double longtitude, int level, int dbm, int asu, Date date) {
        mLatitude = latitude;
        mLongitude = longtitude;
        mLevel = level;
        mDbm = dbm;
        mAsu = asu;
        mDate = date;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongtitude() {
        return mLongitude;
    }

    public int getLevel() {
        return mLevel;
    }

    public int getDbm() {
        return mDbm;
    }

    public int getAsu() {
        return mAsu;
    }

    public Date getDate() {
        return mDate;
    }

}
