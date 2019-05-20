package com.weather.www.wt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weather.www.wt.db.City;
import com.weather.www.wt.db.County;
import com.weather.www.wt.db.Province;
import com.weather.www.wt.util.HttpUtil;
import com.weather.www.wt.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //选中的级别
    private int currentLevel;
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //创建适配器
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view; //布局返回
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有省份
     */
    private void queryProvinces(){
        //UI处理
        titleText.setText("中国");
        backButton.setVisibility(View.GONE); //隐藏返回按钮
        //数据处理
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getName());
            }
            //当适配器数据发生改变时强制调用getView()，返回一个新布局
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    /**
     * 查询某省的所有城市
     */
    private void queryCities(){
        //UI处理
        titleText.setText(selectedProvince.getName());
        backButton.setVisibility(View.VISIBLE);
        //数据处理
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.
                getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0); //重置
            currentLevel = LEVEL_CITY;
        }else{
            int code = selectedProvince.getCode();
            String address = "http://guolin.tech/api/china/"+code;
            queryFromServer(address,"city");
        }
    }
    /**
     * 查询某市的所有的县
     */
    private void queryCounties(){
        //UI处理
        titleText.setText(selectedCity.getName());
        backButton.setVisibility(View.VISIBLE);
        //数据处理
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.
                getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0); //重置
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvince.getCode();
            int cityCode = selectedCity.getCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+
                    cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器查询省市县数据
     */
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //返回到主线程处理
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                //获取服务器返回的数据
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.ProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.CityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.CountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss(); //关闭对话框
        }
    }
}