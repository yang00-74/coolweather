package com.coolweather.android.model;

public interface WeatherModel {
    void requestWeatherInfo(final String weatherId);

    void requestBingPic();
}
