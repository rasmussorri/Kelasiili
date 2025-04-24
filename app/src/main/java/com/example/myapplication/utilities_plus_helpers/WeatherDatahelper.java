package com.example.myapplication.utilities_plus_helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.AirQualityData;
import com.example.myapplication.apiServices.WeatherApiService;
import com.example.myapplication.utilities_plus_helpers.XmlWeatherParser.ParameterResult;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
        Log.d("WeatherDatahelper", "Kunta: " + municipality);
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
                        // Jos symboli antaa jonkun muun kuin luvun tai muuta shittiä, niin laitetaan oletusarvo = aurinkologo
                        Log.e("WeatherDatahelper", "Virheellinen symboliarvo: " + symbolId, e);
                        raw = 1;
                    }
                    int symbol = Math.round(raw);
                    if (symbol == 0) {
                        symbol = 1; // Jos symboli on 0, käytetään oletusarvoa
                    }
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


                    // 1) Tunnisteet → luettavat suomennetut selitteet
                    Map<String, String> labels = new HashMap<>();
                    labels.put("QBCPM25_PT1H_AVG",   "PM₂.₅ ");
                    labels.put("QBCPM10_PT1H_AVG",   "PM₁₀ ");
                    labels.put("NO2_PT1H_avg",       "NO₂ ");
                    labels.put("O3_PT1H_avg",        "O₃ ");
                    labels.put("SO2_PT1H_avg",       "SO₂ ");
                    labels.put("CO_PT1H_avg",        "CO ");
                    labels.put("NO_PT1H_avg",        "NO ");
                    labels.put("AQINDEX_PT1H_avg",   "Ilmanlaatu-indeksi");
                    labels.put("AQINDEXCLASS_PT1H_avg","Ilmanlaatu-luokka");
                    labels.put("AQINDEXTEXT_PT1H_avg","Ilmanlaatu-kuvaus");

                    // 2) Halutut tagit ja suodatus
                    List<String> tags = Arrays.asList(
                            "QBCPM25_PT1H_AVG", "QBCPM10_PT1H_AVG", "NO2_PT1H_avg",
                            "O3_PT1H_avg", "SO2_PT1H_avg", "CO_PT1H_avg",
                            "NO_PT1H_avg", "AQINDEX_PT1H_avg",
                            "AQINDEXCLASS_PT1H_avg", "AQINDEXTEXT_PT1H_avg"
                    );
                    Map<String, ParameterResult> filtered = new LinkedHashMap<>();
                    for (String tag : tags) {
                        ParameterResult pr = XmlWeatherParser.parseWithTimestamp(xml, tag);
                        if (pr.value != null && !pr.value.isEmpty() && !"-".equals(pr.value)) {
                            String label = labels.getOrDefault(tag, tag);
                            filtered.put(label, pr);
                        }
                    }
                    listener.onAirQualityDataReceived(new AirQualityData(filtered));

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
