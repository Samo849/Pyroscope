package com.example.fireapp.models;

public class satelliteFireModel implements DashboardItem {
    public int id;
    public double latitude;
    public double longitude;
    public double brightness;
    public double scan; // changed from int to double
    public double track; // changed from int to double
    public String acq_date;
    public String acq_time; // changed from int to String
    public String satellite;
    public String instrument;
    public int confidence;
    public double version;
    public double bright_t31;
    public double frp;
    public String daynight;
    public int type;

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getType() {
        return "Satellite";
    }

    @Override
    public String getDisplayData() {
        return "Brightness: " + brightness + ", Date: " + acq_date;
    }
}