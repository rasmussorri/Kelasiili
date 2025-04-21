package com.example.myapplication.apiServices;

import com.example.myapplication.utilities_plus_helpers.TrafficCameraHelper.TrafficCameraResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TrafficApiService {

    @GET("stations")
    Call<TrafficCameraResponse> getAllCameras();

}