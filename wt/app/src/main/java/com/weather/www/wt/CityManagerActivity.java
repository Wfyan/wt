package com.weather.www.wt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weather.www.wt.gson.Weather;
import com.weather.www.wt.util.Utility;

public class CityManagerActivity extends AppCompatActivity {
    private DrawerLayout city_drawerLayout;
    private LinearLayout cityLayout;
    private Button addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citymanager);
        //获取已经选择的城市
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] sa = preferences.getString("weather_id",null).split(" ");

        //初始化控件
        city_drawerLayout = (DrawerLayout)findViewById(R.id.city_drawer_layout);
        cityLayout = (LinearLayout)findViewById(R.id.city_list_layout);
        addButton = (Button)findViewById(R.id.add_button);

        showCity(sa);

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                city_drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 展示城市列表
     */
    public void showCity(String[] sa){
        cityLayout.removeAllViews();
        //重新绘制多天未来天气
        for (String s :sa){
            View view = LayoutInflater.from(this).inflate(R.layout.city_item,cityLayout,false);
            TextView cityName = (TextView)view.findViewById(R.id.city_name);
            TextView degree = (TextView)view.findViewById(R.id.city_degree);
            TextView txt = (TextView)view.findViewById(R.id.city_txt);

            //获取数据
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Weather weather = Utility.WeatherResponse(preferences.getString(s,null));
            cityName.setText(weather.basic.cityName);
            degree.setText(weather.now.temperture+"℃");
            txt.setText(weather.now.cond_txt);
            cityLayout.addView(view);
        }
    }
}
