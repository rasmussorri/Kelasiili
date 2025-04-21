package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import com.example.myapplication.apiServices.TrafficApiService;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class TrafficCameraHelper {

    public interface TrafficCameraCallback {
        void onResult(List<String> cameraImageUrls);
        void onError(String error);
    }

    public interface TrafficCameraApi {
        @GET("traffic-camera/stations")
        Call<TrafficCameraResponse> getAllCameras();
    }

    public static class TrafficCameraResponse {
        public List<CameraStation> features;
    }

    public static class CameraStation {
        public String id;
        public String name;

        @SerializedName("cameraPresets")
        public List<CameraPreset> cameraPresets;
    }

    public static class CameraPreset {
        public String id;
        public String presentationName;
        public String imageUrl;
    }

    public static void fetchCamerasByMunicipality(String municipalityName, TrafficCameraCallback callback) {
        // Tämän voi laittaa menemään ApiServiceBuilderin kautta
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tie.digitraffic.fi/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TrafficApiService api = retrofit.create(TrafficApiService.class);

        api.getAllCameras().enqueue(new Callback<TrafficCameraResponse>() {
            @Override
            public void onResponse(Call<TrafficCameraResponse> call, Response<TrafficCameraResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> imageUrls = new ArrayList<>();

                    for (CameraStation station : response.body().features) {
                        if (station.name != null && station.name.toLowerCase().contains(municipalityName.toLowerCase())) {
                            if (station.cameraPresets != null) {
                                for (CameraPreset preset : station.cameraPresets) {
                                    if (preset.imageUrl != null) {
                                        imageUrls.add(preset.imageUrl);
                                    }
                                }
                            }
                        }
                    }

                    callback.onResult(imageUrls);

                } else {
                    callback.onError("Virhe kameratietojen haussa. Koodi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TrafficCameraResponse> call, Throwable t) {
                callback.onError("Verkkovirhe: " + t.getMessage());
            }
        });
    }
}

