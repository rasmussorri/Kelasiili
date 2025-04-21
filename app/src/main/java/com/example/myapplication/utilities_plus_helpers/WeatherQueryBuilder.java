package com.example.myapplication.utilities_plus_helpers;

public class WeatherQueryBuilder {

    private static final String BASE_URL = "https://opendata.fmi.fi/wfs";

    public static String buildWeatherQuery(String municipality) {
        return BASE_URL +
                "?request=getFeature" +
                "&storedquery_id=fmi::observations::weather::hourly::simple" +
                "&place=" + municipality +
                "&parameters=TA_PT1H_AVG,WAWA_PT1H_RANK" +
                "&format=text/xml";
    }

    public static String buildAirQualityQuery(String municipality) {
        return BASE_URL +
                "?request=getFeature" +
                "&storedquery_id=fmi::observations::airquality::hour::simple" +
                "&place=" + municipality +
                "&parameters=PM10,PM2.5" +
                "&format=text/xml";
    }

}
