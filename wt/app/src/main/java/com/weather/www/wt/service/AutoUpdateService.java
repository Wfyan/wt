package com.weather.www.wt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){

        return super.onStartCommand(intent,flags,startId);
    }
    /**
     * 更新天气信息
     */
    private void updataWeather(){

    }

    /**
     * 更新每日一图
     */
    private void updateBingPic(){

    }
}
