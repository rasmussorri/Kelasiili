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
import com.example.myapplication.utilities_plus_helpers.MunicipalityDataHelper;
import com.example.myapplication.utilities_plus_helpers.TrafficCameraHelper;
import com.example.myapplication.utilities_plus_helpers.WeatherDatahelper;

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
        fetchWeather(municipalityName, view);


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

    private void fetchWeather(String municipalityName, View view){
        TextView weatherTextView = view.findViewById(R.id.weatherTextView);


        WeatherDatahelper.fetchWeatherData(municipalityName, new WeatherDatahelper.WeatherListener() {


            @Override
            public void onWeatherDataReceived(String weatherData, String symbol) {
                weatherTextView.setText(weatherData);
            }

            @Override
            public void onAirQualityDataReceived(String jotain, String jottaa) {

            }

            @Override
            public void onError(String error) {
                weatherTextView.setText("Virhe sään haussa: " + error);
            }
        });

    }
}


//#######################################################################

/*
package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;


public class TrafficPlusWeatherInfoFragment extends Fragment {



    public TrafficPlusWeatherInfoFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traffic_weather_info, container, false);

        return view;
    }
}
*/