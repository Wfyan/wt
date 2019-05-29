package com.weather.www.wt.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.weather.www.wt.gson.BingImage;
import com.weather.www.wt.gson.Weather;
import com.weather.www.wt.util.HttpUtil;
import com.weather.www.wt.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        updataWeather();
        updateBingPic();
        //设置后台定时任务
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 1*60*1000; //8小时
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        System.out.println(triggerAtTime+"更新");
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0, i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }
    /**
     * 更新天气信息
     */
    private void updataWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = preferences.getString("weather_id",null).split(" ")[0];
        if (weather != null){
            final String weatherId = weather;
            String url = "https://free-api.heweather.net/s6/weather?location="+weatherId+"&key=d2ae781d61744d65a2ef2156eef2cb64";
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.WeatherResponse(responseText);
                    if(weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString(weatherId,responseText);
                        System.out.println(responseText);
                        editor.apply();
                    }
                }
            });

        }
    }

    /**
     * 更新每日一图
     */
    private void updateBingPic(){
        String BingUrl = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";
        HttpUtil.sendOkHttpRequest(BingUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                BingImage bingImage = Utility.BingImageResponse(responseText);
                if (bingImage != null){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    String bingPic = "https://cn.bing.com/"+bingImage.getUrl();
                    editor.putString("bing_pic",bingPic);
                    editor.apply();
                }
            }
        });
    }
}
