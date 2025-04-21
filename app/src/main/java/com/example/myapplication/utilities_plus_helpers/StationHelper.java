package com.example.myapplication.utilities_plus_helpers;

import android.content.Context;

import com.example.myapplication.AirQualityStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class StationHelper {

    public interface StationListener {
        void onStationSelected(String stationName);
        void onError(String errorMessage);

    }

    public static void fetchStationData(Context context, String municipality, StationListener listener) {
        // Simulate fetching station data
        new Thread(() -> {
            try {
                // luetaan JSON tiedosto
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(context.getAssets()
                                .open("air_quality_stations_list.json"), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
                r.close();

                // 2) Parse into List<AirQualityStation>
                Type listType = new TypeToken<List<AirQualityStation>>(){}.getType();
                List<AirQualityStation> stations = new Gson()
                        .fromJson(sb.toString(), listType);

                // 3) Find a matching municipality (case‑insensitive)
                for (AirQualityStation s : stations) {
                    if (s.getMunicipality().equalsIgnoreCase(municipality.trim())) {
                        listener.onStationSelected(s.getStationID());
                        return;
                    }
                }
                listener.onError("Ei löydy asemaa kunnalle “" + municipality + "”");
            } catch (Exception e) {
                listener.onError("StationHelper error: " + e.getMessage());
            }
        }).start();
    }
}

