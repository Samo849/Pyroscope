package com.example.fireapp;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fireapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

public class MainActivity extends AppCompatActivity{

    TextView data;
    String url;
    JSONArray fires2023; // Add a field for the JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.fireapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        data = findViewById(R.id.data);
        url = "http://lukamali.com/ttn2value/data/70B3D57ED0070837.json";

        // Load the sloveniaFires2023.json from assets
        fires2023 = loadSloveniaFires2023Json();
        // You can now use sloveniaFires2023Json as needed


        getDataFromServer();
    }

    private JSONArray loadSloveniaFires2023Json() {
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = getAssets().open("fires2023.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // fetch
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            return new JSONArray(jsonString);
        } catch (IOException | JSONException e) {
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
