package com.example.fireapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.DisplayMetrics;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.fireapp.R;

public class MarkerInfoDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_SNIPPET = "snippet";

    public static MarkerInfoDialog newInstance(String title, String snippet) {
        MarkerInfoDialog fragment = new MarkerInfoDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SNIPPET, snippet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_marker_info, container, false);

        TextView titleTextView = view.findViewById(R.id.marker_title);
        TextView snippetTextView = view.findViewById(R.id.marker_snippet);
        view.findViewById(R.id.close_button).setOnClickListener(v -> dismiss());

        if (getArguments() != null) {
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            snippetTextView.setText(getArguments().getString(ARG_SNIPPET));
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Get screen width and set dialog width to 90% of screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int dialogWidth = (int) (displayMetrics.widthPixels * 0.9); // 90% of screen width

            getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}