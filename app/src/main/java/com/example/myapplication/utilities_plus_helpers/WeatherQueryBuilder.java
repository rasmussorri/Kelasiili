package com.example.myapplication.utilities_plus_helpers;

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
            // UTF-8 pitää aina toimia, mutta varotoimenpiteenä:
            Log.w("WeatherQueryBuilder", "UTF-8 encode failed, using raw: " + municipality, e);
            placeEncoded = municipality;
        }

        // Kootaan kysely käsin, jotta '::' säilyy tallennettuna:
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

    // Rakentaa kyselyn koordinaateilla, jos paikkakunnalta ei löydy sääasemaa
    // Voisi tehdä pelkästään tällä mutta ei luoteta androidin geokooderiin
    public static String buildByLatLon(double lat, double lon) {
        Log.d("WeatherQueryBuilder", "Tehtiin fallback kysely koordinaateilla:" + "?service=WFS"
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
