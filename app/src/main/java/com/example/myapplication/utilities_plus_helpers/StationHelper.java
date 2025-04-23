package com.example.myapplication.utilities_plus_helpers;

import java.text.Normalizer;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.AirQualityStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StationHelper {

    public interface StationListener {
        void onStationSelected(List<String> stationIds);
        void onError(String errorMessage);

    }

    public static void fetchStationData(Context context, String municipality, StationListener listener) {

        new Thread(() -> {
            try {

                // luetaan JSON tiedosto
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(context.getAssets()
                                .open("airQualityStationsList.JSON"), "UTF-8"));
                Log.d("HÖÖ", "höö");
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
                r.close();

                // 2) Parse into List<AirQualityStation>
                Type listType = new TypeToken<List<AirQualityStation>>(){}.getType();
                List<AirQualityStation> stations = new Gson()
                        .fromJson(sb.toString(), listType);

                String normalizedMunicipality = normalize(municipality);
                List<String> stationIds = new ArrayList<>();

                // 3) Find a matching municipality (case‑insensitive)
                for (AirQualityStation s : stations) {

                    String muni = normalize(s.getMunicipality());
                    if (muni.equalsIgnoreCase(normalizedMunicipality)) {
                        stationIds.add(s.getStationID());
                        Log.d("StationHelper", "Löydettiin asema: " + s.getStationID());
                    }
                }
                if (stationIds.isEmpty()) {
                    listener.onError(
                            "Ilmanlaatuasemaa ei löydy kunnalle \"" + municipality + "\""
                    );
                } else {
                    listener.onStationSelected(stationIds);
                }




            } catch (Exception e) {
                Log.e("StationHelper", "Asemat JSON listaa ei saatu avattua", e);
                listener.onError("StationHelper error: " + e.getMessage());
            }
        }).start();
    }


    private static String normalize(String input) {
        // Poistaa diakriittiset merkit ja normalisoi merkkijonon
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}

