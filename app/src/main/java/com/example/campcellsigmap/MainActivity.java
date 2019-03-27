package com.example.campcellsigmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button mBeginBtn;
    TextView mBulletinTv;
    TelephonyManager mTeleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initView();
        mTeleManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        Log.d("MainActivity", "onCreate");
    }

    private void initView() {
        mBeginBtn = findViewById(R.id.begin);
        mBulletinTv = findViewById(R.id.bulletin);

        mBeginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "onClick:");
                testCell();
            }
        });
    }

    private void testCell() {
        StringBuilder infoS = new StringBuilder();
        int type = mTeleManager.getNetworkType();
        String typeName = "";
        switch (type){
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                typeName = "Unknown";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                typeName = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                typeName = "GSM";

        }
        infoS.append(type).append(":").append(typeName).append("\n");

        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<CellInfo> cellInfoList = mTeleManager.getAllCellInfo();
            infoS.append(cellInfoList.size());
            for (CellInfo info : cellInfoList) {
                if (info instanceof CellInfoLte) {
                    CellSignalStrengthLte infoLte = ((CellInfoLte) info).getCellSignalStrength();
                    infoS.append("Dbm:").append(infoLte.getDbm()).append("\n");
                }
            }
        }

        mBulletinTv.setText(infoS.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
