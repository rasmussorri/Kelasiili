package com.example.myapplication;

import com.example.myapplication.utilities_plus_helpers.XmlWeatherParser.ParameterResult;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AirQualityData {
    private final Map<String, ParameterResult> values;

    public AirQualityData(Map<String, ParameterResult> values) {
        // Suojataan arvot, ettei niitä vahingossa muokata luomisen jälkeen
        this.values = Collections.unmodifiableMap(values);
    }

    public ParameterResult get(String tagName) {
        return values.get(tagName);
    }

    public Set<String> tagNames() {
        return values.keySet();
    }
    public Map<String, ParameterResult> getValues() {
        return values;
    }
}
