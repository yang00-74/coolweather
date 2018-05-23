package com.coolweather.android.presenter;

import com.coolweather.android.gson.Weather;

public interface WeatherPresenter {

    void loadPic();

    void requestPic();

    void setRefresh(boolean flag);

    void requestWeather(String weatherId);

    void showWeather(Weather weather);

    void init();
}
