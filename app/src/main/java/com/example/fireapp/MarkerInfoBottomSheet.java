package com.example.fireapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fireapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MarkerInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_SNIPPET = "snippet";

    public static MarkerInfoBottomSheet newInstance(String title, String snippet) {
        MarkerInfoBottomSheet fragment = new MarkerInfoBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SNIPPET, snippet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marker_info_bottom_sheet, container, false);

        TextView titleTextView = view.findViewById(R.id.marker_title);
        TextView snippetTextView = view.findViewById(R.id.marker_snippet);

        if (getArguments() != null) {
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            snippetTextView.setText(getArguments().getString(ARG_SNIPPET));
        }

        return view;
    }
}