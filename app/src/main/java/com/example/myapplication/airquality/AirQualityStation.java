package com.example.myapplication.airquality;

import com.google.gson.annotations.SerializedName;

public class AirQualityStation {
    @SerializedName("municipality")
    private String Municipality;

    // Was not used but could be added later if relevant
    @SerializedName("stationName")
    private String StationName;

    // Serialized so Gson knows to find the correct field from JSON-file
    @SerializedName("fmisid")
    private String StationID;

    public AirQualityStation(String municipality, String stationName, String stationID) {
        this.Municipality = municipality;
        this.StationName = stationName;
        this.StationID = stationID;
    }

    public String getMunicipality() {
        return Municipality;
    }

    public String getStationID() {
        return StationID;
    }
}
