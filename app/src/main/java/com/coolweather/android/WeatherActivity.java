package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.model.WeatherModelImpl;
import com.coolweather.android.presenter.WeatherPresenter;
import com.coolweather.android.presenter.WeatherPresenterImpl;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.Utility;
import com.coolweather.android.util.Utils;
import com.coolweather.android.view.WeatherView;

public class WeatherActivity extends AppCompatActivity implements WeatherView {
    private ScrollView weatherLayout;
    private LinearLayout forecastLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;
    //The weather presenter
    private WeatherPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        presenter = new WeatherPresenterImpl(this, new WeatherModelImpl());
        presenter.init();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = preferences.getString("weather", null);
        String bingPic = preferences.getString("bing_pic", null);

        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            presenter.requestPic();
        }
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherRespone(weatherString);
            if (weather == null || !"ok".equals(weather.status)) {
                weatherLayout.setVisibility(View.INVISIBLE);
                return;
            }
            mWeatherId = weather.basic.weatherId;
            presenter.showWeather(weather);
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            presenter.requestWeather(mWeatherId);
        }
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.requestWeather(mWeatherId);
                presenter.requestPic();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void handlerError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.alertToast("获取天气失败");
                presenter.setRefresh(false);
            }
        });
    }

    @Override
    public void loadBingPic() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String bingPic = preferences.getString("bing_pic", null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
    }

    @Override
    public void setSwipRefresh(boolean flag) {
        swipeRefreshLayout.setRefreshing(flag);
    }

    @Override
    public void showWeatherInfo(Weather weather1) {
        final Weather weather = weather1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String cityName = weather.basic.cityName;
                String updateTime = weather.basic.update.updateTime.split(" ")[1];
                String degree = weather.now.temperature + "℃";
                String weatherInfo = weather.now.more.info;

                titleCity.setText(cityName);
                titleUpdateTime.setText(updateTime);
                degreeText.setText(degree);
                weatherInfoText.setText(weatherInfo);
                forecastLayout.removeAllViews();

                for (Forecast forecast : weather.forecastList) {
                    View view = LayoutInflater.from(WeatherActivity.this)
                            .inflate(R.layout.forecast_item, forecastLayout, false);

                    TextView dateText = view.findViewById(R.id.date_text);
                    TextView infoText = view.findViewById(R.id.info_text);
                    TextView maxText = view.findViewById(R.id.max_text);
                    TextView minText = view.findViewById(R.id.min_text);

                    dateText.setText(forecast.date);
                    infoText.setText(forecast.more.info);
                    maxText.setText(forecast.temperature.max);
                    minText.setText(forecast.temperature.min);

                    forecastLayout.addView(view);
                }
                if (weather.aqi != null) {
                    aqiText.setText(weather.aqi.city.aqi);
                    pm25Text.setText(weather.aqi.city.pm25);
                }

                String comfort = "舒适度:" + weather.suggestion.comfort.info;
                String carWash = "洗车指数:" + weather.suggestion.crashWash.info;
                String sport = "运动建议:" + weather.suggestion.sport.info;

                comfortText.setText(comfort);
                carWashText.setText(carWash);
                sportText.setText(sport);

                weatherLayout.setVisibility(View.VISIBLE);
                Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                startService(intent);
                presenter.setRefresh(false);
            }
        });
    }

    // This method is the entry of ChooseAcreFragment
    public void requestWeather(String weatherId) {
        mWeatherId = weatherId;
        presenter.requestWeather(weatherId);
    }
}
