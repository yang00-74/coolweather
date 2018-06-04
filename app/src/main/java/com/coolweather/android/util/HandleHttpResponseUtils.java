package com.coolweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 */

public class HandleHttpResponseUtils {
    /**
     * 解析服务器返回的数据
     */
    public static Weather handleResponeOfWeather(String httpResponse) {
        if (TextUtils.isEmpty(httpResponse)) {
            return null;
        }
        try{
            JSONObject weatherJson = new JSONObject(httpResponse);
            JSONArray weatherArray = weatherJson.getJSONArray("HeWeather");
            String weatherString = weatherArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherString, Weather.class);
        }catch (Exception e){
            Log.e("asd_exception", e.toString());
        }
        return null;
    }

    public static boolean handleResponseOfProvince(String httpResponse) {
        if (!TextUtils.isEmpty(httpResponse)) {
            try {
                JSONArray provincesArray = new JSONArray(httpResponse);
                for (int i = 0; i < provincesArray.length(); i++) {
                    JSONObject jsonProvince = provincesArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonProvince.getString("name"));
                    province.setProvinceCode(jsonProvince.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                Log.e("asd_exception", e.toString());
            }
        }
        return false;
    }

    public static boolean handleResponseOfCity(String httpResponse, int pId) {
        if (!TextUtils.isEmpty(httpResponse)) {
            try {
                JSONArray citesArray = new JSONArray(httpResponse);
                for (int i = 0; i < citesArray.length(); i++) {
                    JSONObject jsonCity = citesArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonCity.getString("name"));
                    city.setCityCode(jsonCity.getInt("id"));
                    city.setProvinceId(pId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                Log.e("asd_exception", e.toString());
            }
        }
        return false;
    }

    public static boolean handleResponseOfCounty(String httpResponse, int cId) {
        if (!TextUtils.isEmpty(httpResponse)) {
            try {
                JSONArray countiesArray = new JSONArray(httpResponse);
                for (int i = 0; i < countiesArray.length(); i++) {
                    JSONObject jsonCounty = countiesArray.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(jsonCounty.getString("name"));
                    county.setWeatherId(jsonCounty.getString("weather_id"));
                    county.setCityId(cId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                Log.e("asd_exception", e.toString());
            }
        }
        return false;
    }

}
