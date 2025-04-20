package com.example.myapplication.utilities_plus_helpers;

public class WeatherQueryBuilder {

    private static final String BASE_URL = "https://opendata.fmi.fi/wfs";

    public static String buildWeatherQuery(String municipality) {
        return BASE_URL +
                "?request=getFeature" +
                "&storedquery_id=fmi::observations::weather::current::simple" +
                "&place=" + municipality +
                "&parameters=temperature,weatherSymbol3" +
                "&timestep=30";
    }

    public static String buildAirQualityQuery(String municipality) {
        return BASE_URL +
                "?request=getFeature" +
                "&storedquery_id=fmi::observations::airquality::hour::simple" +
                "&place=" + municipality +
                "&parameters=PM10,PM2.5";
    }

}
