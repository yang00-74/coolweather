package com.coolweather.android.presenter;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.view.WeatherView;

public class WeatherPresenterImpl implements WeatherPresenter {

    private WeatherView weatherView;

    public WeatherPresenterImpl(WeatherView weatherView) {
        this.weatherView = weatherView;
    }


    @Override
    public void showWeather(Weather weather) {
        weatherView.showWeatherInfo(weather);
    }

    @Override
    public void loadPic() {
        weatherView.loadBingPic();
    }

    @Override
    public void setRefresh(boolean flag) {
        weatherView.setSwipRefresh(flag);
    }

    @Override
    public void requestWeather(String weatherId) {
        weatherView.requestWeatherInfo(weatherId);
    }

    @Override
    public void init() {
        weatherView.initView();
    }
}
