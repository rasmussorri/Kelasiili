package com.example.myapplication.utilities_plus_helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MunicipalityQueryBuilder {

    // replikoidaan tilastokeskuksen vaatimaa JSON-muotoa tekemällä sitä vastaava oma query
    public static JsonObject buildQuery(String municipalityCode, String[] dataTypes, String year){

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
        for (String dataType : dataTypes) {
            data.add(dataType);
        }

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

    public static JsonObject buildJobSelfRelianceQuery(String municipalityCode, String year) {
        JsonObject root = new JsonObject();

        // Vuosiobjekti
        JsonArray years = new JsonArray();
        years.add(year);
        JsonObject yearObj = new JsonObject();
        yearObj.addProperty("code", "Vuosi");
        JsonObject yearSel = new JsonObject();
        yearSel.addProperty("filter", "item");
        yearSel.add("values", years);
        yearObj.add("selection", yearSel);

        // Alueobjekti
        JsonArray areas = new JsonArray();
        areas.add(municipalityCode);
        JsonObject areaObj = new JsonObject();
        areaObj.addProperty("code", "Alue");
        JsonObject areaSel = new JsonObject();
        areaSel.addProperty("filter", "item");
        areaSel.add("values", areas);
        areaObj.add("selection", areaSel);

        // Rakentaa queryn
        JsonArray query = new JsonArray();
        query.add(yearObj);
        query.add(areaObj);

        JsonObject response = new JsonObject();
        response.addProperty("format", "json-stat2");

        root.add("query", query);
        root.add("response", response);

        return root;
    }

    public static JsonObject buildEmploymentRateQuery(String municipalityCode, String year) {
        JsonObject root = new JsonObject();

        // Vuosi
        JsonArray years = new JsonArray();
        years.add(year);
        JsonObject yearObj = new JsonObject();
        yearObj.addProperty("code", "Vuosi");
        JsonObject yearSel = new JsonObject();
        yearSel.addProperty("filter", "item");
        yearSel.add("values", years);
        yearObj.add("selection", yearSel);

        // Alue
        JsonArray areas = new JsonArray();
        areas.add(municipalityCode);
        JsonObject areaObj = new JsonObject();
        areaObj.addProperty("code", "Alue");
        JsonObject areaSel = new JsonObject();
        areaSel.addProperty("filter", "item");
        areaSel.add("values", areas);
        areaObj.add("selection", areaSel);

        // Tiedot
        JsonArray tiedot = new JsonArray();
        tiedot.add("tyollisyysaste");
        JsonObject tiedotObj = new JsonObject();
        tiedotObj.addProperty("code", "Tiedot");
        JsonObject tiedotSel = new JsonObject();
        tiedotSel.addProperty("filter", "item");
        tiedotSel.add("values", tiedot);
        tiedotObj.add("selection", tiedotSel);

        // Koko kysely
        JsonArray query = new JsonArray();
        query.add(areaObj);
        query.add(yearObj);
        query.add(tiedotObj);

        JsonObject response = new JsonObject();
        response.addProperty("format", "json-stat2");

        root.add("query", query);
        root.add("response", response);

        return root;
    }

}
