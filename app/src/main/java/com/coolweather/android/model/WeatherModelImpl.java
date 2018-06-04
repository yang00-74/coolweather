package com.coolweather.android.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpRequestUtil;
import com.coolweather.android.util.HandleHttpResponseUtils;
import com.coolweather.android.util.Utils;

import org.litepal.LitePalApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherModelImpl implements WeatherModel {

    private onRequestListener listener;

    public void setOnRequestListener(onRequestListener listener) {
        this.listener = listener;
    }

    @Override
    public void requestBingPic() {
        String requestBingPic = Utils.getPicPath();
        HttpRequestUtil.sendHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorStatus();
            }

            @Override
            public void onResponse(Call call, Response httpResponse) throws IOException {

                final String bingPic = httpResponse.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(LitePalApplication.getContext()).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                listener.onRequestPicSuccess();
            }
        });
    }

    public void requestWeatherInfo(String weatherId) {

        Log.d("asd_entry_model_request", weatherId);
        String weatherUrl = Utils.getWeatherPath(weatherId);
        HttpRequestUtil.sendHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorStatus();
            }

            @Override
            public void onResponse(Call call, Response httpResponse) throws IOException {
                String responseContent = httpResponse.body().string();
                Weather weather = HandleHttpResponseUtils.handleResponeOfWeather(responseContent);
                if (weather == null || !"ok".equals(weather.status)) {
                    listener.onErrorStatus();
                    return;
                }
                Log.d("asd_model_response", responseContent);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(LitePalApplication.getContext())
                        .edit();
                editor.putString("weather", responseContent);
                editor.apply();
                listener.onRequestWeatherSuccess(weather);
            }
        });
    }

    public interface onRequestListener {
        void onRequestWeatherSuccess(Weather weather);

        void onRequestPicSuccess();

        void onErrorStatus();
    }
}
