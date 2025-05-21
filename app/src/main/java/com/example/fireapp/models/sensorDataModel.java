package com.example.fireapp.models;

public class sensorDataModel {
    public String deviceId;
    public String applicationId;
    public String timeOfDetection;
    public String signalQuality;
    public String data;
    public double latitude;
    public double longitude;

    public sensorDataModel(String deviceId, String applicationId, String timeOfDetection, String signalQuality, String data, double latitude, double longitude) {
        this.deviceId = deviceId;
        this.applicationId = applicationId;
        this.timeOfDetection = timeOfDetection;
        this.signalQuality = signalQuality;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}