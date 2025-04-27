package com.example.myapplication.core.util;

import android.util.Log;
import java.lang.ref.WeakReference;

import com.example.myapplication.core.network.ApiClient;
import com.example.myapplication.municipality.service.MunicipalityApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MunicipalityDataHelper {

    public interface Listener {
        void onMunicipalityDataReady(
                String population,
                String populationChange,
                String selfReliance,
                String employmentRate
        );
        void onError(String errorMessage);
    }

    public void fetchData(String municipality, Listener listener, String year) {
        WeakReference<Listener> listenerRef = new WeakReference<>(listener);

        MunicipalityCodeHelper.fetchMunicipalityCode(municipality, new MunicipalityCodeHelper.CodeListener() {
            @Override
            public void onCodeReady(String code) {
                MunicipalityApiService api = ApiClient.municipalityService();

                // Fetch population and change
                JsonObject popChangeQuery = MunicipalityQueryBuilder.buildQuery(
                        code,
                        new String[]{"vaesto", "valisays"},
                        year
                );
                api.getPopulationAndChange(popChangeQuery).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                        Listener l = listenerRef.get();
                        if (l == null) return;

                        Log.d("MunicipalityDataHelper", "HTTP " + resp.code() +
                                " — body: " + (resp.body() != null ? resp.body().toString() : "null"));

                        if (!resp.isSuccessful() || resp.body() == null) {
                            l.onError("Väkiluvun haku epäonnistui, HTTP " + resp.code());
                            return;
                        }

                        JsonArray values = resp.body().getAsJsonArray("value");

                        String change = values.size() > 0 ? values.get(0).getAsString() : "-";
                        String pop = values.size() > 1 ? values.get(1).getAsString() : "-";

                        // Fetch self-reliance
                        JsonObject selfRelianceQuery = MunicipalityQueryBuilder.buildJobSelfRelianceQuery(code, year);
                        api.getJobSelfReliance(selfRelianceQuery).enqueue(new Callback<>() {
                            @Override
                            public void onResponse(Call<JsonObject> call2, Response<JsonObject> resp2) {
                                Listener l2 = listenerRef.get();
                                if (l2 == null) return;

                                if (!resp2.isSuccessful() || resp2.body() == null) {
                                    l2.onError("Omavaraisuuden haku epäonnistui, HTTP " + resp2.code());
                                    return;
                                }

                                JsonArray values2 = resp2.body().getAsJsonArray("value");
                                String self = values2.size() > 0 ? values2.get(0).getAsString() : "-";

                                // Fetch employment rate
                                JsonObject employmentQuery = MunicipalityQueryBuilder.buildEmploymentRateQuery(code, year);
                                api.getEmploymentRate(employmentQuery).enqueue(new Callback<>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call3, Response<JsonObject> resp3) {
                                        Listener l3 = listenerRef.get();
                                        if (l3 == null) return;

                                        if (!resp3.isSuccessful() || resp3.body() == null) {
                                            l3.onError("Työllisyysasteen haku epäonnistui, HTTP " + resp3.code());
                                            return;
                                        }

                                        JsonArray values3 = resp3.body().getAsJsonArray("value");
                                        String emp = values3.size() > 0 ? values3.get(0).getAsString() : "-";

                                        // All data is ready
                                        l3.onMunicipalityDataReady(pop, change, self, emp);
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call3, Throwable t3) {
                                        Listener l3 = listenerRef.get();
                                        if (l3 != null)
                                            l3.onError("Työllisyysasteen haku epäonnistui: " + t3.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call2, Throwable t2) {
                                Listener l2 = listenerRef.get();
                                if (l2 != null)
                                    l2.onError("Omavaraisuuden haku epäonnistui: " + t2.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Listener l = listenerRef.get();
                        if (l != null) l.onError("Väkiluvun haku epäonnistui: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                Listener l = listenerRef.get();
                if (l != null) l.onError("Kuntakoodin haku epäonnistui: " + error);
            }
        });
    }
}
