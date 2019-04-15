package com.example.campcellsigmap.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import com.example.campcellsigmap.entity.MeasuredSignal;

import java.util.List;

public class SignalMeasureHelper {



    public static MeasuredSignal measureSigStrength(Context context, TelephonyManager telephonyManager) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            for (CellInfo info : cellInfoList) {
                if (info.isRegistered()) {
                    if (info instanceof CellInfoGsm) {
                        CellSignalStrengthGsm sigStrengthGsm = ((CellInfoGsm) info).getCellSignalStrength();
                        MeasuredSignal signal = new MeasuredSignal(sigStrengthGsm.getLevel(), sigStrengthGsm.getDbm(), sigStrengthGsm.getAsuLevel());
                        return signal;
                    } else if (info instanceof CellInfoCdma) {
                        CellSignalStrengthCdma sigStrengthCdma = ((CellInfoCdma) info).getCellSignalStrength();
                        MeasuredSignal signal = new MeasuredSignal(sigStrengthCdma.getLevel(), sigStrengthCdma.getDbm(), sigStrengthCdma.getAsuLevel());
                        return signal;
                    } else if (info instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma sigStrengthWcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                        MeasuredSignal signal = new MeasuredSignal(sigStrengthWcdma.getLevel(), sigStrengthWcdma.getDbm(), sigStrengthWcdma.getAsuLevel());
                        return signal;
                    } else if (info instanceof CellInfoLte) {
                        CellSignalStrengthLte sigStrengthLte = ((CellInfoLte) info).getCellSignalStrength();
                        MeasuredSignal signal = new MeasuredSignal(sigStrengthLte.getLevel(), sigStrengthLte.getDbm(), sigStrengthLte.getAsuLevel());
                        return signal;
                    }
                }
            }
        }
        return new MeasuredSignal();
    }
}
