package com.coolweather.android.view;


import com.coolweather.android.gson.Weather;

public interface WeatherView {

    void showWeatherInfo(Weather weather);

    void loadBingPic();

    void setSwipRefresh(boolean flag);

    void initView();

    void handlerError();
}
