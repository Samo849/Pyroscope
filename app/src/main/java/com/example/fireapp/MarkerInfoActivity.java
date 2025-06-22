package com.example.fireapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fireapp.R;

public class MarkerInfoActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_SNIPPET = "snippet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);

        TextView titleTextView = findViewById(R.id.marker_title);
        TextView snippetTextView = findViewById(R.id.marker_snippet);
        findViewById(R.id.close_button).setOnClickListener(v -> finish());

        // Retrieve data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            titleTextView.setText(extras.getString(EXTRA_TITLE));
            snippetTextView.setText(extras.getString(EXTRA_SNIPPET));
        }

        // Set window size to 90% of screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int dialogWidth = (int) (displayMetrics.widthPixels * 0.9);
        getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // Helper method to create an Intent for starting this Activity
    public static Intent newIntent(Context context, String title, String snippet) {
        Intent intent = new Intent(context, MarkerInfoActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_SNIPPET, snippet);
        return intent;
    }
}