package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        TextView weatherTextView = view.findViewById(R.id.weatherTextView);
        TextView airQualityTextView = view.findViewById(R.id.airQualityText);

        // 1) First: get the FMIS station ID in a background thread
        StationHelper.fetchStationData(requireContext(), municipalityName, new StationHelper.StationListener() {
            @Override
            public void onStationSelected(String stationId) {
                // stationId is a String like "101004"
                // Now we must switch back to the UI thread to kick off Retrofit & touch views:
                requireActivity().runOnUiThread(() -> {
                    // 2a) Weather call
                    WeatherApiService api = ApiServiceBuilder
                            .createService(WeatherApiService.class, "https://opendata.fmi.fi/wfs");
                    String weatherUrl = WeatherQueryBuilder.buildWeatherQuery(municipalityName);
                    api.getCurrentWeather(weatherUrl).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> c, Response<ResponseBody> r) {
                            try {
                                String xml = r.body().string();
                                String temp = XmlWeatherParser.parse(xml, "Temperature");
                                weatherTextView.setText("Temp: " + temp + " °C");
                            } catch (Exception e) {
                                weatherTextView.setText("Säädatan käsittely epäonnistui");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> c, Throwable t) {
                            weatherTextView.setText("Virhe sään haussa: " + t.getMessage());
                        }
                    });

                    // 2b) Air‑quality call (now passing stationId, not the municipality)
                    String aqUrl = WeatherQueryBuilder.buildAirQualityQuery(stationId);
                    api.getAirQuality(aqUrl).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> c, Response<ResponseBody> r) {
                            try {
                                String xml = r.body().string();
                                String pm10 = XmlWeatherParser.parse(xml, "PM10");
                                String pm25 = XmlWeatherParser.parse(xml, "PM2.5");
                                airQualityTextView.setText(
                                        "PM₁₀: " + pm10 + " µg/m³\n" +
                                                "PM₂.₅: " + pm25 + " µg/m³"
                                );
                            } catch (Exception e) {
                                airQualityTextView.setText("Ilmanlaadun käsittely epäonnistui");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> c, Throwable t) {
                            airQualityTextView.setText("Ilmanlaadun haku epäonnistui: " + t.getMessage());
                        }
                    });
                });
            }

            @Override
            public void onError(String error) {
                // back to UI thread for the error
                requireActivity().runOnUiThread(() ->
                        airQualityTextView.setText("Asemaa ei löytynyt: " + error)
                );
            }
        });
    }
}


