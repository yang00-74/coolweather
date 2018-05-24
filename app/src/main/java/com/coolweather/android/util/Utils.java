package com.coolweather.android.util;

import android.widget.Toast;

import org.litepal.LitePalApplication;

public class Utils {
    public static void alertToast(String message) {
        Toast.makeText(LitePalApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static String getPicPath() {
        return String.valueOf("http://guolin.tech/api/bing_pic");
    }

    public static String getWeatherPath(String weatherId) {
        return String.valueOf("http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=cdb7dc83f26141d9b83e15e6e92acb72");
    }
}
