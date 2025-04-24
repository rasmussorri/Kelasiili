package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeatherQueryBuilder {

    private static final String BASE_URL = "https://opendata.fmi.fi/wfs";

    // Rakentaa kyselyn paikkakunnan nimen perusteella
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

    // Rakentaa kyselyn koordinaateilla, jos paikkakunnalta ei löydy sääasemaa
    // Voisi tehdä pelkästään tällä mutta en luota androidin geokooderiin
    public static String buildByLatLon(double lat, double lon) {
        return "?service=WFS"
                + "&version=2.0.0"
                + "&request=getFeature"
                + "&storedquery_id=fmi::observations::weather::multipointcoverage"
                + "&latlon=" + lat + "," + lon      // latitude first!
                + "&maxlocations=1"
                + "&parameters=TA_PT1H_AVG,WAWA_PT1H_RANK,PRA_PT1H_ACC";
    }

    // Rakentaa kyselyn ilmanlaatudatan hakemiseksi valmiista asemalistasta
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
