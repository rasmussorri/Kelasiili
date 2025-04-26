package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.dataModels.MunicipalityInfo;
import com.example.myapplication.utilities_plus_helpers.MunicipalityDataHelper;
import com.example.myapplication.utilities_plus_helpers.SearchedMunicipalitiesManager;


public class MunicipalityInfoFragment extends Fragment {


    private static final String ARG_MUNICIPALITY_NAME = "municipalityName";
    private static final String year = "2023"; // Vuosi, jota käytetään tietojen hakemiseen

    public static MunicipalityInfoFragment newInstance(String municipalityName) {
        MunicipalityInfoFragment fragment = new MunicipalityInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MUNICIPALITY_NAME, municipalityName);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_municipality_info, container, false);
        // Hakee kunnan nimen argumenteista

        String municipalityName = getArguments() != null ? getArguments().getString(ARG_MUNICIPALITY_NAME) : null;

        if (municipalityName != null) {
            fetchMunicipalityData(municipalityName, view);
        }

        return view;
    }

    private void fetchMunicipalityData(String municipalityName, View view){
        TextView populationTextView = view.findViewById(R.id.populationTextView);
        TextView changeTextView = view.findViewById(R.id.populationChangeTextView);
        TextView municipalityNameTextView = view.findViewById(R.id.municipalityNameTextView);
        TextView employmentRateTextView = view.findViewById(R.id.employmentRateTextView);
        TextView selfRelianceRateTextView = view.findViewById(R.id.jobSelfRelianceTextView);

        municipalityNameTextView.setText(municipalityName.toUpperCase()+ " " + year);

        MunicipalityDataHelper municipalityDataHelper = new MunicipalityDataHelper();
        municipalityDataHelper.fetchData(municipalityName, new MunicipalityDataHelper.Listener() {
            @Override
            public void onMunicipalityDataReady(String population, String change, String selfRelianceRate, String employmentRate) {
                // päivittää kunnan tiedot
                populationTextView.setText("Väkiluku: " + population);
                changeTextView.setText("Väkiluvun muutos: " + change);
                employmentRateTextView.setText("Työllisyysaste-%: " + employmentRate);
                selfRelianceRateTextView.setText("Työpaikkaomavaraisuus-%: " + selfRelianceRate);

                try {
                    int pop = Integer.parseInt(population);
                    int popChange = Integer.parseInt(change);
                    double selfRel = Double.parseDouble(selfRelianceRate);
                    double empRate = Double.parseDouble(employmentRate);

                    MunicipalityInfo info = new MunicipalityInfo(municipalityName, pop, popChange, selfRel, empRate);
                    SearchedMunicipalitiesManager.addMunicipality(info);
                    SearchedMunicipalitiesManager.saveToPreferences(requireContext()); // Tallenna tiedot kunnan tietojen päivityksen jälkeen
                } catch (NumberFormatException e) {
                    // Optional: Log or handle invalid data formats
                    e.printStackTrace();
                }

            }


            @Override
            public void onError(String errorMessage) {
                // Käsittelee virheen
                populationTextView.setText("Virhe: " + errorMessage);

            }
        }, year);
    }
}