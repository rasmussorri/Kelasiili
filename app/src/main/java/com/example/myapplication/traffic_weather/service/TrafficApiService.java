package com.example.myapplication.traffic_weather.service;

import com.example.myapplication.core.util.TrafficCameraHelper.TrafficCameraResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TrafficApiService {

    @GET("stations")
    Call<TrafficCameraResponse> getAllCameras();

}