package com.weather.www.wt.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.www.wt.R;
import com.weather.www.wt.gson.Weather;

import java.util.List;

public class CityAdapter extends ArrayAdapter<Weather> {
    private int resourceId; //用来记录子布局的布局方式
    private boolean isShowDelete;//判断显示图标
    //创建适配器构造方法
    public CityAdapter(Context context, int textViewResourceId, List<Weather> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId; }
    //重写


    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.isShowDelete = showDelete;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Weather weather = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView cityName = (TextView)view.findViewById(R.id.city_name);
        TextView degree = (TextView)view.findViewById(R.id.city_degree);
        TextView txt = (TextView)view.findViewById(R.id.city_txt);
        ImageView delete = (ImageView)view.findViewById(R.id.delete_item);

        cityName.setText(weather.basic.cityName);
        degree.setText(weather.now.temperture+"℃");
        txt.setText(weather.now.cond_txt);
        if (isShowDelete) {
            delete.setVisibility(View.VISIBLE);
            System.out.println("可见");
        } else {
            delete.setVisibility(View.GONE);
        }

/*        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("跳到WeatherActivity相应界面");
            }
        });*/
        return view;
    }

}
