package com.weather.www.wt.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperture;
    public String cond_txt;
    public String cond_code;
}
