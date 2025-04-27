package com.example.myapplication.airquality;

import com.example.myapplication.core.util.XmlWeatherParser.ParameterResult;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AirQualityData {
    private final Map<String, ParameterResult> values;

    public AirQualityData(Map<String, ParameterResult> values) {
        // Protecting the values so they cannot be modified after creation
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
