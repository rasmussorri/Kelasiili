package com.example.myapplication.utilities_plus_helpers;

import com.example.myapplication.dataModels.MunicipalityInfo;
import java.util.ArrayList;
import java.util.List;

public class SearchedMunicipalitiesManager {

    private static final List<MunicipalityInfo> searchedList = new ArrayList<>();

    public static void addMunicipality(MunicipalityInfo info) {
        // Optional: avoid duplicates
        for (MunicipalityInfo existing : searchedList) {
            if (existing.getName().equalsIgnoreCase(info.getName())) {
                return;
            }
        }
        searchedList.add(info);
    }

    public static List<MunicipalityInfo> getAll() {
        return new ArrayList<>(searchedList); // defensive copy
    }

    public static void clear() {
        searchedList.clear();
    }

    public static boolean isEmpty() {
        return searchedList.isEmpty();
    }
}
