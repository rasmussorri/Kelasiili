package com.example.myapplication.utilities_plus_helpers;

import com.example.myapplication.apiServices.MunicipalityApiService;
import com.example.myapplication.apiServices.TrafficApiService;
import com.example.myapplication.apiServices.WeatherApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String FMI_BASE = "https://opendata.fmi.fi/";
    private static final String TRAFFIC_BASE = "https://tie.digitraffic.fi/api/weathercam/v1/";
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    public static WeatherApiService weatherService() {
        return new Retrofit.Builder()
                .baseUrl(FMI_BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService.class);
    }
    public static TrafficApiService trafficService() {
        return new Retrofit.Builder()
                .baseUrl(TRAFFIC_BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TrafficApiService.class);
    }
    public static MunicipalityApiService municipalityService() {
        return new Retrofit.Builder()
                .baseUrl("https://pxdata.stat.fi/PxWeb/api/v1/fi/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MunicipalityApiService.class);
    }
}

