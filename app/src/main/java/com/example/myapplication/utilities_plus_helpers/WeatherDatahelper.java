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
        void onAirQualityDataReceived(String jotain, String jottaa);  //TODO
        void onError(String errorMessage);


    }

    public static void fetchWeatherData(String municipality, WeatherListener listener) {

        WeatherApiService api = ApiServiceBuilder.createService(WeatherApiService.class, "https://opendata.fmi.fi/wfs");
        String weatherQuery = WeatherQueryBuilder.buildWeatherQuery(municipality);
        String airQualityQuery = WeatherQueryBuilder.buildAirQualityQuery(municipality);

        api.getCurrentWeather(weatherQuery).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String xml = response.body().string();
                    Log.d("FMI_XML", xml);
                    String temperature = XmlWeatherParser.parse(xml, "Temperature");
                    String symbol = XmlWeatherParser.parse(xml, "WeatherSymbol3");
                    listener.onWeatherDataReceived(temperature, symbol);
                } catch (Exception e) {
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
                    String something = XmlWeatherParser.parse(xml, "PM10"); //TODO
                    String somethingElse = XmlWeatherParser.parse(xml, "PM2.5"); //TODO
                    listener.onAirQualityDataReceived(something, somethingElse);
                    Log.d("AirQuality", "Ilmanlaadun tiedot: " + something + ", " + somethingElse);
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
