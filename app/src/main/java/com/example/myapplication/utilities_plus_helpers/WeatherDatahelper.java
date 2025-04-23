package com.example.myapplication.utilities_plus_helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.AirQualityData;
import com.example.myapplication.apiServices.WeatherApiService;
import com.example.myapplication.utilities_plus_helpers.XmlWeatherParser.ParameterResult;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDatahelper {

    public interface WeatherListener{
        void onWeatherDataReceived(String temp, int symbol, String rain);
        void onAirQualityDataReceived( AirQualityData airData );
        void onError(String errorMessage);


    }

    public static void fetchWeatherAndAirQuality(Context context, String municipality , String stationId, WeatherListener listener) {

        WeatherApiService api = ApiServiceBuilder.createService(WeatherApiService.class, "https://opendata.fmi.fi/wfs");
        String weatherQuery = WeatherQueryBuilder.buildWeatherQuery(municipality);
        String airQualityQuery = WeatherQueryBuilder.buildAirQualityQuery(stationId);

        api.getCurrentWeather(weatherQuery).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    listener.onError("Säädatan haku epäonnistui: HTTP " + response.code());
                    return;
                }
                try {
                    String xml = response.body().string();
                    Log.d("FMI_XML", xml);
                    ParameterResult temperatureResult = XmlWeatherParser.parseWithTimestamp(xml, "TA_PT1H_AVG");
                    ParameterResult symbolResult = XmlWeatherParser.parseWithTimestamp(xml, "WAWA_PT1H_RANK");
                    ParameterResult rainResult = XmlWeatherParser.parseWithTimestamp(xml, "PRA_PT1H_ACC");
                    String temperature = temperatureResult.value;
                    String symbolId = symbolResult.value;
                    String rain = rainResult.value;
                    float raw;
                    try {
                        raw = Float.parseFloat(symbolId);
                    } catch (NumberFormatException e) {
                        Log.e("WeatherDatahelper", "Virheellinen sademäärä: " + rain, e);
                        raw = 0f;
                    }
                    int symbol = Math.round(raw);
                    String symbolString = "fmi_" + symbol;
                    Log.d("SYMBOOLI", symbolString);

                    int symbolFromStorage = context.getResources().getIdentifier(symbolString, "drawable", context.getPackageName());
                    Log.d("SYMBOL", "Symboli: " + symbolFromStorage);
                    listener.onWeatherDataReceived(temperature, symbolFromStorage, rain);

                } catch (Exception e) {
                    Log.e("WeatherDatahelper", "Virhe parsittaessa säätietoja", e);
                    listener.onError("Säädatan käsittely epäonnistui");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onError("Säädatan haku epäonnistui: " + t.getMessage());
            }
        });

        api.getAirQuality(airQualityQuery).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String xml = response.body().string();
                    //Log.d("FMI_AIR_QUALITY_XML", xml);


                    Map<String, ParameterResult> map = new HashMap<>();
                    for (String tag : Arrays.asList(
                            "QBCPM25_PT1H_AVG",
                            "QBCPM10_PT1H_AVG",
                            "NO2_PT1H_avg",
                            "O3_PT1H_avg",
                            "SO2_PT1H_avg",
                            "CO_PT1H_avg",
                            "NO_PT1H_avg",
                            "AQINDEX_PT1H_avg",
                            "AQINDEXCLASS_PT1H_avg",
                            "AQINDEXTEXT_PT1H_avg")) {
                        map.put(tag, XmlWeatherParser.parseWithTimestamp(xml, tag));
                    }
                    listener.onAirQualityDataReceived(new AirQualityData(map));

                } catch (Exception e) {
                    listener.onError("Ilmanlaadun käsittely epäonnistui");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onError("Ilmanlaadun haku epäonnistui: " + t.getMessage());
            }
        });
    }
}
