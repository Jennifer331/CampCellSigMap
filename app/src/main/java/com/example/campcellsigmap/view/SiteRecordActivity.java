package com.example.campcellsigmap.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.example.campcellsigmap.R;
import com.example.campcellsigmap.entity.MeasuredSignal;
import com.example.campcellsigmap.entity.SignalStrength;
import com.example.campcellsigmap.utils.CSVHelper;
import com.example.campcellsigmap.utils.Config;
import com.example.campcellsigmap.utils.SignalMeasureHelper;
import com.example.campcellsigmap.utils.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.example.campcellsigmap.utils.Config.FILENAME;
import static com.example.campcellsigmap.utils.Config.SITE_FOLDER;


public class SiteRecordActivity extends AppCompatActivity {

    TelephonyManager mTeleManager;
    List<SignalStrength> mSamples = new LinkedList<>();
    List<Entry> mChartEntries = new LinkedList<>();
    Button mStopBtn;
    LineChart mLineChart;
    String mFilename;

    Location mLocation = new Location(LocationManager.GPS_PROVIDER);

    Handler mHandler = new Handler();
    Runnable mSampleTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilename = getIntent().getStringExtra(FILENAME);
        getLocation();
        setContentView(R.layout.activity_site_record);

        initView(savedInstanceState);
        initSigMearment();
    }

    private void initView(Bundle savedInstanceState) {
        mStopBtn = findViewById(R.id.stop);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTask();
                saveDataToFile();
                finish();
            }
        });

        initChart();
    }

    private void initSigMearment() {
        mTeleManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mSampleTask = new Runnable() {
            @Override
            public void run() {
                sample();
                mHandler.postDelayed(mSampleTask, getSamplingInterval());
            }
        };
        mSampleTask.run();
    }

    private int getSamplingInterval() {
        return Util.getInterval(this, Config.SITE_PREF_KEY, Config.SITE_SAMPLE_INTERVAL_DEFAULT);
    }

    private void initChart() {
        mLineChart = findViewById(R.id.chart);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        LineDataSet dataSet = new LineDataSet(mChartEntries, "level");
        dataSet.setColor(Color.GRAY);
        dataSet.setValueTextColor(Color.GRAY);

        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Util.flippedMillisecondToString((int) value);
            }
        });
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (null != location) {
                    mLocation = location;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        }
    }
    private void getLocation1() {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null)
                    continue;

                if (null == bestLocation || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            if (null != bestLocation) {
                mLocation = bestLocation;
            }
        }
    }

    private void stopTask() {
        mHandler.removeCallbacks(mSampleTask);
    }

    private void sample() {
        MeasuredSignal signal = SignalMeasureHelper.measureSigStrength(this, mTeleManager);
//        int level = Util.randomLevel();
        SignalStrength newSample = new SignalStrength(
                mLocation.getLatitude(),
                mLocation.getLongitude(),
                signal.level,
                signal.dbm,
                signal.asu,
                Calendar.getInstance().getTime());
        mSamples.add(newSample);
        addToChart(newSample);
    }


    private void addToChart(SignalStrength sample) {
        LineData data = mLineChart.getData();
        Entry newEntry = new Entry(Util.flippedMillisecond(sample.getDate()), sample.getDbm());
        data.addEntry(newEntry, 0);
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTask();
        saveDataToFile();
    }

    private void saveDataToFile() {
        CSVHelper.saveToFile(this, mSamples, SITE_FOLDER, mFilename);
    }
}
