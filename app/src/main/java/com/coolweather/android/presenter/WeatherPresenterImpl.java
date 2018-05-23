package com.coolweather.android.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.model.WeatherModel;
import com.coolweather.android.model.WeatherModelImpl;
import com.coolweather.android.util.Utils;
import com.coolweather.android.view.WeatherView;

public class WeatherPresenterImpl implements WeatherPresenter {

    private WeatherView weatherView;
    private WeatherModelImpl weatherModel;
    private String mWeatherId;

    public WeatherPresenterImpl(final WeatherView weatherView, WeatherModelImpl weatherModel) {
        this.weatherView = weatherView;
        this.weatherModel = weatherModel;
        this.weatherModel.setOnRequestListener(new WeatherModelImpl.onRequestListener() {
            @Override
            public void onRequestWeatherSuccess(Weather weather) {
                showWeather(weather);
            }

            @Override
            public void onRequestPicSuccess() {
                loadPic();
            }

            @Override
            public void onErrorStatus() {
                handlerErrorUI();
            }
        });
    }

    @Override
    public void requestPic() {
        weatherModel.requestBingPic();
    }

    @Override
    public void loadPic() {
        weatherView.loadBingPic();
    }

    public void handlerErrorUI() {
        weatherView.handlerError();
    }

    @Override
    public void setRefresh(boolean flag) {
        weatherView.setSwipRefresh(flag);
    }

    @Override
    public void requestWeather(String weatherId) {
        mWeatherId = weatherId;
        if (!TextUtils.isEmpty(weatherId)) {
            Log.d("asd_entry_presenter", weatherId);
            weatherModel.requestWeatherInfo(mWeatherId);
        } else {
            Utils.alertToast("获取天气失败");
            setRefresh(false);
        }
    }

    @Override
    public void showWeather(Weather weather) {
        weatherView.showWeatherInfo(weather);
    }

    @Override
    public void init() {
        weatherView.initView();
    }
}
