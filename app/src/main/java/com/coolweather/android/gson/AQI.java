package com.coolweather.android.gson;

/**
 * Created by ts on 17-11-29.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
