package com.weather.www.wt.gson;

import com.google.gson.annotations.SerializedName;

public class Air {
    @SerializedName("aqi")
    public String aqin;
    public String qlty;
    public String pm25;
    public String time;
    public String NO2;
    public String SO2;
    public String O3;
}
