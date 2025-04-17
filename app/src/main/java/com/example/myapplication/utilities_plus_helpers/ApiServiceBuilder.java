package com.example.myapplication.utilities_plus_helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceBuilder {
    private static Retrofit retrofit = null;

    // Luo singleton retrofit instanssin, jolle annetaan APIn verkko-osoite
    // Muuntaja muuntaa haetun JSON-muotoisen datan Java-olioiksi
    // Nyt voidaan luoda useita API palveluja samalla metodilla
    public static Retrofit getClient(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Käytetään geneeristä metodia, joka luo API palvelun
    public static <T> T createService(Class<T> serviceClass, String url) {
        return getClient(url).create(serviceClass);
    }

}
