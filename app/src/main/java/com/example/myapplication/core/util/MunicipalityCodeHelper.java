package com.example.myapplication.core.util;

import com.example.myapplication.core.network.ApiClient;
import com.example.myapplication.municipality.service.MunicipalityApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MunicipalityCodeHelper {

    public interface CodeListener {
        void onCodeReady(String code);
        void onError(String error);
    }

    public static void fetchMunicipalityCode(String name, CodeListener listener) {
        MunicipalityApiService api = ApiClient.municipalityService();

        api.getMunicipalityMetadata().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    listener.onError("Virheellinen vastaus API:sta");
                    return;
                }

                JsonObject json = response.body();
                JsonArray values = json.getAsJsonArray("variables")
                        .get(0).getAsJsonObject()
                        .getAsJsonArray("values");

                JsonArray valueTexts = json.getAsJsonArray("variables")
                        .get(0).getAsJsonObject()
                        .getAsJsonArray("valueTexts");

                for (int i = 0; i < valueTexts.size(); i++) {
                    if (valueTexts.get(i).getAsString().equalsIgnoreCase(name)) {
                        listener.onCodeReady(values.get(i).getAsString());
                        return;
                    }
                }

                listener.onError("Kuntaa ei löytynyt");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}
