package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import com.example.myapplication.apiServices.WeatherApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDatahelper {

    public interface WeatherListener{
        void onWeatherDataReceived(String weatherData, String symbol);
        void onAirQualityDataReceived(String pm10, String pm25, String no2,String o3,String so2,String co,String no,String nox,String voc,String nmvoc);  //TODO
        void onError(String errorMessage);


    }

    public static void fetchWeatherAndAirQuality(String municipality,String stationId, WeatherListener listener) {

        WeatherApiService api = ApiServiceBuilder.createService(WeatherApiService.class, "https://opendata.fmi.fi/wfs");
        String weatherQuery = WeatherQueryBuilder.buildWeatherQuery(municipality);
        String airQualityQuery = WeatherQueryBuilder.buildAirQualityQuery(stationId);

        api.getCurrentWeather(weatherQuery).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    listener.onError("Säädatan haku epäonnistui: HTTP " + response.code());
                    return;
                }
                try {
                    String xml = response.body().string();
                    //Log.d("FMI_XML", xml);
                    String temperature = XmlWeatherParser.parse(xml, "Temperature");
                    String symbol = XmlWeatherParser.parse(xml, "WeatherSymbol3");
                    listener.onWeatherDataReceived(temperature, symbol);
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
                    //Log.d("FMI_AIR_QUALITY_XML", xml); // tämä näyttää koko raakadatan

                    String co = XmlWeatherParser.parse(xml, "CO_PT1H_avg");
                    String no2 = XmlWeatherParser.parse(xml, "NO2_PT1H_avg");
                    String o3 = XmlWeatherParser.parse(xml, "O3_PT1H_avg");
                    String pm10 = XmlWeatherParser.parse(xml, "PM10_PT1H_avg");
                    String pm25 = XmlWeatherParser.parse(xml, "PM25_PT1H_avg");
                    String so2 = XmlWeatherParser.parse(xml, "SO2_PT1H_avg");
                    String no = XmlWeatherParser.parse(xml, "NO_PT1H_avg");
                    String aqIndex = XmlWeatherParser.parse(xml, "AQIndex_PT1H_avg");
                    String aqIndexClass = XmlWeatherParser.parse(xml, "AQIndexClass_PT1H_avg");
                    String aqIndexText = XmlWeatherParser.parse(xml, "AQIndexText_PT1H_avg");

                    listener.onAirQualityDataReceived(pm25, pm10, no2, o3, so2, co, no, aqIndex, aqIndexClass, aqIndexText);
                    //Log.d("AirQuality", "Ilmanlaadun tiedot: " + something + ", " + somethingElse);
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
