package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.apiServices.MunicipalityApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MunicipalityDataHelper {

    public interface Listener {
        void onMunicipalityDataReady(String population, String populationChange, String selfReliance, String employmentRate);
        void onError(String errorMessage);
    }

    public void fetchData(String municipality, Listener listener, String year) {
        MunicipalityCodeHelper.fetchMunicipalityCode(municipality, new MunicipalityCodeHelper.CodeListener() {
            @Override
            public void onCodeReady(String code) {
                final String[] population = {null};
                final String[] populationChange = {null};
                final String[] selfReliance = {null};
                final String[] employmentRate = {null};

                MunicipalityApiService api = ApiServiceBuilder.createService(
                        MunicipalityApiService.class,
                        "https://pxdata.stat.fi/PxWeb/api/v1/fi/"
                );
                // Väkiluvun haku

                JsonObject popQuery = MunicipalityQueryBuilder.buildQuery(code, new String[]{"vaesto"}, year);
                api.getPopulationAndChange(popQuery).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonArray values = response.body().getAsJsonArray("value");
                            if (values != null && values.size() > 0) {
                                population[0] = values.get(0).getAsString();
                                Log.d("POPULATION", "Väkiluku haettu: " + population[0]);
                            }
                        }
                        // Väkiluvun muutoksen haku

                        JsonObject changeQuery = MunicipalityQueryBuilder.buildQuery(code, new String[]{"valisays"}, year);
                        api.getPopulationAndChange(changeQuery).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call2, @NonNull Response<JsonObject> response2) {
                                if (response2.isSuccessful() && response2.body() != null) {
                                    JsonArray values2 = response2.body().getAsJsonArray("value");
                                    if (values2 != null && values2.size() > 0) {
                                        populationChange[0] = values2.get(0).getAsString();
                                        Log.d("CHANGE", "Muutos haettu: " + populationChange[0]);
                                    }
                                }
                                // Omavaraisuusasteen haku

                                JsonObject relianceQuery = MunicipalityQueryBuilder.buildJobSelfRelianceQuery(code, year);
                                api.getJobSelfReliance(relianceQuery).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            JsonArray values = response.body().getAsJsonArray("value");
                                            if (values != null && values.size() > 0) {
                                                selfReliance[0] = values.get(0).getAsString();
                                                Log.d("SELF", "Omavaraisuus: " + selfReliance[0]);
                                            }
                                        }
                                        // Työllisyysasteen haku

                                        JsonObject empQuery = MunicipalityQueryBuilder.buildEmploymentRateQuery(code, year);
                                        api.getEmploymentRate(empQuery).enqueue(new Callback<JsonObject>() {
                                            @Override
                                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                                if (response.isSuccessful() && response.body() != null) {
                                                    JsonArray values = response.body().getAsJsonArray("value");
                                                    if (values != null && values.size() > 0) {
                                                        employmentRate[0] = values.get(0).getAsString();
                                                        Log.d("EMP", "Työllisyysaste: " + employmentRate[0]);
                                                    }
                                                }

                                                // Kaikki haettu data listeneriin
                                                listener.onMunicipalityDataReady(
                                                        population[0] != null ? population[0] : "-",
                                                        populationChange[0] != null ? populationChange[0] : "-",
                                                        selfReliance[0] != null ? selfReliance[0] : "-",
                                                        employmentRate[0] != null ? employmentRate[0] : "-"
                                                );
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                                listener.onMunicipalityDataReady(
                                                        population[0] != null ? population[0] : "-",
                                                        populationChange[0] != null ? populationChange[0] : "-",
                                                        selfReliance[0] != null ? selfReliance[0] : "-",
                                                        "-"
                                                );
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                        listener.onMunicipalityDataReady(
                                                population[0] != null ? population[0] : "-",
                                                populationChange[0] != null ? populationChange[0] : "-",
                                                "-",
                                                "-"
                                        );
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                listener.onMunicipalityDataReady(
                                        population[0] != null ? population[0] : "-",
                                        "-",
                                        "-",
                                        "-"
                                );
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        listener.onError("Väkiluvun haku epäonnistui");
                    }
                });
            }

            @Override
            public void onError(String error) {
                listener.onError("Kuntakoodin haku epäonnistui");
                Log.e("ERROR", "Error occurred: " + error);
            }
        });
    }
}
