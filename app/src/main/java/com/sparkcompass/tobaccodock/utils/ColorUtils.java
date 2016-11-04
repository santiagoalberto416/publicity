package com.sparkcompass.tobaccodock.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class ColorUtils {

    public static String getColor(Context context, int colorId){
        int color = ContextCompat.getColor(context, colorId);
        return "#" + Integer.toHexString(color);
    }
}
