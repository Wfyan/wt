package com.weather.www.wt.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.weather.www.wt.WeatherFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private List<WeatherFragment> lists;

    public ViewPagerAdapter(FragmentManager fm,List<WeatherFragment> lists){
        super(fm);
        this.fm = fm;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Fragment getItem(int i) {
        return lists.get(i);
    }

/*    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }*/

}
