package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class AirQualityStation {
    @SerializedName("municipality")
    private String Municipality;

    @SerializedName("stationName")
    private String StationName;

    // Serialisoitu jotta Gson tietää minkä niminen kenttä on JSONissa
    @SerializedName("fmisid")
    private String StationID;

    public AirQualityStation(String municipality, String stationName, String stationID) {
        this.Municipality = municipality;
        this.StationName = stationName;
        this.StationID = stationID;
    }

    public void setMunicipality(String municipality) {
        this.Municipality = municipality;
    }
    public void setStationName(String stationName) {
        this.StationName = stationName;
    }
    public void setStationID(String stationID) {
        this.StationID = stationID;
    }
    public String getMunicipality() {
        return Municipality;
    }
    public String getStationName() {
        return StationName;
    }
    public String getStationID() {
        return StationID;
    }
}
