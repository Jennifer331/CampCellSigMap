package com.example.campcellsigmap.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.campcellsigmap.R;
import com.example.campcellsigmap.entity.SignalStrength;
import com.example.campcellsigmap.utils.CSVHelper;
import com.example.campcellsigmap.utils.Config;
import com.example.campcellsigmap.utils.SignalIndicator;

import java.util.List;

import static com.example.campcellsigmap.utils.Config.ALL;
import static com.example.campcellsigmap.utils.Config.FILENAME;
import static com.example.campcellsigmap.utils.Config.TRIP_FOLDER;

public class TripShowActivity extends Activity {

    List<SignalStrength> mSamples;
    Config.Mode mMode = Config.Mode.RSRP;

    MapView mMapView = null;
    AMap mAMap = null;
    String mFileName = null;

    Button mLevelBtn;
    Button mRsrpBtn;
    Button mAsuBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFileName = getIntent().getStringExtra(FILENAME);
        if (null == mFileName) {
            finish();
        }

        setContentView(R.layout.activity_trip_show);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mSamples = getSamples();

        initView();
        initMap();
    }

    private void initView() {
        mLevelBtn = findViewById(R.id.level);
        mRsrpBtn = findViewById(R.id.rsrp);
        mAsuBtn = findViewById(R.id.asu);

        mLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = Config.Mode.LEVEL;
                validate();
            }
        });
        mRsrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = Config.Mode.RSRP;
                validate();
            }
        });
        mAsuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = Config.Mode.ASU;
                validate();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void initMap() {
        if (null == mAMap) {
            mAMap = mMapView.getMap();
        }

        addMarkers(mMode);

        cameraUpdate(mSamples);
    }

    private List<SignalStrength> getSamples() {
        List<SignalStrength> samples;
        if (mFileName.equals(ALL)) {
            samples = CSVHelper.readFromFolder(this, TRIP_FOLDER);
//            CSVHelper.saveToFile(this, samples, TRIP_FOLDER, "all");
        } else {
            samples = CSVHelper.readFromFile(this, TRIP_FOLDER, mFileName);
        }
        return samples;
    }

    private void addMarkers(List<SignalStrength> samples) {
        for (SignalStrength sample : samples) {
            MarkerOptions markerOptions = new MarkerOptions().position(
                    new LatLng(sample.getLatitude(), sample.getLongtitude()));
            Bitmap indicator = getIndicator(sample.getLevel());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(indicator));
            mAMap.addMarker(markerOptions);
        }
    }

    private void validate() {
        mAMap.clear();
        addMarkers(mMode);
    }

    private void addMarkers(Config.Mode mode) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (SignalStrength sample : mSamples) {
            int value = 0;
            switch (mode) {
                case LEVEL:
                    value = sample.getLevel();
                    break;
                case RSRP:
                    value = sample.getDbm();
                    break;
                case ASU:
                    value = sample.getAsu();
                    break;
            }
            if (value < min) {
                min = value;
            } else if (value > max) {
                max = value;
            }
        }

        for (SignalStrength sample : mSamples) {
            int value = 0;
            switch (mode) {
                case LEVEL:
                    value = sample.getLevel();
                    break;
                case RSRP:
                    value = sample.getDbm();
                    break;
                case ASU:
                    value = sample.getAsu();
                    break;
            }
            MarkerOptions markerOptions = new MarkerOptions().position(
                    new LatLng(sample.getLatitude(), sample.getLongtitude()));
            Bitmap indicator = SignalIndicator.getIndicator(this, min, max, value);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(indicator));
            mAMap.addMarker(markerOptions);
        }
    }

    private void cameraUpdate(List<SignalStrength> samples) {
        SignalStrength sample = samples.get(0);
        if (null != sample) {
            LatLng latLng = new LatLng(sample.getLatitude(), sample.getLongtitude());
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 13);
            mAMap.moveCamera(cu);
        }
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
        ((GradientDrawable)drawable).setColor(Color.BLACK);
        Bitmap bitmap = Bitmap.createBitmap(Config.MARKER_DIAMETER, Config.MARKER_DIAMETER, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

