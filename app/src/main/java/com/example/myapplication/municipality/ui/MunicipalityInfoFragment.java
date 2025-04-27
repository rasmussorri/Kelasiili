package com.example.myapplication.municipality.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.municipality.model.MunicipalityInfo;
import com.example.myapplication.core.util.MunicipalityDataHelper;
import com.example.myapplication.core.util.SearchedMunicipalitiesManager;


public class MunicipalityInfoFragment extends Fragment {

    private static final String TAG = MunicipalityInfoFragment.class.getSimpleName();
    private static final String ARG_MUNICIPALITY_NAME = "municipalityName";
    private static final String year = "2023"; // Year for data fetching
    private TextView populationTextView;
    private TextView changeTextView;
    private TextView municipalityNameTextView;
    private TextView employmentRateTextView;
    private TextView selfRelianceRateTextView;

    public static MunicipalityInfoFragment newInstance(String municipalityName) {
        MunicipalityInfoFragment fragment = new MunicipalityInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MUNICIPALITY_NAME, municipalityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_municipality_info, container, false);

        populationTextView = view.findViewById(R.id.populationTextView);
        changeTextView = view.findViewById(R.id.populationChangeTextView);
        municipalityNameTextView = view.findViewById(R.id.municipalityNameTextView);
        employmentRateTextView = view.findViewById(R.id.employmentRateTextView);
        selfRelianceRateTextView = view.findViewById(R.id.jobSelfRelianceTextView);

        // Picks up the municipality name from the arguments
        String municipalityName = getArguments() != null ? getArguments().getString(ARG_MUNICIPALITY_NAME) : null;

        if (municipalityName != null) {
            fetchMunicipalityData(municipalityName, view);
        }

        return view;
    }

    private void fetchMunicipalityData(String municipalityName, View view){

        municipalityNameTextView.setText(municipalityName.toUpperCase()+ " " + year);

        MunicipalityDataHelper municipalityDataHelper = new MunicipalityDataHelper();
        municipalityDataHelper.fetchData(municipalityName, new MunicipalityDataHelper.Listener() {
            @Override
            public void onMunicipalityDataReady(String population, String change, String selfRelianceRate, String employmentRate) {
                // Updates the UI with the fetched data
                populationTextView.setText("Väkiluku: " + population);
                changeTextView.setText("Väkiluvun muutos: " + change);
                employmentRateTextView.setText("Työllisyysaste: " + employmentRate + "%");
                selfRelianceRateTextView.setText("Työpaikkaomavaraisuus: " + selfRelianceRate + "%");

                try {
                    int pop = Integer.parseInt(population);
                    int popChange = Integer.parseInt(change);
                    double selfRel = Double.parseDouble(selfRelianceRate);
                    double empRate = Double.parseDouble(employmentRate);

                    MunicipalityInfo info = new MunicipalityInfo(municipalityName, pop, popChange, selfRel, empRate);
                    SearchedMunicipalitiesManager.addMunicipality(info);
                    SearchedMunicipalitiesManager.saveToPreferences(requireContext()); // Store the data in preferences
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Wrong number format for the municipality " + municipalityName, e);
                    Toast.makeText(requireContext(),
                            "Palvelin palautti epäkelvon luvun: " + population,
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String errorMessage) {
                populationTextView.setText("Virhe: " + errorMessage);

            }
        }, year);
    }
}