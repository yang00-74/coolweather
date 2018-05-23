package com.coolweather.android.util;

import android.widget.Toast;

import org.litepal.LitePalApplication;

public class Utils {
    public static void alertToast(String message) {
        Toast.makeText(LitePalApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
