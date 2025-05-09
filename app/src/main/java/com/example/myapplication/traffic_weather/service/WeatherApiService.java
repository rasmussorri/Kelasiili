package com.example.myapplication.traffic_weather.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherApiService {

    @GET
    Call<ResponseBody> getCurrentWeather(@Url String url);

    @GET
    Call<ResponseBody> getAirQuality(@Url String url);



}
