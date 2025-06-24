// app/src/main/java/com/example/fireapp/ui/dashboard/DashboardFragment.java
package com.example.fireapp.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fireapp.MainActivity;
import com.example.fireapp.R;
import com.example.fireapp.models.DashboardItem;
import com.example.fireapp.models.sensorDataModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private SensorAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        EditText searchBar = view.findViewById(R.id.search_bar);
        Spinner filterSpinner = view.findViewById(R.id.filter_spinner);
        RecyclerView recyclerView = view.findViewById(R.id.sensor_recycler_view);

        String[] filters = {"All", "Sensor", "Satellite"};
        filterSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, filters));

        List<DashboardItem> items = new ArrayList<>();
        items.addAll(((MainActivity) requireActivity()).sensorDataList);
        items.addAll(((MainActivity) requireActivity()).loadFires2023WithGson());

        adapter = new SensorAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString(), filterSpinner.getSelectedItem().toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        filterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                adapter.filter(searchBar.getText().toString(), filters[position]);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        adapter.setOnItemClickListener(item -> {
            // Show a dialog with all relevant data
            StringBuilder details = new StringBuilder();
            details.append("Type: ").append(item.getType()).append("\n");
            details.append("ID: ").append(item.getId()).append("\n");
            details.append(item.getDisplayData()).append("\n");

            if (item instanceof sensorDataModel) {
                sensorDataModel sensor = (sensorDataModel) item;
                details.append("Application ID: ").append(sensor.applicationId).append("\n");
                details.append("Time: ").append(sensor.timeOfDetection).append("\n");
                details.append("Signal: ").append(sensor.signalQuality).append("\n");
                details.append("Lat: ").append(sensor.latitude).append("\n");
                details.append("Lon: ").append(sensor.longitude).append("\n");
            } else if (item instanceof com.example.fireapp.models.satelliteFireModel) {
                com.example.fireapp.models.satelliteFireModel sat = (com.example.fireapp.models.satelliteFireModel) item;
                details.append("Brightness: ").append(sat.brightness).append("\n");
                details.append("Date: ").append(sat.acq_date).append("\n");
                details.append("Time: ").append(sat.acq_time).append("\n");
                details.append("Lat: ").append(sat.latitude).append("\n");
                details.append("Lon: ").append(sat.longitude).append("\n");
                details.append("Satellite: ").append(sat.satellite).append("\n");
                details.append("Instrument: ").append(sat.instrument).append("\n");
                details.append("Confidence: ").append(sat.confidence).append("\n");
                details.append("FRP: ").append(sat.frp).append("\n");
                details.append("Day/Night: ").append(sat.daynight).append("\n");
            }

            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Details")
                    .setMessage(details.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        return view;
    }
}