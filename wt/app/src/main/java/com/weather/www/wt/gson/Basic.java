package com.weather.www.wt.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("admin_area")
    public String cityName;
    @SerializedName("cid")
    public String weatherId;
}
