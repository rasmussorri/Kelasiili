package com.example.myapplication.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplication.municipality.model.MunicipalityInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchedMunicipalitiesManager {

    private static final int MAX_HISTORY_SIZE = 5;
    private static final List<MunicipalityInfo> searchedList = new ArrayList<>();

    private static final String PREF_NAME = "SearchHistoryPrefs";
    private static final String KEY_HISTORY = "search_history";

    public static void addMunicipality(MunicipalityInfo info) {
        for (MunicipalityInfo existing : searchedList) {
            if (existing.getName().equalsIgnoreCase(info.getName())) {
                return;
            }
        }
        searchedList.add(0,info);

        if (searchedList.size() > MAX_HISTORY_SIZE) {
            searchedList.remove(searchedList.size() - 1);
        }
    }

    public static List<MunicipalityInfo> getAll() {
        return new ArrayList<>(searchedList);
    }

    public static void clear() {
        searchedList.clear();
    }

    public static void saveToPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(searchedList);
        editor.putString(KEY_HISTORY, json);
        editor.apply();
    }

    public static void loadFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MunicipalityInfo>>() {}.getType();
            List<MunicipalityInfo> loaded = gson.fromJson(json, type);

            searchedList.clear();
            searchedList.addAll(loaded);
        }
    }

    public static void removeMunicipality(MunicipalityInfo info) {
        searchedList.removeIf(m -> m.getName().equalsIgnoreCase(info.getName()));
    }
}
