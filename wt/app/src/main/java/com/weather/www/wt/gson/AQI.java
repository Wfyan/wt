package com.weather.www.wt.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    public String status;
    public Basic basic;
    public Update update;
    @SerializedName("air_now_city")
    public Air air;
}
