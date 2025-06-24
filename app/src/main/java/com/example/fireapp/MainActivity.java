package com.example.fireapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.content.res.AssetManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.fireapp.models.classificatonsModel;
import com.example.fireapp.models.sensorDataModel;
import com.example.fireapp.ui.dashboard.DashboardFragment;
import com.example.fireapp.ui.home.HomeFragment;
import com.example.fireapp.ui.notifications.NotificationsFragment;
import com.example.fireapp.models.satelliteFireModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.example.fireapp.utils.utils;

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

    JSONArray fires2023; // Add a field for the JSON

    private boolean isFirstRun = true;

    private Handler handler = new Handler();
    private Runnable periodicTask;

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



        //jsonTextView = findViewById(R.id.local_json);




        // Load the JSON file from assets
        List<satelliteFireModel> fires = loadFires2023WithGson();
        if (fires != null && !fires.isEmpty()) {
            satelliteFireModel firstFire = fires.get(0);

            //jsonTextView.setText(firstFire.acq_date + " " + firstFire.latitude);
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            // Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        // TextView tokenView = findViewById(R.id.token);
                        // tokenView.setText(token);
                        // Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        // get it once at startup
        String url = "http://lukamali.com/ttn2value/data/70B3D57ED0070837.json";
        String url2 = "http://lukamali.com/ttn2value/data/70B3D57ED0071075.json";

        getDataFromServer(url);
        getDataFromServer(url2);



        // periodic check every 60 seconds
        startPeriodicCheck();

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
            return gson.fromJson(jsonString, new TypeToken<List<satelliteFireModel>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void getDataFromServer(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            sensorDataModel newSensorData = utils.parseSensorData(response);
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

    private void updateSensorDataList(sensorDataModel newSensorData) {
        boolean sensorExists = false;

        for (int i = 0; i < sensorDataList.size(); i++) {
            sensorDataModel existingSensor = sensorDataList.get(i);


            if (existingSensor.deviceId.equals(newSensorData.deviceId)) {
                // Update the existing sensor data
                newSensorData.marker = existingSensor.marker;

                sensorDataList.set(i, newSensorData);
                sensorExists = true;
                break;
            }
        }

        if (!sensorExists) {
            // Add new sensor data
            sensorDataList.add(newSensorData);
        }
        // check here if fire is detected
        if(newSensorData.data.fire > 0.5 && !isFirstRun) {

            // Show a toast message
            // Toast.makeText(this, "Fire detected by sensor: " + newSensorData.deviceId, Toast.LENGTH_LONG).show();

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).showMarkerInfo(newSensorData.marker);
            }

            // TODO: check if its recent, also check if user already saw this warning

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


    private void startPeriodicCheck() {
        periodicTask = new Runnable() {
            @Override
            public void run() {
                if (isFirstRun) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFirstRun = false;
                            periodicTask.run();
                        }
                    }, 2 * 1000);
                } else {
                    String url = "http://lukamali.com/ttn2value/data/70B3D57ED0070837.json";
                    String url2 = "http://lukamali.com/ttn2value/data/70B3D57ED0071075.json";
                    getDataFromServer(url);
                    getDataFromServer(url2);
                    handler.postDelayed(this, 120 * 1000);
                    // update snippets, etc.
                }
            }
        };
        handler.post(periodicTask);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(periodicTask);
    }

}