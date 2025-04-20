package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.utilities_plus_helpers.MunicipalityDataHelper;


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

        MunicipalityDataHelper municipalityDataHelper = new MunicipalityDataHelper();
        municipalityDataHelper.fetchPopulationAndChange(municipalityName, new MunicipalityDataHelper.Listener() {
            @Override
            public void onMunicipalityDataReady(String population) {
                // päivittää kunnan väkiluvun
                populationTextView.setText("Väkiluku: " + population);

            }

            @Override
            public void onError(String errorMessage) {
                // Käsittelee virheen
                populationTextView.setText("Virhe: " + errorMessage);

            }
        }, year);
    }



}