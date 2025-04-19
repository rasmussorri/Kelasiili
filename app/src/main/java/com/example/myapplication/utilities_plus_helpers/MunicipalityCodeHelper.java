package com.example.myapplication.utilities_plus_helpers;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
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

    public static String getMunicipalityCode(String municipality) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pxdata.stat.fi/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        try {
            MetadataResponse data = api.getMetadata().execute().body();
            if (data != null) {
                for (MetadataResponse.Variable variable : data.variables) {
                    if ("Alue".equals(variable.code)) {
                        List<String> names = variable.valueTexts;
                        List<String> codes = variable.values;

                        for (int i = 0; i < names.size(); i++) {
                            if (names.get(i).toLowerCase().equals(municipality.toLowerCase())) {
                                return codes.get(i);
                            }
                        }
                    }
                }
            } else {
                System.out.println("Virhe: tyhjä vastaus API:lta.");
            }
        } catch (IOException e) {
            System.out.println("Verkkovirhe: " + e.getMessage());
        }
        return null;
    }
}