package com.example.myapplication.utilities_plus_helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.AirQualityData;
import com.example.myapplication.apiServices.WeatherApiService;
import com.example.myapplication.utilities_plus_helpers.XmlWeatherParser.ParameterResult;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*public class WeatherDatahelper {

    public interface WeatherListener{
        void onWeatherDataReceived(String temp, int symbol, String rain);
        void onAirQualityDataReceived( AirQualityData airData );
        void onError(String errorMessage);


    }

    public static void fetchWeatherAndAirQuality(Context context, String municipality , String stationId, WeatherListener listener) {

        WeatherApiService api = ApiServiceBuilder.createService(WeatherApiService.class, "https://opendata.fmi.fi/wfs");
        Log.d("WeatherDatahelper", "Kunta: " + municipality);
        String base = "https://opendata.fmi.fi/wfs";
        String weatherUrl = base + WeatherQueryBuilder.buildWeatherQuery(municipality);
        //String weatherQuery = WeatherQueryBuilder.buildWeatherQuery(municipality);
        //String airQualityQuery = WeatherQueryBuilder.buildAirQualityQuery(stationId);

        api.getCurrentWeather(weatherUrl).enqueue(new Callback<>() {
            boolean fellback = false;

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {

                    if (!response.isSuccessful() || response.body() == null) {
                        throw new IOException("HTTP virhe: " + response.code());
                    }

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
*/

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
        WeatherApiService api = ApiServiceBuilder.createService(
                WeatherApiService.class,
                "https://opendata.fmi.fi"
        );

        // 1) PLACE-based weather via timevaluepair
        String placeUrl = WeatherQueryBuilder.buildWeatherQuery(municipality);
        Log.d("WeatherDatahelper", "Weather→place: " + placeUrl);

        api.getCurrentWeather(placeUrl).enqueue(new Callback<ResponseBody>() {
            boolean didFallback = false;

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull retrofit2.Response<ResponseBody> resp) {
                String xml;

                // 1a) handle HTTP errors or missing body
                try {
                    if (!resp.isSuccessful() || resp.body() == null) {
                        Log.w("WeatherDatahelper", "Place query HTTP " + resp.code());
                        doFallback();
                        return;
                    }
                    xml = resp.body().string();
                } catch (IOException e) {
                    Log.w("WeatherDatahelper", "Place query read failed", e);
                    doFallback();
                    return;
                }

                // 1b) handle OWS ExceptionReport
                if (xml.contains("<ExceptionReport")) {
                    Log.w("WeatherDatahelper", "Place query returned ExceptionReport");
                    doFallback();
                    return;
                }

                // 2) if already in fallback, parse multipointcoverage XML
                if (didFallback) {
                    ParameterResult t2 = XmlWeatherParser.parseWithTimestamp(xml, "TA_PT1H_AVG");
                    if (!"-".equals(t2.value)) {
                        ParameterResult s2 = XmlWeatherParser.parseWithTimestamp(xml, "WAWA_PT1H_RANK");
                        ParameterResult r2 = XmlWeatherParser.parseWithTimestamp(xml, "PRA_PT1H_ACC");
                        int sym2 = 1;
                        try { sym2 = Math.max(1, Math.round(Float.parseFloat(s2.value))); } catch(Exception ignored){}
                        int icon2 = ctx.getResources()
                                .getIdentifier("fmi_" + sym2, "drawable", ctx.getPackageName());
                        listener.onWeatherDataReceived(t2.value, icon2, r2.value);
                    } else {
                        listener.onError("Ei säätietoja saatavilla");
                    }
                    return;
                }

                // 3) normal place-query parsing
                ParameterResult t = XmlWeatherParser.parseTimeValuePair(xml, "t2m");
                if (!"-".equals(t.value)) {
                    ParameterResult s = XmlWeatherParser.parseTimeValuePair(xml, "wawa");
                    ParameterResult r = XmlWeatherParser.parseTimeValuePair(xml, "pra");
                    int sym = 1;
                    try { sym = Math.max(1, Math.round(Float.parseFloat(s.value))); } catch(Exception ignored){}
                    int icon = ctx.getResources()
                            .getIdentifier("fmi_" + sym, "drawable", ctx.getPackageName());
                    listener.onWeatherDataReceived(t.value, icon, r.value);
                    return;
                }

                // 4) no place-data → fallback
                doFallback();
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
                LatLng c = geocodeMunicipality(ctx, municipality);
                if (c != null) {
                    String llUrl = BASE + WeatherQueryBuilder.buildByLatLon(c.latitude, c.longitude);
                    Log.d("WeatherDatahelper", "Weather→latlon fallback: " + llUrl);
                    api.getCurrentWeather(llUrl).enqueue(this);
                } else {
                    listener.onError("Geocode failed for " + municipality);
                }
            }
        });

        // 2) AIR-QUALITY (unchanged)
        String airUrl = WeatherQueryBuilder.buildAirQualityQuery(stationId);
        api.getAirQuality(airUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull retrofit2.Response<ResponseBody> resp) {
                try {
                    String xml = resp.body().string();
                    Map<String, String> labels = new HashMap<>();
                    labels.put("QBCPM25_PT1H_AVG", "PM₂.₅ ");
                    labels.put("QBCPM10_PT1H_AVG", "PM₁₀ ");
                    labels.put("NO2_PT1H_avg", "NO₂ ");
                    labels.put("O3_PT1H_avg", "O₃ ");
                    labels.put("SO2_PT1H_avg", "SO₂ ");
                    labels.put("CO_PT1H_avg", "CO ");
                    labels.put("NO_PT1H_avg", "NO ");
                    labels.put("AQINDEX_PT1H_avg", "Ilmanlaatu-indeksi");
                    labels.put("AQINDEXCLASS_PT1H_avg", "Ilmanlaatu-luokka");
                    labels.put("AQINDEXTEXT_PT1H_avg", "Ilmanlaatu-kuvaus");

                    List<String> tags = List.of(
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
                    listener.onError("Ilmanlaadun käsittely epäonnistui");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                listener.onError("Ilmanlaadun haku epäonnistui: " + t.getMessage());
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
            Log.e("WeatherDatahelper", "Geocode failed for " + mun, e);
        }
        return null;
    }

    private static class LatLng {
        final double latitude, longitude;
        LatLng(double lat, double lon) { latitude = lat; longitude = lon; }
    }
}

