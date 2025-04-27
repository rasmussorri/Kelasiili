package com.example.myapplication.core.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeatherQueryBuilder {

    private static final String BASE_URL = "https://opendata.fmi.fi/wfs";

    public static String buildWeatherQuery(String municipality) {
        String placeEncoded;
        try {
            placeEncoded = URLEncoder.encode(municipality, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            Log.w("WeatherQueryBuilder", "UTF-8 encode failed, using raw: " + municipality, e);
            placeEncoded = municipality;
        }


        String query =
                "storedquery_id=fmi::observations::weather::timevaluepair" +
                        "&service=WFS" +
                        "&version=2.0.0" +
                        "&request=getFeature" +
                        "&place=" + placeEncoded +
                        "&timestep=60" +
                        "&parameters=t2m,wawa,r_1h";

        String url = BASE_URL + "?" + query;
        Log.d("WeatherQueryBuilder", "Built URL: " + url);
        return url;
    }

    // Builds the query with coordinates if the name doesn't work
    // Could be done only with this but trust issues for the geocoder
    public static String buildByLatLon(double lat, double lon) {
        Log.d("WeatherQueryBuilder", "Fallback query:" + "?service=WFS"
                + "&version=2.0.0"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::weather::multipointcoverage"
                + "&latlon=" + lat + "," + lon
                + "&maxlocations=1"
                + "&parameters=TA_PT1H_AVG,WAWA_PT1H_RANK,PRA_PT1H_ACC");
        return "?service=WFS"
                + "&version=2.0.0"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::weather::multipointcoverage"
                + "&latlon=" + lat + "," + lon
                + "&maxlocations=1"
                + "&parameters=TA_PT1H_AVG,WAWA_PT1H_RANK,PRA_PT1H_ACC";
    }

    // Builds the query using the list of FMI station IDs in assets dir
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
