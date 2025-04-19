package com.example.myapplication.utilities_plus_helpers;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.apiServices.MunicipalityApiService;
import com.google.gson.JsonObject;

public class MunicipalityDataHelper {

    public interface Listener {
        void onMunicipalityDataReady(int population, String populationChange);
        void onError(String errorMessage);
    }

    public void fetchPopulationAndChange (String municipality, Listener listener) {

        MunicipalityCodeHelper.getMunicipalityCode(municipality, new MunicipalityCodeHelper.MunicipalityCodeCallback() {
            @Override
            public void onCodeFound(String code) {
                // Koodin haku onnistui - Voidaan tehdä varsinainen haku
                JsonObject query = MunicipalityQueryBuilder.buildQuery(code, "vaesto", "2023");

                System.out.println("Query JSON: " + query.toString()); // Debugging line

                MunicipalityApiService api = ApiServiceBuilder.createService(
                        MunicipalityApiService.class,
                        "https://pxdata.stat.fi/"
                );

                api.getPopulationAndChange(query).enqueue(new retrofit2.Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject data = response.body();
                            if (data != null) {
                                int population = data.getAsJsonArray("value").get(0).getAsInt();
                                String populationChange = data.getAsJsonArray("value").get(1).getAsString();
                                listener.onMunicipalityDataReady(population, populationChange);
                            } else {
                                listener.onError("Dataa ei löydetty");
                            }
                        } else {
                            listener.onError("API-yhteyden haku epäonnistui: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                        listener.onError("Verkkovirhe: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError("Kuntakoodin haku epäonnistui: " + errorMessage);
            }
        });
    }
}
