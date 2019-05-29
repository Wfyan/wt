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

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends ArrayAdapter<Weather> {
    private int resourceId; //用来记录子布局的布局方式
    private boolean isShowDelete;//判断显示图标

    private List<String> weatherIdList;
    private List<Integer> positionList;

    //创建适配器构造方法
    public CityAdapter(Context context, int textViewResourceId, List<Weather> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
        weatherIdList = new ArrayList<String>();
        positionList = new ArrayList<Integer>();
    }

    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.isShowDelete = showDelete;
        notifyDataSetChanged();
    }

    public List<String> getWeatherIdList() {
        return weatherIdList;
    }

    public void setWeatherIdList(List<String> weatherIdList) {
        this.weatherIdList = weatherIdList;
    }

    public List<Integer> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<Integer> positionList) {
        this.positionList = positionList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        final Weather weather = getItem(position);
        final View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.cityName = (TextView)view.findViewById(R.id.city_name);
            viewHolder.degree = (TextView)view.findViewById(R.id.city_degree);
            viewHolder.txt = (TextView)view.findViewById(R.id.city_txt);
            viewHolder.delete = (ImageView)view.findViewById(R.id.delete_item);
            //将viewHolder存到view中
            view.setTag(viewHolder);
        }else{
            view = convertView;
            //复用控件实例
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.cityName.setText(weather.basic.cityName);
        viewHolder.degree.setText(weather.now.temperture+"℃");
        viewHolder.txt.setText(weather.now.cond_txt);

        viewHolder.delete.setVisibility(isShowDelete?View.VISIBLE:View.GONE);
        viewHolder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                weatherIdList.add(weather.basic.weatherId);
                positionList.add(position);
                view.setVisibility(View.GONE);
            }
        });
        view.setVisibility(View.VISIBLE);
        return view;
    }
    class ViewHolder{
        TextView cityName;
        TextView degree;
        TextView txt;
        ImageView delete;
    }
}
