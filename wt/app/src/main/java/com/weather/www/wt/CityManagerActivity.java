package com.weather.www.wt;

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
        //设置适配器
        adapter = new CityAdapter(CityManagerActivity.this,R.layout.city_item,lists);

        cityLayout.setAdapter(adapter);
        cityLayout.setOnItemLongClickListener(this);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (isShowDelete) {
            isShowDelete = false;
        } else {
            isShowDelete = true;
            adapter.setShowDelete(isShowDelete);
            title1.setVisibility(View.GONE);
            title2.setVisibility(View.VISIBLE);
            cityLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //delete(position);//删除选中项
                    System.out.println(position);
                    adapter = new CityAdapter(CityManagerActivity.this,R.layout.city_item,lists); //重新绑定一次adapter
                    cityLayout.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        adapter.setShowDelete(isShowDelete);//setIsShowDelete()方法用于传递isShowDelete值
        return true;
    }
}
