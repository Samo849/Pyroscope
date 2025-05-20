package com.example.fireapp.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        LatLng senzor01 = new LatLng(46.0928, 14.3416);
        mMap.addMarker(new MarkerOptions()
                        .position(senzor01)
                        .title("Marker of sensor 01")
                        .snippet("Default color marker")
                // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)) // Default is RED
        );

        LatLng sensor02 = new LatLng(46.0954, 14.3383);
        mMap.addMarker(new MarkerOptions()
                .position(sensor02)
                .title("Marker of sensor 02")
                .snippet("Blue default marker")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))); // Or HUE_BLUE, HUE_CYAN, etc.

        LatLng sensor03 = new LatLng(46.12, 14.31);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 120, true);
        mMap.addMarker(new MarkerOptions()
                .position(sensor03)
                .title("Marker of sensor 03")
                .snippet("Blue default marker")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));

        // Move the camera to the first marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(senzor01, 12));

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