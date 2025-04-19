package com.example.myapplication.utilities_plus_helpers;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import java.io.IOException;
import java.util.List;

public class MunicipalityCodeHelper {

    interface ApiService {
        @GET("PxWeb/api/v1/fi/Kuntien_avainluvut/2023/142h_2023.px")
        Call<MetadataResponse> getMetadata();
    }

    public static class MetadataResponse {
        public List<Variable> variables;

        public static class Variable {
            public String code;

            @SerializedName("values")
            public List<String> values;

            @SerializedName("valueTexts")
            public List<String> valueTexts;
        }
    }
    public interface MunicipalityCodeCallback {
        void onCodeFound(String code);
        void onError(String errorMessage);
    }


    // Tämä pitää muuttaa ApiServiceBuilderin kautta meneväksi
    public static void getMunicipalityCode(String municipality, MunicipalityCodeCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pxdata.stat.fi/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        api.getMetadata().enqueue(new retrofit2.Callback<MetadataResponse>() {
            @Override
            public void onResponse(Call<MetadataResponse> call, Response<MetadataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (MetadataResponse.Variable variable : response.body().variables) {
                        if ("Alue".equals(variable.code)) {
                            List<String> names = variable.valueTexts;
                            List<String> codes = variable.values;

                            for (int i = 0; i < names.size(); i++) {
                                if (names.get(i).equalsIgnoreCase(municipality)) {
                                    callback.onCodeFound(codes.get(i));
                                    return;
                                }
                            }
                            callback.onError("Kuntaa ei löytynyt: " + municipality);
                            return;
                        }
                    }
                } else {
                    callback.onError("Virhe vastauksessa API:lta.");
                }
            }

            @Override
            public void onFailure(Call<MetadataResponse> call, Throwable t) {
                callback.onError("Verkkovirhe: " + t.getMessage());
            }
        });
    }
}