package com.example.myapplication.utilities_plus_helpers;

import androidx.annotation.NonNull;

import com.example.myapplication.apiServices.MunicipalityApiService;
import com.google.gson.JsonObject;

public class MunicipalityDataHelper {

    public interface Listener {
        void onMunicipalityDataReady(int population, String populationChange);
        void onError(String errorMessage);
    }

    public void fetchPopulationAndChange (String municipality, Listener listener) {

        String municipalityCode = MunicipalityCodeHelper.getMunicipalityCode(municipality);
        if (municipalityCode == null) {
            listener.onError("Municipality not found");
            return;
        }

        JsonObject query = MunicipalityQueryBuilder.buildQuery(municipalityCode, "vaesto", "2023");

        MunicipalityApiService api = ApiServiceBuilder.createService(MunicipalityApiService.class, "https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px");


        api.getPopulationAndChange(query).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject data = response.body();
                    if (data != null) {
                        // Indeksi 0: "vaesto", Indeksi 1: "valisays"
                        int population = data.getAsJsonArray("value").get(0).getAsInt();
                        String populationChange = data.getAsJsonArray("value").get(1).getAsString();
                        listener.onMunicipalityDataReady(population, populationChange);
                    } else {
                        listener.onError("Dataa ei löydetty");
                    }
                } else {
                    listener.onError("API yhteyden haku epäonnistui: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}
