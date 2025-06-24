package com.example.fireapp.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fireapp.MainActivity;
import com.example.fireapp.R;
import com.example.fireapp.databinding.FragmentHomeBinding;
import com.example.fireapp.models.satelliteFireModel;
import com.example.fireapp.models.sensorDataModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    GoogleMap mMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Fetch the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Fires from assets
        List<satelliteFireModel> fires = ((MainActivity) requireActivity()).loadFires2023WithGson();
        if (fires != null) {
            for (satelliteFireModel fire : fires) {
                String formattedTime = fire.acq_time.length() == 4
                        ? fire.acq_time.substring(0, 2) + ":" + fire.acq_time.substring(2)
                        : fire.acq_time;

                LatLng fireLocation = new LatLng(fire.latitude, fire.longitude);
                String snippet = "Brightness: " + fire.brightness +
                        "\nDate: " + fire.acq_date +
                        "\nTime: " + formattedTime +
                        "\nDetected by Satellite: " + fire.satellite +
                        "\nConfidence: " + fire.confidence + "%";

                mMap.addMarker(new MarkerOptions()
                        .position(fireLocation)
                        .title("Fire ID: " + fire.id)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        }

        // Adding sensor markers
        List<sensorDataModel> sensors = ((MainActivity) requireActivity()).sensorDataList;
        LatLng sensorLocation = new LatLng(46.12, 14.31); // Default location for the camera
        if (sensors != null) {
            for (sensorDataModel sensor : sensors) {
                sensorLocation = new LatLng(sensor.latitude, sensor.longitude);
                String snippet = createSnippet(sensor);

                String title = "Sensor data";
                if (sensor.data.fire > 0.5) {
                    title = "🔥New Fire detected!🔥";
                }
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(sensorLocation)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                sensor.marker = marker;
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sensorLocation, 12));

        // Manually added markers
        LatLng sensor03 = new LatLng(46.12, 14.31);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 120, true);
        mMap.addMarker(new MarkerOptions()
                .position(sensor03)
                .title("Marker of sensor 03")
                .snippet("Blue default marker")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));

        mMap.setOnMarkerClickListener(marker -> {
            // Show MarkerInfoActivity with marker info
            showMarkerInfo(marker);
            return true;
        });
    }

    public void updateSnippets(List<sensorDataModel> sensors) {
        for (sensorDataModel sensor : sensors) {
            if (sensor.marker != null) {
                String snippet = createSnippet(sensor);
                sensor.marker.setSnippet(snippet);
            }
        }
    }

    private String createSnippet(sensorDataModel sensor) {
        return "Device ID: " + sensor.deviceId +
                "\nApplication ID: " + sensor.applicationId +
                "\nTime: " + sensor.timeOfDetection +
                "\nSignal Quality: " + sensor.signalQuality +
                "\n" +
                "\nFire: " + sensor.data.fire + "%" +
                "\nNormal: " + sensor.data.normal + "%" +
                "\nWind: " + sensor.data.wind + "%" +
                "\nRain: " + sensor.data.rain + "%";
    }

    private void createMarker(Marker marker) {
        if (marker == null) {
            LatLng defaultLocation = new LatLng(46.12, 14.31); // Default location for the camera
            marker = mMap.addMarker(new MarkerOptions()
                    .position(defaultLocation)
                    .title("Default Marker")
                    .snippet("This is a default marker")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    public void showMarkerInfo(Marker marker) {
        if (marker == null) {
            createMarker(marker);
        }

        // Start MarkerInfoActivity instead of showing a DialogFragment
        Intent intent = com.example.fireapp.ui.home.MarkerInfoActivity.newIntent(requireContext(), marker.getTitle(), marker.getSnippet());
        startActivity(intent);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}