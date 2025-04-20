package com.example.myapplication.utilities_plus_helpers;

public class WeatherDatahelper {

    public interface WeatherListener{
        void onWeatherDataReceived(String weatherData, String airQualityData);
        void onError(String errorMessage);


    }

    public void fetchWeatherData(String municipality, WeatherListener listener) {

        public void on


        if (weatherData != null && airQualityData != null) {
            listener.onWeatherDataReceived(weatherData, airQualityData);
        } else {
            listener.onError("Failed to fetch weather data");
        }
    }
}
