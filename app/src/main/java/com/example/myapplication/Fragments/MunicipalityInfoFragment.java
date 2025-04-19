package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

public class MunicipalityInfoFragment extends Fragment {

    // KOVAKOODATTU VUOSI PITÄÄ PÄIVITTÄÄ MANUAALISESTI
    private String year = "2023";
    private TextView municipalityNameTextView;
    private TextView populationTextView;
    private TextView populationChangeTextView;
    private TextView jobSelfRelianceTextView;
    private TextView employmentRateTextView;


    public MunicipalityInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        municipalityNameTextView = getView().findViewById(R.id.municipalityNameTextView);
        populationTextView = getView().findViewById(R.id.populationTextView);
        populationChangeTextView = getView().findViewById(R.id.populationChangeTextView);
        jobSelfRelianceTextView = getView().findViewById(R.id.jobSelfRelianceTextView);
        employmentRateTextView = getView().findViewById(R.id.employmentRateTextView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_municipality_info, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    public void updateMunicipalityInfo(String municipalityName, int population, String populationChange, double jobSelfReliance, double employmentRate) {
        municipalityNameTextView.setText(municipalityName);
        populationTextView.setText(String.valueOf(population));
        populationChangeTextView.setText(populationChange);
        jobSelfRelianceTextView.setText(String.valueOf(jobSelfReliance));
        employmentRateTextView.setText(String.valueOf(employmentRate));
    }
}