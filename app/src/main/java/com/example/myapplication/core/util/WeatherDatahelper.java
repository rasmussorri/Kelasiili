package com.example.myapplication.core.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.airquality.AirQualityData;
import com.example.myapplication.core.network.ApiClient;
import com.example.myapplication.traffic_weather.service.WeatherApiService;
import com.example.myapplication.core.util.XmlWeatherParser.ParameterResult;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherDatahelper {

    public interface WeatherListener {
        void onWeatherDataReceived(String temp, int symbol, String rain);
        void onAirQualityDataReceived(AirQualityData airData);
        void onError(String errorMessage);
    }

    public static void fetchWeatherAndAirQuality(Context ctx,
                                                 String municipality,
                                                 String stationId,
                                                 WeatherListener listener) {
        final String BASE = "https://opendata.fmi.fi/wfs";
        WeatherApiService api = ApiClient.weatherService();


        // Fetching weather data
        String placeUrl = WeatherQueryBuilder.buildWeatherQuery(municipality);
        Log.d("WeatherDatahelper", "Weather→place: " + placeUrl);

        api.getCurrentWeather(placeUrl).enqueue(new Callback<>() {
            boolean didFallback = false;

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> resp) {
                String xml;
                try {
                    if (!resp.isSuccessful() || resp.body() == null) {
                        Log.w("WeatherDatahelper", "Place query HTTP " + resp.code());
                        doFallback();
                        return;
                    }
                    xml = resp.body().string();
                    //Log.d("FMI_XML", xml);
                } catch (IOException e) {
                    Log.w("WeatherDatahelper", "Place query read failed", e);
                    doFallback();
                    return;
                }

                if (xml.contains("<ExceptionReport")) {
                    Log.w("WeatherDatahelper", "Place query returned ExceptionReport");
                    doFallback();
                    return;
                }

                // If already fell back it is not tried again
                if (didFallback) {
                    parseAndNotify(xml, ctx, listener);
                    return;
                }

                // Normal case
                parseAndNotify(xml, ctx, listener);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.w("WeatherDatahelper", "Place query failed", t);
                doFallback();
            }

            private void doFallback() {
                if (didFallback) {
                    listener.onError("Ei säätietoja saatavilla");
                    return;
                }
                didFallback = true;
                Executors.newSingleThreadExecutor().execute(() -> {
                    LatLng c = geocodeMunicipality(ctx, municipality);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (c != null) {
                            String llUrl = BASE + WeatherQueryBuilder.buildByLatLon(c.latitude, c.longitude);
                            api.getCurrentWeather(llUrl).enqueue(this);
                        } else {
                            listener.onError("Kuntaa '" + municipality + "' ei löytynyt");
                        }
                    });
                });
            }

            private void parseAndNotify(String xml, Context context, WeatherListener listener) {
                // Parsing temperature
                ParameterResult t = XmlWeatherParser.parseTimeValuePair(xml, "t2m");
                if ("NaN".equals(t.value) || "-".equals(t.value)) {
                    t = XmlWeatherParser.parseWithTimestamp(xml, "TA_PT1H_AVG");
                }

                // Parsing weather symbol
                ParameterResult s = XmlWeatherParser.parseWithTimestamp(xml, "WAWA_PT1H_RANK");
                int sym = 1;
                try {
                    sym = Math.max(1, Math.round(Float.parseFloat(s.value)));
                } catch (Exception ignored) {
                }
                int icon = context.getResources()
                        .getIdentifier("fmi_" + sym, "drawable", context.getPackageName());

                // Parsing rain
                ParameterResult r = XmlWeatherParser.parseTimeValuePair(xml, "r_1h");
                if ("NaN".equals(r.value) || "-".equals(r.value)) {
                    r = XmlWeatherParser.parseTimeValuePair(xml, "ri_10min");
                }
                if ("NaN".equals(r.value) || "-".equals(r.value)) {
                    r = new ParameterResult(r.time, "-");
                }

                listener.onWeatherDataReceived(t.value, icon, r.time.equals("-") ? "-" : (r.value + "@" + r.time));
            }
        });

        // Fetching and parsing air quality data
        String airUrl = WeatherQueryBuilder.buildAirQualityQuery(stationId);
        api.getAirQuality(airUrl).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> resp) {
                try {
                    String xml = resp.body().string();


                    // Keys for parsing and labels for display
                    Map<String, String> labels = new HashMap<>();
                    labels.put("QBCPM25_PT1H_AVG", "PM₂.₅ (μg/m³)");
                    labels.put("QBCPM10_PT1H_AVG", "PM₁₀ (μg/m³)");
                    labels.put("NO2_PT1H_avg", "NO₂ (μg/m³)");
                    labels.put("O3_PT1H_avg", "O₃ (μg/m³)");
                    labels.put("SO2_PT1H_avg", "SO₂ (μg/m³)");
                    labels.put("CO_PT1H_avg", "CO (μg/m³)");
                    labels.put("NO_PT1H_avg", "NO (μg/m³)");
                    labels.put("AQINDEX_PT1H_avg", "Ilmanlaatu-indeksi");
                    labels.put("AQINDEXCLASS_PT1H_avg", "Ilmanlaatu-luokka");
                    labels.put("AQINDEXTEXT_PT1H_avg", "Ilmanlaatu-kuvaus");

                    // Keys for parsing
                    List<String> tags = Arrays.asList(
                            "QBCPM25_PT1H_AVG", "QBCPM10_PT1H_AVG", "NO2_PT1H_avg",
                            "O3_PT1H_avg", "SO2_PT1H_avg", "CO_PT1H_avg",
                            "NO_PT1H_avg", "AQINDEX_PT1H_avg",
                            "AQINDEXCLASS_PT1H_avg", "AQINDEXTEXT_PT1H_avg"
                    );
                    Map<String, ParameterResult> filtered = new LinkedHashMap<>();
                    for (String tag : tags) {
                        ParameterResult pr = XmlWeatherParser.parseWithTimestamp(xml, tag);
                        if (!"-".equals(pr.value)) {
                            filtered.put(labels.getOrDefault(tag, tag), pr);
                        }
                    }

                    listener.onAirQualityDataReceived(new AirQualityData(filtered));
                } catch (Exception e) {
                    Log.e("WeatherDatahelper", "Air quality parsing failed", e);
                    listener.onError("Ilmanlaadun käsittely epäonnistui");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("WeatherDatahelper", "Air quality fetching failed", t);
                listener.onError("Ilmanlaadun haku epäonnistui");
            }
        });
    }

    private static LatLng geocodeMunicipality(Context ctx, String mun) {
        try {
            Geocoder g = new Geocoder(ctx, Locale.getDefault());
            List<Address> res = g.getFromLocationName(mun, 1);
            if (!res.isEmpty()) {
                Address a = res.get(0);
                return new LatLng(a.getLatitude(), a.getLongitude());
            }
        } catch (IOException e) {
            Log.e("WeatherDatahelper", "Geocoding failed: " + mun, e);
        }
        return null;
    }

    private static class LatLng {
        final double latitude, longitude;
        LatLng(double lat, double lon) { latitude = lat; longitude = lon; }
    }
}

