package com.example.fireapp;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fireapp.databinding.ActivityMainBinding;
import com.example.fireapp.models.sensorDataModel;
import com.example.fireapp.ui.dashboard.DashboardFragment;
import com.example.fireapp.ui.home.HomeFragment;
import com.example.fireapp.ui.notifications.NotificationsFragment;
import com.example.fireapp.models.satelliteFireModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    TextView data;
    TextView jsonTextView;
    String url;
    JSONArray fires2023; // Add a field for the JSON
    public List<sensorDataModel> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.dashBoard) {
                replaceFragment(new DashboardFragment());
            } else if (itemId == R.id.notifications) {
                replaceFragment(new NotificationsFragment());
            }

            return true;
        });


        data = findViewById(R.id.data);
        //jsonTextView = findViewById(R.id.local_json);

        url = "http://lukamali.com/ttn2value/data/70B3D57ED0070837.json";

        // Load the JSON file from server
        getDataFromServer();

        // Load the JSON file from assets
        List<satelliteFireModel> fires = loadFires2023WithGson();
        if (fires != null && !fires.isEmpty()) {
            satelliteFireModel firstFire = fires.get(0);

            //jsonTextView.setText(firstFire.acq_date + " " + firstFire.latitude);
        }
    }

    public List<satelliteFireModel> loadFires2023WithGson() {
        try {
            InputStream inputStream = getAssets().open("fires2023.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            return gson.fromJson(jsonString, new TypeToken<List<satelliteFireModel>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getDataFromServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            sensorDataModel newSensorData = parseSensorData(response);
                            if (newSensorData != null) {
                                updateSensorDataList(newSensorData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }

        );
        Volley.newRequestQueue(this).add(request);
    }

    public sensorDataModel parseSensorData(JSONObject response) {
        try {
            // Extract data from JSON
            JSONObject endDeviceIds = response.getJSONObject("end_device_ids");
            String deviceId = endDeviceIds.getString("device_id");
            String applicationId = endDeviceIds.getJSONObject("application_ids").getString("application_id");

            String timeOfDetection = response.getString("received_at");

            JSONObject uplinkMessage = response.getJSONObject("uplink_message");
            String signalQuality = uplinkMessage.getJSONArray("rx_metadata")
                    .getJSONObject(0)
                    .getString("snr");

            String data = uplinkMessage.getJSONObject("decoded_payload").getString("data");

            JSONObject location = uplinkMessage.getJSONArray("rx_metadata")
                    .getJSONObject(0)
                    .getJSONObject("location");
            double latitude = location.getDouble("latitude");
            double longitude = location.getDouble("longitude");

            // Create and return the sensorDataModel object
            return new sensorDataModel(deviceId, applicationId, timeOfDetection, signalQuality, data, latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void updateSensorDataList(sensorDataModel newSensorData) {
        boolean sensorExists = false;

        for (int i = 0; i < sensorDataList.size(); i++) {
            sensorDataModel existingSensor = sensorDataList.get(i);
            if (existingSensor.deviceId.equals(newSensorData.deviceId)) {
                // Update the existing sensor data
                sensorDataList.set(i, newSensorData);
                sensorExists = true;
                break;
            }
        }

        if (!sensorExists) {
            // Add new sensor data
            sensorDataList.add(newSensorData);
        }

        // Debugging or UI update
        for (sensorDataModel sensor : sensorDataList) {
            System.out.println("Device ID: " + sensor.deviceId + ", Data: " + sensor.data);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }



    public boolean isRecent(String isoTimestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant messageTime = Instant.parse(isoTimestamp);
            Instant now = Instant.now();
            Duration duration = Duration.between(messageTime, now);
            // Check if the duration is less than 1 hour
            return !duration.isNegative() && duration.toHours() < 1;
        }
        return false;
    }
}