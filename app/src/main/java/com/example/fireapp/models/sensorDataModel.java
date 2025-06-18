package com.example.fireapp.models;

import android.os.Build;

import com.google.android.gms.maps.model.Marker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class sensorDataModel implements DashboardItem {
    public String deviceId;
    public String applicationId;
    public String timeOfDetection;
    public String signalQuality;
    public classificatonsModel data;
    public double latitude;
    public double longitude;
    public Marker marker;

    @Override
    public String getId() {
        return deviceId;
    }

    @Override
    public String getType() {
        return "Sensor";
    }

    @Override
    public String getDisplayData() {
        return "Fire: " + data.fire + "%, Normal: " + data.normal + "%";
    }
    public sensorDataModel(String deviceId, String applicationId, String timeOfDetection, String signalQuality, classificatonsModel data, Double latitude, Double longitude) {
        this.deviceId = deviceId;
        this.applicationId = applicationId;
        this.timeOfDetection = formatTimestamp(timeOfDetection);
        this.signalQuality = signalQuality;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
        this.marker = null; // Initialize marker to null
    }

    public static String formatTimestamp(String isoTimestamp) {
        Instant instant = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = Instant.parse(isoTimestamp);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            return formatter.format(instant);
        }
        return isoTimestamp;
    }
}