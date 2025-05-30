package com.example.fireapp.models;

import com.google.android.gms.maps.model.Marker;

public class sensorDataModel {
    public String deviceId;
    public String applicationId;
    public String timeOfDetection;
    public String signalQuality;
    public classificatonsModel data;
    public double latitude;
    public double longitude;
    public Marker marker;

    public sensorDataModel(String deviceId, String applicationId, String timeOfDetection, String signalQuality, classificatonsModel data, Double latitude, Double longitude) {
        this.deviceId = deviceId;
        this.applicationId = applicationId;
        this.timeOfDetection = timeOfDetection;
        this.signalQuality = signalQuality;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
        this.marker = null; // Initialize marker to null
    }
}