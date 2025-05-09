package com.example.myapplication.traffic_weather.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.airquality.AirQualityData;
import com.example.myapplication.R;
import com.example.myapplication.core.util.StationHelper;
import com.example.myapplication.core.util.TrafficCameraHelper;
import com.example.myapplication.core.util.WeatherDatahelper;
import com.example.myapplication.core.util.XmlWeatherParser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TrafficPlusWeatherInfoFragment extends Fragment {

    private static final String ARG_MUNICIPALITY_NAME = "municipalityName";

    public static TrafficPlusWeatherInfoFragment newInstance(String municipalityName) {
        TrafficPlusWeatherInfoFragment fragment = new TrafficPlusWeatherInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MUNICIPALITY_NAME, municipalityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traffic_weather_info, container, false);
        TextView titleTextView = view.findViewById(R.id.municipalityNameTextView2);
        TextView airQualityTextView = view.findViewById(R.id.airQualityInfoTextView);
        String municipalityName = getArguments() != null ? getArguments().getString(ARG_MUNICIPALITY_NAME) : "";
        titleTextView.setText("Kelikamerat: " + municipalityName.toUpperCase());
        airQualityTextView.setText("Ilmanlaatutiedot (tunnin keskiarvo)");

        fetchTrafficCameraImages(municipalityName, view);
        setupWeatherAndAirQuality(municipalityName, view);

        Log.d("TrafficFrag", "Municipality:" + municipalityName);
        return view;
    }

    private void fetchTrafficCameraImages(String municipalityName, View view) {
        LinearLayout cameraContainer = view.findViewById(R.id.cameraImageContainer);
        TrafficCameraHelper.fetchCamerasByMunicipality(municipalityName, new TrafficCameraHelper.TrafficCameraCallback() {
            @Override
            public void onResult(List<String> cameraImageUrls) {
                for (String url : cameraImageUrls) {
                    ImageView imageView = new ImageView(requireContext());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    imageView.setAdjustViewBounds(true);
                    Glide.with(requireContext()).load(url).into(imageView);
                    cameraContainer.addView(imageView);
                }
            }

            @Override
            public void onError(String error) {
                TextView errorTextView = new TextView(requireContext());
                errorTextView.setText("Virhe kamerakuvissa: " + error);
                cameraContainer.addView(errorTextView);
            }
        });
    }

    private void setupWeatherAndAirQuality(String municipalityName, View view) {
        TextView weatherTextView = view.findViewById(R.id.weatherTextView);
        TextView airQualityTextView = view.findViewById(R.id.airQualityText);
        ImageView weatherIcon = view.findViewById(R.id.weatherImageView);

        StationHelper.fetchStationData(requireContext(), municipalityName, new StationHelper.StationListener() {
            @Override
            public void onStationSelected(List<String> stationIds) {
                if (stationIds.isEmpty()) {
                    stationIds = Collections.singletonList("");
                }
                tryNextStation(0, stationIds, municipalityName, weatherTextView, airQualityTextView, weatherIcon);
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() ->
                        airQualityTextView.setText("Asemaa ei löytynyt: " + errorMessage)
                );
                Log.w("StationHelper", "Air quality station not found, fetching only weather data");
                // Set an empty value for station so we can still fetch weather data
                tryNextStation(0,
                        Collections.singletonList(""),
                        municipalityName,
                        weatherTextView,
                        airQualityTextView,
                        weatherIcon);
            }
        });
    }

    private void tryNextStation(int index,
                                List<String> stationIds,
                                String municipalityName,
                                TextView weatherTextView,
                                TextView airQualityTextView,
                                ImageView weatherIcon) {
        if (index >= stationIds.size()) {
            requireActivity().runOnUiThread(() ->
                    airQualityTextView.setText("Ei dataa saatavilla tällä hetkellä")
            );
            return;
        }
        String stationId = stationIds.get(index);
        WeatherDatahelper.fetchWeatherAndAirQuality(requireContext(),municipalityName, stationId, new WeatherDatahelper.WeatherListener() {
            @Override
            public void onWeatherDataReceived(String temp, int symbol, String rain) {
                requireActivity().runOnUiThread(() -> {
                    if (symbol != 0) {
                        weatherIcon.setImageResource(symbol);
                    }

                    String display = String.format("Lämpötila: %s °C", temp);
                    // Rain value if available
                    if (rain != null && !rain.isEmpty()) {
                        int at = rain.indexOf('@');
                        if (at >= 0) {
                            String rainValue = rain.substring(0, at).trim();
                            String rainTime = rain.substring(at + 1).trim();
                            display += String.format("\nSademäärä tunnin ajalta: %s mm @ %s", rainValue, rainTime);
                        }
                    }
                    weatherTextView.setText(display);
                });
            }

            @Override
            public void onAirQualityDataReceived(AirQualityData airData) {
                if (hasAnyData(airData)) {
                    displayAirQuality(airData, airQualityTextView);
                } else {
                    tryNextStation(index + 1, stationIds, municipalityName, weatherTextView, airQualityTextView, weatherIcon);
                }
            }

            @Override
            public void onError(String errorMessage) {
                tryNextStation(index + 1, stationIds, municipalityName, weatherTextView, airQualityTextView, weatherIcon);
            }
        });
    }

    private boolean hasAnyData(AirQualityData airData) {
        for (XmlWeatherParser.ParameterResult pr : airData.getValues().values()) {
            if (!"-".equals(pr.value)) return true;
        }
        return false;
    }

    private void displayAirQuality(AirQualityData airData, TextView airQualityTextView) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, XmlWeatherParser.ParameterResult> entry : airData.getValues().entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().value)
                    .append(" @ ")
                    .append(entry.getValue().time)
                    .append("\n");
        }
        requireActivity().runOnUiThread(() ->
                airQualityTextView.setText(sb.toString())
        );
    }
}
