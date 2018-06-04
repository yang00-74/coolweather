package com.coolweather.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpRequestUtil;
import com.coolweather.android.util.HandleHttpResponseUtils;
import com.coolweather.android.util.Utils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 */

public class SelectAreaFragment extends Fragment {

    private ProgressDialog progressDialog;
    private TextView tv_Title;
    private Button bt_Back;
    private ListView areaListView;
    private ArrayAdapter<String> areaAdapter;
    private List<String> areaList = new ArrayList<>();

    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;

    private Province selectedProvince;
    private City selectedCity;

    private Utils.SelectedLevel currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup root,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_area, root, false);

        tv_Title = view.findViewById(R.id.tv_title);
        bt_Back = view.findViewById(R.id.bt_back);
        areaListView = view.findViewById(R.id.list_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            areaAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, areaList);
        }
        areaListView.setAdapter(areaAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        areaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int clickedPosition, long id) {
                if (currentLevel == Utils.SelectedLevel.PROVINCE) {
                    selectedProvince = provinces.get(clickedPosition);
                    findCities();
                } else if (currentLevel == Utils.SelectedLevel.CITY) {
                    selectedCity = cities.get(clickedPosition);
                    findCounties();
                } else if (currentLevel == Utils.SelectedLevel.COUNTY) {
                    String weatherCode = counties.get(clickedPosition).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent it = new Intent(getActivity(), WeatherActivity.class);
                        it.putExtra("weather_id", weatherCode);
                        startActivity(it);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        Log.d("asd_requested_weather", weatherCode);
                        activity.requestWeather(weatherCode);
                    }
                }
            }
        });
        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == Utils.SelectedLevel.COUNTY) {
                    findCities();
                } else if (currentLevel == Utils.SelectedLevel.CITY) {
                    findProvinces();
                }
            }
        });
        findProvinces();
    }

    private void findCounties() {
        tv_Title.setText(selectedCity.getCityName());
        bt_Back.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid=?",
                String.valueOf(selectedCity.getId())).find(County.class);

        if (counties.size() > 0) {
            areaList.clear();
            for (County county : counties) {
                areaList.add(county.getCountyName());
            }
            areaAdapter.notifyDataSetChanged();
            areaListView.setSelection(0);
            currentLevel = Utils.SelectedLevel.COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = Utils.getServerPath() + provinceCode + "/" + cityCode;
            findFromServer(address, Utils.SelectedLevel.COUNTY);
        }
    }

    private void findCities() {
        tv_Title.setText(selectedProvince.getProvinceName());
        bt_Back.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceid=?",
                String.valueOf(selectedProvince.getId())).find(City.class);

        if (cities.size() > 0) {
            areaList.clear();
            for (City city : cities) {
                areaList.add(city.getCityName());
            }
            areaAdapter.notifyDataSetChanged();
            areaListView.setSelection(0);
            currentLevel = Utils.SelectedLevel.CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = Utils.getServerPath() + provinceCode;
            findFromServer(address, Utils.SelectedLevel.CITY);
        }
    }

    private void findProvinces() {
        tv_Title.setText("中国");
        bt_Back.setVisibility(View.GONE);
        provinces = DataSupport.findAll(Province.class);

        if (provinces.size() > 0) {
            areaList.clear();
            for (Province province : provinces) {
                areaList.add(province.getProvinceName());
            }
            areaAdapter.notifyDataSetChanged();
            areaListView.setSelection(0);
            currentLevel = Utils.SelectedLevel.PROVINCE;
        } else {
            String address = Utils.getServerPath();
            findFromServer(address, Utils.SelectedLevel.PROVINCE);
        }
    }

    private void findFromServer(String address, final Utils.SelectedLevel type) {
        showProgressDialog();

        HttpRequestUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Toast.makeText(getContext(), "加载失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (Utils.SelectedLevel.PROVINCE == type) {
                    result = HandleHttpResponseUtils.handleResponseOfProvince(responseText);
                } else if (Utils.SelectedLevel.CITY == type) {
                    result = HandleHttpResponseUtils.handleResponseOfCity(responseText, selectedProvince.getId());
                } else if (Utils.SelectedLevel.COUNTY == type) {
                    result = HandleHttpResponseUtils.handleResponseOfCounty(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (Utils.SelectedLevel.PROVINCE == type) {
                                findProvinces();
                            } else if (Utils.SelectedLevel.CITY == type) {
                                findCities();
                            } else {
                                findCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
