package com.example.campcellsigmap.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.example.campcellsigmap.utils.Config.TIME_SPLITTER;

public class Util {

    public static int randomLevel() {
        Random random = new Random();
        return random.nextInt(5);
    }

    public static int flippedMillisecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        return millisecond + second * 1000 + minute * 60 * 1000 + hour * 60 * 60 * 1000;
    }

    public static String flippedMillisecondToString(int value) {
        int hour = (int) value / (60 * 60 * 1000);
        value %= (60 * 60 * 1000);
        int minute = (int) value / (60 * 1000);
        value %= (60 * 1000);
        int second = (int) value / 1000;
        int millisecond = (int) value % 1000;
        StringBuilder sb = new StringBuilder();
        sb
                .append(hour).append(TIME_SPLITTER)
                .append(minute).append(TIME_SPLITTER)
                .append(second).append(".")
                .append(millisecond);
        return sb.toString();
    }


    public static int getInterval(Context context, String key, String defaultVal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(key, defaultVal);
        int interval = Integer.parseInt(value);
        return interval;
    }
}
