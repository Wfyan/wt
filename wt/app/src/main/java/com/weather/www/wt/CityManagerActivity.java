package com.weather.www.wt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.weather.www.wt.gson.Weather;
import com.weather.www.wt.util.CityAdapter;
import com.weather.www.wt.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class CityManagerActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private DrawerLayout city_drawerLayout;
    private ListView cityLayout;
    private Button addButton;
    private Button backButton;

    private Button cancel_btn;
    private Button done_btn;

    private RelativeLayout title1;
    private RelativeLayout title2;

    private CityAdapter adapter;
    private List<Weather> lists = new ArrayList<>();
    private boolean isShowDelete = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citymanager);
        //获取已经选择的城市
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] sa = preferences.getString("weather_id",null).split(" ");

        //初始化控件
        city_drawerLayout = (DrawerLayout)findViewById(R.id.city_drawer_layout);
        cityLayout = (ListView)findViewById(R.id.city_list_layout);
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
                Intent intent = new Intent(CityManagerActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
        });

        showCity(sa);
        //设置适配器
        adapter = new CityAdapter(CityManagerActivity.this,R.layout.city_item,lists);

        cancel_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                title1.setVisibility(View.VISIBLE);
                title2.setVisibility(View.GONE);
                isShowDelete = false;
                adapter.setShowDelete(isShowDelete);
            }
        });
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title1.setVisibility(View.VISIBLE);
                title2.setVisibility(View.GONE);
                isShowDelete = false;
                delete(adapter.getPositionList().get(0));
                adapter.setShowDelete(isShowDelete);
            }
        });
        cityLayout.setAdapter(adapter);

        cityLayout.setOnItemLongClickListener(this);

        cityLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到相应的天气信息页面
                Intent intent = new Intent(CityManagerActivity.this,WeatherActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    /**
     * 展示城市列表
     */
    public void showCity(String[] sa){
        for (String s :sa){
            //获取数据
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Weather weather = Utility.WeatherResponse(preferences.getString(s,null));
            lists.add(weather);
        }
    }

    /**
     * 删除
     */
    public void delete(int position){
       String id = lists.get(position).basic.weatherId;
       //移除天气数据
       SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
       editor.remove(id);
       lists.remove(position);
       //重新记录天气Id
        String s = lists.get(0).basic.weatherId;
        for(int i=0;i< lists.size();i++){
            if(i==0){
            }else {
                s = s+" "+lists.get(i).basic.weatherId;
            }
        }
        System.out.println(s);
        editor.putString("weather_id",s);
        editor.commit();
       adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (isShowDelete) {
            isShowDelete = false;
        } else {
            isShowDelete = true;
            title1.setVisibility(View.GONE);
            title2.setVisibility(View.VISIBLE);
        }
        adapter.setShowDelete(isShowDelete);//setIsShowDelete()方法用于传递isShowDelete值
        return true;
    }
}
