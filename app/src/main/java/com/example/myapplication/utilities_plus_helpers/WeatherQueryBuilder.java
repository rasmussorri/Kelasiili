package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeatherQueryBuilder {

    private static final String BASE_URL = "https://opendata.fmi.fi/wfs";

    public static String buildWeatherQuery(String municipality) {
        String place;
        try {
            place = URLEncoder.encode(municipality, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF‑8 pitäisi aina olla tuettu – tässä kohtaa voi valita jonkin fallbackin
            place = municipality;
        }
        Log.d("WeatherQueryBuilder",BASE_URL
                + "?service=WFS"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::weather::hourly::simple"
                + "&place=" + place
                + "&parameters=TA_PT1H_AVG,WAWA_PT1H_RANK,PRA_PT1H_ACC"
                + "&format=text/xml");
        return BASE_URL
                + "?service=WFS"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::weather::hourly::simple"
                + "&place=" + place
                + "&parameters=TA_PT1H_AVG,WAWA_PT1H_RANK,PRA_PT1H_ACC"
                + "&format=text/xml";

    }

    public static String buildAirQualityQuery(String fmisid) {
        Log.d("WeatherQueryBuilder", "FMISID: " + fmisid);
        Log.d("WeatherQueryBuilder", BASE_URL
                + "?service=WFS"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::airquality::hourly::simple"
                + "&fmisid=" + fmisid);
        return BASE_URL
                + "?service=WFS"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::airquality::hourly::simple"
                + "&fmisid=" + fmisid;
    }

}
