package com.example.myapplication.utilities_plus_helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MunicipalityQueryBuilder {

    // replikoidaan tilastokeskuksen vaatimaa JSON-muotoa tekemällä sitä vastaava oma query
    public static JsonObject buildQuery(String municipalityCode, String dataType, String year){

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
