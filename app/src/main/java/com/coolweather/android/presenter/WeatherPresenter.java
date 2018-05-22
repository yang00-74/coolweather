package com.coolweather.android.presenter;

import com.coolweather.android.gson.Weather;

public interface WeatherPresenter {
    void showWeather(Weather weather);
    void loadPic();
    void setRefresh(boolean flag);
    void requestWeather(String weatherId);
    void init();
}
