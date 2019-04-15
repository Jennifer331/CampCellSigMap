package com.example.campcellsigmap.utils;

import android.content.Context;

import com.example.campcellsigmap.entity.SignalStrength;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CSVHelper {
    private static final String HEADERS = "Date,Latitude,Longitude,Level,Dbm,Asu\n";
    private static final String DATE_PATTERN = "EEE MMM dd HH:mm:ss z yyyy";
    private static final String CSV_SUFFIX = ".csv";

    public static void saveToFile(Context context, List<SignalStrength> samples, String folder, String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADERS);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        for (SignalStrength sample : samples) {
            append(sb, sample, formatter);
            sb.append("\n");
        }

        try {
            File file = createInternalFile(context, folder, filename);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(Context context, String folderName, String fileName) {
        File folder = new File(context.getFilesDir(), folderName);
        if (folder.exists()) {
            File file = new File(folder, fileName + CSV_SUFFIX);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static List<SignalStrength> readFromFolder(Context context, String folderName) {
        List<SignalStrength> samples = new LinkedList<>();
        File folder = new File(context.getFilesDir(), folderName);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                samples.addAll(readFromFile(file));
            }
        }
        return samples;
    }

    public static List<SignalStrength> readFromFile(Context context, String folder, String filename) {
        File file = openInternalFile(context, folder, filename);
        if (null != file) {
            return readFromFile(file);
        }
        return new LinkedList<>();
    }

    private static List<SignalStrength> readFromFile(File file) {
        List<SignalStrength> samples = new LinkedList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();//read column names
            String line = br.readLine();
            while (null != line) {
                String[] tokens = line.split(",");
                double latitude = Double.valueOf(tokens[1]);
                double longitude = Double.valueOf(tokens[2]);
                int level = Integer.valueOf(tokens[3]);
                int dbm = Integer.valueOf(tokens[4]);
                int asu = Integer.valueOf(tokens[5]);
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.US);
                Date date = formatter.parse(tokens[0]);
                SignalStrength sample = new SignalStrength(latitude, longitude, level, dbm, asu, date);
                samples.add(sample);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return samples;
    }

    private static File createInternalFile(Context context, String folderName, String fileName) {
        File folder = new File(context.getFilesDir(), folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, fileName + CSV_SUFFIX);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static File openInternalFile(Context context, String folderName, String fileName) {
        File folder = new File(context.getFilesDir(), folderName);
        if (folder.exists()) {
            File file = new File(folder, fileName + CSV_SUFFIX);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private static void append(StringBuilder sb, SignalStrength sample, DateFormat formatter) {
        String date = formatter.format(sample.getDate());
        sb
                .append(date).append(",")
                .append(sample.getLatitude()).append(",")
                .append(sample.getLongtitude()).append(",")
                .append(sample.getLevel()).append(",")
                .append(sample.getDbm()).append(",")
                .append(sample.getAsu())
        ;
    }

    public static List<SignalStrength> readFromFile(String filename) {
        List<SignalStrength> samples = new LinkedList<>();
        return samples;
    }

    public static List<String> getFiles(Context context, String foldername) {
        List<String> result = new LinkedList<>();
        File folder = new File(context.getFilesDir(), foldername);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(CSV_SUFFIX)) {
                    result.add(name.substring(0, name.length() - CSV_SUFFIX.length()));
                }
            }
        }
        return result;
    }
}
