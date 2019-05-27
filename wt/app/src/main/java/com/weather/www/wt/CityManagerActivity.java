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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weather.www.wt.gson.Weather;
import com.weather.www.wt.util.Utility;

public class CityManagerActivity extends AppCompatActivity {
    private DrawerLayout city_drawerLayout;
    private LinearLayout cityLayout;
    private Button addButton;
    private Button backButton;

    private Button cancel_btn;
    private Button done_btn;

    private RelativeLayout title1;
    private RelativeLayout title2;

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
        backButton = (Button)findViewById(R.id.back_button);
        cancel_btn = (Button)findViewById(R.id.cancel_btn);
        done_btn = (Button)findViewById(R.id.done_btn);
        title1 = (RelativeLayout)findViewById(R.id.title1);
        title2 = (RelativeLayout)findViewById(R.id.title2);

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                city_drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //返回上一个界面
                finish();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                title1.setVisibility(View.VISIBLE);
                title2.setVisibility(View.GONE);
            }
        });
        showCity(sa);
    }

    /**
     * 展示城市列表
     */
    public void showCity(String[] sa){
        cityLayout.removeAllViews();

        for (String s :sa){
            View view = LayoutInflater.from(this).inflate(R.layout.city_item,cityLayout,false);
            TextView cityName = (TextView)view.findViewById(R.id.city_name);
            final TextView degree = (TextView)view.findViewById(R.id.city_degree);
            TextView txt = (TextView)view.findViewById(R.id.city_txt);
            final ImageView delete = (ImageView)view.findViewById(R.id.delete_item);

            //获取数据
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Weather weather = Utility.WeatherResponse(preferences.getString(s,null));
            cityName.setText(weather.basic.cityName);
            degree.setText(weather.now.temperture+"℃");
            txt.setText(weather.now.cond_txt);

            delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    System.out.println("长按执行成功");
                    title1.setVisibility(View.GONE);
                    title2.setVisibility(View.VISIBLE);
                    return true;
                }
            });

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println("跳到WeatherActivity相应界面");
                }
            });
            cityLayout.addView(view);
        }
    }
}
