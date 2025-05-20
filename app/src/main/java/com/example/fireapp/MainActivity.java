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
import com.example.fireapp.ui.dashboard.DashboardFragment;
import com.example.fireapp.ui.home.HomeFragment;
import com.example.fireapp.ui.notifications.NotificationsFragment;
import com.example.fireapp.models.FireModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    TextView data;
    TextView jsonTextView;
    String url;
    JSONArray fires2023; // Add a field for the JSON

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
        List<FireModel> fires = loadFires2023WithGson();
        if (fires != null && !fires.isEmpty()) {
            FireModel firstFire = fires.get(0);

            //jsonTextView.setText(firstFire.acq_date + " " + firstFire.latitude);
        }
    }

    private List<FireModel> loadFires2023WithGson() {
        try {
            InputStream inputStream = getAssets().open("fires2023.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            return gson.fromJson(jsonString, new TypeToken<List<FireModel>>(){}.getType());
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
                            JSONObject jsonData = response.getJSONObject("uplink_message");

                            // Parse the JSON response
                            String decodedPayloadStr = jsonData.getString("decoded_payload");
                            JSONObject decodedPayload = new JSONObject(decodedPayloadStr);


                            // get message:
                            String someValue = decodedPayload.getString("data");
                            // get time:
                            String receivedAt = jsonData.getString("received_at");

                            if (isRecent(receivedAt)) {
                                data.setText("Recent: " + someValue + "\n" + jsonData.getString("received_at"));
                            } else {
                                data.setText("Not recent: " + someValue + "\n" + jsonData.getString("received_at"));
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