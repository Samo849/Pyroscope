package com.example.fireapp.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fireapp.MainActivity;
import com.example.fireapp.R;
import com.example.fireapp.models.sensorDataModel;
import com.example.fireapp.ui.dashboard.DashboardFragment;

public class NotificationsFragment extends Fragment {

    private LinearLayout notificationContainer;
    private ScrollView scrollView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationContainer = root.findViewById(R.id.notification_container);
        scrollView = root.findViewById(R.id.scroll_view);

        // Retrieve sensor data from MainActivity
        for (sensorDataModel sensor : ((MainActivity) requireActivity()).sensorDataList) {
            if (sensor.data.fire > 0.5) {
                String message = "Fire Warning: High fire risk detected at Sensor ID: " + sensor.getId();
                addFireWarning(message);
            }
        }

        // Hint at scrollability with a slight scroll animation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (scrollView != null) {
                scrollView.smoothScrollBy(0, 100); // Scroll down slightly
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        scrollView.smoothScrollBy(0, -100), 500); // Scroll back up
            }
        }, 1000);

        return root;
    }

    private void addFireWarning(String message) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardView = inflater.inflate(R.layout.notification_card, notificationContainer, false);

        // Set message
        TextView textView = cardView.findViewById(R.id.notification_message);
        textView.setText(message);

        // Set up buttons
        Button dismissButton = cardView.findViewById(R.id.dismiss_button);
        Button dashboardButton = cardView.findViewById(R.id.dashboard_button);

        // Swipe animation for dismiss
        dismissButton.setOnClickListener(v -> {
            cardView.animate()
                    .translationX(cardView.getWidth())
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> notificationContainer.removeView(cardView))
                    .start();
        });

        // Navigate to DashboardFragment
        dashboardButton.setOnClickListener(v -> {
            // Replace with DashboardFragment
            Fragment dashboardFragment = new DashboardFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, dashboardFragment)
                    .commit();

            // Update BottomNavigationView to highlight Dashboard
            if (requireActivity().findViewById(R.id.bottomNavigationView) != null) {
                requireActivity().findViewById(R.id.bottomNavigationView)
                        .findViewById(R.id.dashBoard)
                        .performClick();
            }
        });

        // Add card to container
        notificationContainer.addView(cardView);
    }
}