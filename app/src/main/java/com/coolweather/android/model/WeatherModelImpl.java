package com.coolweather.android.model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import com.coolweather.android.util.Utils;
import com.google.gson.Gson;

import org.litepal.LitePalApplication;

import java.io.IOException;
import java.net.InterfaceAddress;

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
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorStatus();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(LitePalApplication.getContext()).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                listener.onRequestPicSuccess();
            }
        });
    }

    public void requestWeatherInfo(String weatherId) {

        Log.d("asd_entry_model_request", weatherId.toString());
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=cdb7dc83f26141d9b83e15e6e92acb72";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorStatus();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Weather weather = Utility.handleWeatherRespone(responseText);
                if (weather == null || !"ok".equals(weather.status)) {
                    listener.onErrorStatus();
                    return;
                }
                Log.d("asd_model_response", responseText);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(LitePalApplication.getContext())
                        .edit();
                editor.putString("weather", responseText);
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
