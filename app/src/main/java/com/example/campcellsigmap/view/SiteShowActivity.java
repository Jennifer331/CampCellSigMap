package com.example.campcellsigmap.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.campcellsigmap.R;
import com.example.campcellsigmap.entity.SignalStrength;
import com.example.campcellsigmap.utils.CSVHelper;
import com.example.campcellsigmap.utils.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.LinkedList;
import java.util.List;

import static com.example.campcellsigmap.utils.Config.FILENAME;
import static com.example.campcellsigmap.utils.Config.SITE_FOLDER;

public class SiteShowActivity extends Activity {

    String mFilename;
    LineChart mChart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_show);

        mFilename = getIntent().getStringExtra(FILENAME);
        mChart = findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Util.flippedMillisecondToString((int) value);
            }
        });

        List<SignalStrength> samples = CSVHelper.readFromFile(this, SITE_FOLDER, mFilename);
        List<Entry> entries = new LinkedList<>();
        for (SignalStrength sample : samples) {
            Entry entry = new Entry(Util.flippedMillisecond(sample.getDate()), sample.getDbm());
            entries.add(entry);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.GRAY);
        dataSet.setValueTextColor(Color.GRAY);

        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);
        mChart.invalidate();
    }


}
