package com.example.myapplication.core.util;

import java.lang.ref.WeakReference;
import java.text.Normalizer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.myapplication.airquality.AirQualityStation;
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
        WeakReference<StationListener> listenerRef = new WeakReference<>(listener);
        Handler main = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("airQualityStationsList.JSON"), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
                r.close();

                Type listType = new TypeToken<List<AirQualityStation>>(){}.getType();
                List<AirQualityStation> stations = new Gson().fromJson(sb.toString(), listType);

                String normalizedMunicipality = normalize(municipality);
                List<String> stationIds = new ArrayList<>();
                for (AirQualityStation s : stations) {
                    if (normalize(s.getMunicipality()).equalsIgnoreCase(normalizedMunicipality)) {
                        stationIds.add(s.getStationID());
                        Log.d("StationHelper", "Löydettiin asema: " + s.getStationID());
                    }
                }

                StationListener l = listenerRef.get();
                if (l != null) {
                    main.post(() -> {
                        if (stationIds.isEmpty()) {
                            l.onError("Ilmanlaatuasemaa ei löydy kunnalle \"" + municipality + "\"");
                        } else {
                            l.onStationSelected(stationIds);
                        }
                    });
                }
            } catch (Exception e) {
                StationListener l = listenerRef.get();
                if (l != null) {
                    new Handler(Looper.getMainLooper()).post(
                            () -> l.onError("StationHelper error: " + e.getMessage())
                    );
                }
            }
        }).start();
    }


    private static String normalize(String input) {
        // Poistaa diakriittiset merkit ja normalisoi merkkijonon
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}

