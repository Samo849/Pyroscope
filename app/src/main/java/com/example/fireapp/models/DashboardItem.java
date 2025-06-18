package com.example.fireapp.models;

public interface DashboardItem {
    String getId();
    String getType(); // "Sensor" or "Satellite"
    String getDisplayData();
}