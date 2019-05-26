package com.weather.www.wt;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.weather.www.wt.util.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<WeatherFragment> lists;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        lists = new ArrayList<WeatherFragment>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] sa = preferences.getString("weather_id",null).split(" ");
        for(int i=0;i<sa.length;i++){
            WeatherFragment t = new WeatherFragment();
            t.setWeatherId(sa[i]);
            System.out.println("WeatherId:"+sa[i]);
            lists.add(t);
        }

        FragmentManager fm = getSupportFragmentManager();
        mAdapter = new ViewPagerAdapter(fm,lists);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
    }

}
