package com.example.fireapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fireapp.R;
import com.example.fireapp.databinding.FragmentHomeBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;

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
        GoogleMap mMap = googleMap;

        // Add markers
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Additional information here"));

        LatLng newYork = new LatLng(40.7128, -74.0060);
        googleMap.addMarker(new MarkerOptions().position(newYork).title("Marker in New York").snippet("Additional information here"));

        LatLng tokyo = new LatLng(35.6895, 139.6917);
        googleMap.addMarker(new MarkerOptions().position(tokyo).title("Marker in Tokyo").snippet("Additional information here"));

        // Move the camera to the first marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Show BottomSheetDialogFragment with marker info
                com.example.fireapp.ui.home.MarkerInfoBottomSheet bottomSheet = com.example.fireapp.ui.home.MarkerInfoBottomSheet.newInstance(
                        marker.getTitle(),
                        marker.getSnippet()
                );
                bottomSheet.show(getChildFragmentManager(), "MarkerInfoBottomSheet");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));

                return true; // Return true to consume the click event
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}