package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import com.example.myapplication.apiServices.TrafficApiService;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrafficCameraHelper {

    public interface TrafficCameraCallback {
        void onResult(List<String> cameraImageUrls);
        void onError(String error);
    }

    public static class TrafficCameraResponse {
        public List<CameraStation> features;
    }

    public static class CameraStation {
        public String id;

        @SerializedName("properties")
        public Properties properties;
    }

    public static class Properties {
        public String id;
        public String name;
        public List<Preset> presets;
    }

    public static class Preset {
        public String id;
        public boolean inCollection;
    }

    public static void fetchCamerasByMunicipality(String municipalityName, TrafficCameraCallback callback) {
        TrafficApiService api = ApiClient.trafficService();


        api.getAllCameras().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<TrafficCameraResponse> call, Response<TrafficCameraResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FINTRAFFIC_RAW", response.body().toString());

                    List<String> imageUrls = new ArrayList<>();

                    for (CameraStation station : response.body().features) {
                        if (station.properties != null && station.properties.name != null &&
                                station.properties.name.toLowerCase().contains(municipalityName.toLowerCase())) {

                            if (station.properties.presets != null) {
                                for (Preset preset : station.properties.presets) {
                                    if (preset.inCollection && preset.id != null) {
                                        String imageUrl = "https://weathercam.digitraffic.fi/" + preset.id + ".jpg?ts=" + System.currentTimeMillis();
                                        imageUrls.add(imageUrl);
                                    }
                                }
                            }
                        }
                    }

                    callback.onResult(imageUrls);

                } else {
                    Log.e("FINTRAFFIC_ERROR", "Vastaus ei ollut onnistunut tai body oli null");
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("FINTRAFFIC_ERROR", "Code: " + response.code() + "\nError body:\n" + errorBody);
                        callback.onError("Virhe kameratietojen haussa. Koodi: " + response.code());
                    } catch (IOException e) {
                        Log.e("FINTRAFFIC_ERROR", "Virheen purku epäonnistui: " + e.getMessage());
                        callback.onError("Virhe kameratietojen haussa.");
                    }
                }
            }

            @Override
            public void onFailure(Call<TrafficCameraResponse> call, Throwable t) {
                Log.e("FINTRAFFIC_ERROR", "onFailure: " + t.getMessage(), t);
                callback.onError("Verkkovirhe: " + t.getMessage());
            }
        });
    }
}
