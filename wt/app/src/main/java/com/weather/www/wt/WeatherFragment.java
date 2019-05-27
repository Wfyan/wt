package com.weather.www.wt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weather.www.wt.gson.BingImage;
import com.weather.www.wt.gson.Forecast;
import com.weather.www.wt.gson.Suggestion;
import com.weather.www.wt.gson.Weather;
import com.weather.www.wt.util.HttpUtil;
import com.weather.www.wt.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherFragment extends Fragment {
    private String weatherId;

    public SwipeRefreshLayout swipeRefreshLayout;
    private ImageView bingPicImage;
    private Button navButton;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private LinearLayout suggestionLayout;
    private TextView txt;

    public WeatherFragment(){

    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather, container, false);

        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //初始化各组件
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        titleCity = (TextView)view.findViewById(R.id.title_city);
        titleUpdateTime = (TextView)view.findViewById(R.id.title_update_time);
        degreeText = (TextView)view.findViewById(R.id.degree_text);
        weatherInfoText = (TextView)view.findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        aqiText = (TextView)view.findViewById(R.id.aqi_text);
        pm25Text = (TextView)view.findViewById(R.id.pm25_text);
        suggestionLayout = (LinearLayout)view.findViewById(R.id.suggestion_layout);
        //bing图片控件
        bingPicImage = (ImageView)view.findViewById(R.id.bing_pic);

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //导航
        navButton = (Button)view.findViewById(R.id.nav_button);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CityManagerActivity.class);
                startActivityForResult(intent,3);
            }
        });

        //数据存储访问
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherString = prefs.getString(weatherId,null);


        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImage);
        }else {
            loadBingPic();
        }

        if(weatherString!=null){//如果有缓存，则直接拿来解析
            Weather weather = Utility.WeatherResponse(weatherString);
            //展示数据
            showWeatherInfo(weather);
        }else { //无缓存时去服务器查询
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        //复写下拉刷新功能
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }

    /**
     * 根据天气id获取天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl ="https://free-api.heweather.net/s6/weather?location="+weatherId+"&key=d2ae781d61744d65a2ef2156eef2cb64";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"连接失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                System.out.println("key is ok");
                final Weather weather = Utility.WeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            //缓存数据
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString(weatherId,responseText);
                            //从内部类访问本地变量，需要被声明为最终类型
                            editor.apply();
                            showWeatherInfo(weather);
                            Toast.makeText(getActivity(), "获取天气信息成功",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理JSON数据展示Weather实体类的数据
     */
    public void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime;
        String degree = weather.now.temperture+"℃";
        String weatherInfo = weather.now.cond_txt;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        //重新绘制多天未来天气
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.cond_txt_d);
            maxText.setText(forecast.tmp_max);
            minText.setText(forecast.tmp_min);
            forecastLayout.addView(view);
        }
        //空气指数
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        suggestionLayout.removeAllViews();
        //重新绘制
        for (Suggestion suggestion : weather.suggestionList){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.suggestion_item,suggestionLayout,false);
            TextView dataText = (TextView)view.findViewById(R.id.date_text);
            dataText.setText(suggestion.txt);
            suggestionLayout.addView(view);
        }

        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取Bing每日一图
     */
    private void loadBingPic(){
        String BingUrl = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";
        HttpUtil.sendOkHttpRequest(BingUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final BingImage bingImage = Utility.BingImageResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bingImage != null){
                            //缓存数据
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            String bingPic = "https://cn.bing.com/"+bingImage.getUrl();
                            editor.putString("bing_pic",bingPic);
                            editor.apply();
                            Glide.with(WeatherFragment.this).load(bingPic).into(bingPicImage);
                        }else {
                            Toast.makeText(getActivity(),"加载图片失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
