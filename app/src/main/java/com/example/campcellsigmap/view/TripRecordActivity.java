package com.example.campcellsigmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.campcellsigmap.R;
import com.example.campcellsigmap.entity.MeasuredSignal;
import com.example.campcellsigmap.entity.SignalStrength;
import com.example.campcellsigmap.utils.CSVHelper;
import com.example.campcellsigmap.utils.Config;
import com.example.campcellsigmap.utils.SignalIndicator;
import com.example.campcellsigmap.utils.SignalMeasureHelper;
import com.example.campcellsigmap.utils.Util;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.example.campcellsigmap.utils.Config.FILENAME;
import static com.example.campcellsigmap.utils.Config.TRIP_FOLDER;


public class TripRecordActivity extends AppCompatActivity {

    MapView mMapView = null;
    AMap mAMap = null;
    Location mCurLocation = null;
    TelephonyManager mTeleManager = null;
    List<SignalStrength> mSamples = new LinkedList<>();
    Button mStopBtn;
    String mFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilename = getIntent().getStringExtra(FILENAME);
        setContentView(R.layout.activity_trip_record);
        initView(savedInstanceState);
        initSigMearment();
        initMap();
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = (MapView)findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mStopBtn = (Button)findViewById(R.id.stop);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFile();
                finish();
            }
        });
    }

    private void initMap() {
        if (null == mAMap) {
            mAMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(getSamplingInterval());
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocationStyle.showMyLocation(false);
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(19));//3-19
        mAMap.setMyLocationEnabled(true);
        mAMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (null == mCurLocation)
                    mCurLocation = new Location(location);
                else {
                    mCurLocation.set(location);
                }
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                dealSigStrength(latLng);
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 19);
                mAMap.moveCamera(cu);
            }
        });
    }

    private void initSigMearment() {
        mTeleManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    private LatLng fromGpsToLatLng(Location location) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(location.getLatitude(), location.getLongitude()));
        return converter.convert();
    }

    private int getSamplingInterval() {
        return Util.getInterval(this, Config.TRIP_PREF_KEY, Config.TRIP_SAMPLE_INTERVAL_DEFAULT);
    }

    private void dealSigStrength(LatLng latLng) {
        MeasuredSignal measuredSignal = SignalMeasureHelper.measureSigStrength(this, mTeleManager);
//        int level = randomLevel();
        mSamples.add(
                new SignalStrength(
                        latLng.latitude,
                        latLng.longitude,
                        measuredSignal.level,
                        measuredSignal.dbm,
                        measuredSignal.asu,
                        Calendar.getInstance().getTime()));
        addMarker(measuredSignal.dbm, latLng);
    }

    private Bitmap getIndicator(int level) {
        int indicatorId = R.drawable.strength_indicator0;
        switch (level) {
            case 0:
                indicatorId = R.drawable.strength_indicator0;
                break;
            case 1:
                indicatorId = R.drawable.strength_indicator1;
                break;
            case 2:
                indicatorId = R.drawable.strength_indicator2;
                break;
            case 3:
                indicatorId = R.drawable.strength_indicator3;
                break;
            case 4:
                indicatorId = R.drawable.strength_indicator4;
                break;
        }
        Drawable drawable = getDrawable(indicatorId);
        Bitmap bitmap = Bitmap.createBitmap(Config.MARKER_DIAMETER, Config.MARKER_DIAMETER, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void addMarker(int level, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        Bitmap indicator = SignalIndicator.getIndicator(this, -1, 500, level);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(indicator));
        mAMap.addMarker(markerOptions);
    }

    private int randomLevel() {
        Random random = new Random();
        return random.nextInt(5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveDataToFile();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private void saveDataToFile() {
        CSVHelper.saveToFile(this, mSamples, TRIP_FOLDER, mFilename);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

}
