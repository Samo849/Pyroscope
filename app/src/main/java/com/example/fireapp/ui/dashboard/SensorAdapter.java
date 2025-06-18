package com.example.fireapp.ui.dashboard;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fireapp.R;
import com.example.fireapp.models.DashboardItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    private List<DashboardItem> itemList;
    private List<DashboardItem> fullList;
    private int expandedPosition = RecyclerView.NO_POSITION;
    Button showOnMapButton;

    public SensorAdapter(List<DashboardItem> items) {
        this.itemList = new ArrayList<>(items);
        this.fullList = new ArrayList<>(items);

    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        DashboardItem item = itemList.get(position);
        holder.deviceId.setText(item.getType() + " ID: " + item.getId());
        holder.data.setText(item.getDisplayData());

        // Prepare details text
        StringBuilder details = new StringBuilder();
        if (item instanceof com.example.fireapp.models.sensorDataModel) {
            com.example.fireapp.models.sensorDataModel sensor = (com.example.fireapp.models.sensorDataModel) item;
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
        holder.detailsText.setText(details.toString());

        final boolean isExpanded = position == expandedPosition;
        holder.detailsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            int oldExpanded = expandedPosition;
            if (isExpanded) {
                expandedPosition = RecyclerView.NO_POSITION;
            } else {
                expandedPosition = holder.getAdapterPosition();
            }
            notifyItemChanged(oldExpanded);
            notifyItemChanged(expandedPosition);
        });

        if (item instanceof com.example.fireapp.models.sensorDataModel) {
            com.example.fireapp.models.sensorDataModel sensor = (com.example.fireapp.models.sensorDataModel) item;
            if (sensor.data.fire > 0.5) {
                holder.cardView.setBackgroundResource(R.drawable.pulse_red);
                AnimationDrawable animation = (AnimationDrawable) holder.cardView.getBackground();
                animation.setEnterFadeDuration(400);
                animation.setExitFadeDuration(400);
                animation.start();
                // Remove card background color so animation is visible
                holder.cardView.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
                holder.cardView.setStrokeColor(
                        holder.itemView.getContext().getResources().getColor(R.color.red_stroke, null)
                );


            } else {
                holder.cardView.setBackgroundResource(0);
                // Reset to XML color
                holder.cardView.setStrokeColor(
                        holder.itemView.getContext().getResources().getColor(R.color.grey_stroke, null)
                );
            }
        } else {
            holder.cardView.setBackgroundResource(0);
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(R.color.sensor_card_background_color, null)
            );
            holder.cardView.setStrokeColor(
                    holder.itemView.getContext().getResources().getColor(R.color.grey_stroke, null)
            );
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filter(String query, String filterType) {
        itemList.clear();
        for (DashboardItem item : fullList) {
            boolean matchesQuery = item.getId().toLowerCase().contains(query.toLowerCase());
            boolean matchesFilter = filterType.equals("All") || item.getType().equals(filterType);
            if (matchesQuery && matchesFilter) {
                itemList.add(item);
            }
        }
        // Sort: sensors with fire > 0.5 at the top if filter is All or Sensor
        if (filterType.equals("All") || filterType.equals("Sensor")) {
            itemList.sort((a, b) -> {
                boolean aIsHot = a instanceof com.example.fireapp.models.sensorDataModel &&
                        ((com.example.fireapp.models.sensorDataModel) a).data.fire > 0.5;
                boolean bIsHot = b instanceof com.example.fireapp.models.sensorDataModel &&
                        ((com.example.fireapp.models.sensorDataModel) b).data.fire > 0.5;
                if (aIsHot == bIsHot) return 0;
                return aIsHot ? -1 : 1;
            });
        }
        notifyDataSetChanged();
    }
    static class SensorViewHolder extends RecyclerView.ViewHolder {

        TextView deviceId, data, detailsText;
        View detailsLayout;
        MaterialCardView cardView;

        SensorViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.sensor_card);
            deviceId = itemView.findViewById(R.id.sensor_device_id);
            data = itemView.findViewById(R.id.sensor_data);
            detailsLayout = itemView.findViewById(R.id.details_layout);
            detailsText = itemView.findViewById(R.id.details_text);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DashboardItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}