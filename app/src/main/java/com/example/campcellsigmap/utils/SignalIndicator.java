package com.example.campcellsigmap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.example.campcellsigmap.R;

public class SignalIndicator {
    public static Bitmap getIndicator(Context context, int min, int max, int value) {
        float ratio = ((value - min) * (float)1.0) / (max - min);
        float minHue = 120;//green
        float maxHue = 0;//red
        float hue = ratio * minHue;
        int color = Color.HSVToColor(255, new float[]{hue, 1, 0.5f});

        Drawable drawable = context.getDrawable(R.drawable.strength_indicator0);
        ((GradientDrawable)drawable).setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(Config.MARKER_DIAMETER, Config.MARKER_DIAMETER, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
