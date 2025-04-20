package com.example.myapplication.utilities_plus_helpers;

import androidx.annotation.NonNull;

import com.example.myapplication.apiServices.MunicipalityApiService;
import com.google.gson.JsonObject;

public class MunicipalityDataHelper {

    public interface Listener {
        void onMunicipalityDataReady(String population); // lisää muutos myöhemmin!
        void onError(String errorMessage);
    }

    public void fetchPopulationAndChange (String municipality, Listener listener, String year) {

        MunicipalityCodeHelper.fetchMunicipalityCode(municipality, new MunicipalityCodeHelper.CodeListener() {
            @Override
            public void onCodeReady(String code) {
                JsonObject query = MunicipalityQueryBuilder.buildQuery(code, "vaesto", year);

                MunicipalityApiService api = ApiServiceBuilder.createService(
                        MunicipalityApiService.class,
                        "https://pxdata.stat.fi/PxWeb/api/v1/fi/"
                );


                api.getPopulationAndChange(query).enqueue(new retrofit2.Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject data = response.body();
                            if (data != null) {
                                // Indeksi 0: "vaesto", Indeksi 1: "valisays"
                                String population = data.getAsJsonArray("value").get(0).getAsString();
                                //String populationChange = data.getAsJsonArray("value").get(1).getAsString();
                                listener.onMunicipalityDataReady(population);
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

            @Override
            public void onError(String error) {


            }
        });
    }
}