package com.grassrootsreporting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;


public class ConfirmSubmission extends AppCompatActivity {

    private final String TAG = "ConfirmSubmission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_submission);

        String submitOutcome = getIntent().getExtras().getString("submitOutcome");
        String reportStr = getIntent().getExtras().getString("reportStr");
        String summaryStr = getIntent().getExtras().getString("summaryStr");
        String locationStr = getIntent().getExtras().getString("locationStr");
        String gpsStr = getIntent().getExtras().getString("gpsStr");
        String timestampStr = getIntent().getExtras().getString("timestampStr");

        TextView headerTextView = (TextView) findViewById(R.id.headingTextViewId);
        TextView summaryTextView = (TextView) findViewById(R.id.summaryTextViewId);
        TextView locationTextView = (TextView) findViewById(R.id.locationTextViewId);
        TextView gpsTextView = (TextView) findViewById(R.id.gpsTextViewId);
        TextView timestampTextView = (TextView) findViewById(R.id.timestampTextViewId);

        headerTextView.setText("Submission:\n" + reportStr + "\n" + submitOutcome);
        summaryTextView.setText(summaryStr);
        locationTextView.setText(locationStr);
        gpsTextView.setText(gpsStr);
        timestampTextView.setText(timestampStr);

        Button mainScreenButton = (Button) findViewById(R.id.mainScreenButton);
        mainScreenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
