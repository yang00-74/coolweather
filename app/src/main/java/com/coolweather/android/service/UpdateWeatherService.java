package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coolweather.android.R;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpRequestUtil;
import com.coolweather.android.util.HandleHttpResponseUtils;
import com.coolweather.android.util.Utils;

import org.litepal.LitePalApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateWeatherService extends Service {
    public UpdateWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingpic();
        Log.d("asd_entry_service","onStartCommand");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 5 * 60 * 1000;
        long triggerTime = SystemClock.elapsedRealtime() + time;
        Intent intent1 = new Intent(this, UpdateWeatherService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, intent1, 0);
        assert alarmManager != null;
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            Weather weather = HandleHttpResponseUtils.handleResponeOfWeather(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = Utils.getWeatherPath(weatherId);
            HttpRequestUtil.sendHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String httpResponse = response.body().string();
                    Weather weather = HandleHttpResponseUtils.handleResponeOfWeather(httpResponse);

                    if (null != weather && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(LitePalApplication.getContext()).edit();
                        editor.putString("weather", httpResponse);
                        editor.apply();
                    }
                }

            });
        }

    }

    private void updateBingpic() {
        String requestBingPic = Utils.getPicPath();
        HttpRequestUtil.sendHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(LitePalApplication.getContext()).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }
}
