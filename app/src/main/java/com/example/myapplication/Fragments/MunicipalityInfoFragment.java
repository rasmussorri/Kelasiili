package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class MunicipalityInfoFragment extends Fragment {

    // KOVAKOODATTU VUOSI PITÄÄ PÄIVITTÄÄ MANUAALISESTI
    private String year = "2023";



    public MunicipalityInfoFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_municipality_info, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    // replikoidaan tilastokeskuksen vaatimaa JSON-muotoa tekemällä sitä vastaava oma query
    private JsonObject buildQuery( String municipalityCode, String dataType){

        // Objekti vuodelle
        JsonObject query = new JsonObject();

        JsonArray years = new JsonArray();
        years.add(year);

        JsonObject selection = new JsonObject();
        selection.addProperty("filter", "item");
        selection.add("values", years);

        JsonObject yearQuery = new JsonObject();
        yearQuery.addProperty("code", "Vuosi");
        yearQuery.add("selection", selection);

        // Objekti kunnalle

        JsonArray municipality = new JsonArray();
        municipality.add(municipalityCode);

        JsonObject municipalitySelection = new JsonObject();
        municipalitySelection.addProperty("filter", "item");
        municipalitySelection.add("values", municipality);

        JsonObject municipalityQuery = new JsonObject();
        municipalityQuery.addProperty("code", "Alue");
        municipalityQuery.add("selection", municipalitySelection);

        // Objekti haetulle tiedolle
        JsonArray data = new JsonArray();
        data.add(dataType);

        JsonObject dataSelection = new JsonObject();
        dataSelection.addProperty("filter", "item");
        dataSelection.add("values", data);

        JsonObject dataQuery = new JsonObject();
        dataQuery.addProperty("code", "Tiedot");
        dataQuery.add("selection", dataSelection);

        // Lista koko kyselylle

        JsonArray queryList = new JsonArray();
        queryList.add(yearQuery);
        queryList.add(municipalityQuery);
        queryList.add(dataQuery);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("format", "json-stat2");

        // Objekti koko kyselylle
        query.add("query", queryList);
        query.add("response", responseObject);



        return query;
    }
}