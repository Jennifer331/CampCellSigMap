package com.example.campcellsigmap.entity;

public class MeasuredSignal {
    public int level;
    public int dbm;
    public int asu;

    public MeasuredSignal() {
        this(0, 0, 0);
    }

    public MeasuredSignal(int level, int dbm, int asu) {
        this.level = level;
        this.dbm = dbm;
        this.asu = asu;
    }
}
