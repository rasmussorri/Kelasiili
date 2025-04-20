package com.example.myapplication.utilities_plus_helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceBuilder {
    // Luo aina uusi Retrofit-instanssi annetulla URL:lla
    // Muuntaja muuntaa haetun JSON-muotoisen datan Java-olioiksi
    // Käytetään geneeristä metodia, joka luo API palvelun
    public static <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") // Varmista lopussa '/'
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(serviceClass);
    }
}
