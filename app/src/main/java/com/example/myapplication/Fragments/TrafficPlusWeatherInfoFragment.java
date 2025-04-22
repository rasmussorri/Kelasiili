package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.apiServices.WeatherApiService;
import com.example.myapplication.utilities_plus_helpers.ApiServiceBuilder;
import com.example.myapplication.utilities_plus_helpers.MunicipalityDataHelper;
import com.example.myapplication.utilities_plus_helpers.StationHelper;
import com.example.myapplication.utilities_plus_helpers.TrafficCameraHelper;
import com.example.myapplication.utilities_plus_helpers.WeatherDatahelper;
import com.example.myapplication.utilities_plus_helpers.WeatherQueryBuilder;
import com.example.myapplication.utilities_plus_helpers.XmlWeatherParser;

import java.util.List;




public class TrafficPlusWeatherInfoFragment extends Fragment {


    private static final String ARG_MUNICIPALITY_NAME = "municipalityName";
    private static final String year = "2023"; // Vuosi, jota käytetään tietojen hakemiseen

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
        String municipalityName = getArguments() != null ? getArguments().getString(ARG_MUNICIPALITY_NAME) : null;
        titleTextView.setText("Kelikamerat: " + municipalityName.toUpperCase());

        fetchTrafficCameraImages(municipalityName, view);
        fetchWeatherAndAirQuality(municipalityName, view);


        Log.d("TrafficFrag", "fetchWeatherAndAirQuality for " + municipalityName);



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

    private void fetchWeatherAndAirQuality(String municipalityName, View view) {

        Log.d("TrafficFrag", "fetchWeatherAndAirQuality for " + municipalityName);

        TextView weatherTextView = view.findViewById(R.id.weatherTextView);
        TextView airQualityTextView = view.findViewById(R.id.airQualityText);

        StationHelper.fetchStationData(requireContext(), municipalityName, new StationHelper.StationListener() {
            @Override
            public void onStationSelected(String stationId) {
                // Now that we have the stationId, kick off both calls via your helper:
                requireActivity().runOnUiThread(() -> {
                    WeatherDatahelper.fetchWeatherAndAirQuality(municipalityName, stationId, new WeatherDatahelper.WeatherListener() {
                        @Override
                        public void onWeatherDataReceived(String temp, String symbol) {
                            requireActivity().runOnUiThread(() ->
                                    weatherTextView.setText("Temp: " + temp + " °C"));
                        }

                        @Override
                        public void onAirQualityDataReceived(String pm25, String pm10, String no2, String o3,
                                                             String so2, String co, String no,
                                                             String aqIndex, String aqIndexClass, String aqIndexText) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("PM2.5: ").append(pm25).append("\n");
                            sb.append("PM10: ").append(pm10).append("\n");
                            sb.append("NO2: ").append(no2).append("\n");
                            sb.append("O3: ").append(o3).append("\n");
                            sb.append("SO2: ").append(so2).append("\n");
                            sb.append("CO: ").append(co).append("\n");
                            sb.append("NO: ").append(no).append("\n");
                            sb.append("Air Quality Index: ").append(aqIndex).append("\n");
                            sb.append("AQI Class: ").append(aqIndexClass).append("\n");
                            sb.append("AQI Description: ").append(aqIndexText).append("\n");
                            Log.d("Fragment", "Air Quality Data: " + sb.toString());

                            requireActivity().runOnUiThread(() ->
                                    airQualityTextView.setText(sb.toString())
                            );
                        }

                        @Override
                        public void onError(String errorMessage) {
                            weatherTextView.setText(errorMessage);
                            airQualityTextView.setText(errorMessage);
                        }
                    });
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() ->
                        airQualityTextView.setText(String.format("Asemaa ei löytynyt: %s", error))
                );
            }
        });
    }
}


