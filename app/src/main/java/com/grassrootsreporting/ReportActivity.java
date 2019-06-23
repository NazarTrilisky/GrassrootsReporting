package com.grassrootsreporting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.location.LocationListener;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class ReportActivity extends AppCompatActivity {

    private final static String TAG = "ReportActivity";
    private MainActivity.IssueType report_type;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final static int LOCATION_UPDATE_INTERVAL_MS = 2000;
    private TextView gpsCoordinatesTextView;
    private TextView titleTextView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private List<Bitmap> photoList = new ArrayList<Bitmap>();
    private Map<String, String> reportDetails = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        gpsCoordinatesTextView = (TextView) findViewById(R.id.gpsCoordinatesTextViewId);

        // Hide progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setProgress(0);  // bug in updating progress bar visibility: this forces refresh

        // Report type
        String report_type_str = getIntent().getExtras().getString("report_type");
        report_type = MainActivity.IssueType.valueOf(report_type_str);
        update_title_of_report(report_type);

        // Timestamp
        String timeStampStr = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss z").format(new Date());
        TextView timestampTextView = (TextView) findViewById(R.id.requestCreatedTimeTextViewId);
        timestampTextView.setText(timeStampStr);

        // GPS pre-populate location
        updateCurrentLocation();

        // Take a photo button listener
        Button takePhotoButton = (Button) findViewById(R.id.takePhotoButtonId);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { takePhoto(); }
        });

        // Clear photos button listener
        Button clearPhotosButton = (Button) findViewById(R.id.clearPhotosButtonId);
        clearPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoList.clear();
                refreshPhotoView();
            }
        });

        // Submit button listener
        Button submitButton = (Button) findViewById(R.id.submitReportButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show progress bar
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                LinearLayout.LayoutParams progBarParams = new LinearLayout.LayoutParams(150, 150);
                progBarParams.gravity = Gravity.CENTER;
                progressBar.setLayoutParams(progBarParams);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);  // bug in updating progress bar visibility: this forces refresh

                // Scroll to top so progress bar can be seen
                ScrollView reportScrollView = (ScrollView) findViewById(R.id.reportActivityScrollView);
                reportScrollView.scrollTo(0, reportScrollView.getTop());
                reportScrollView.invalidate();

                // Push report data to the server
                // Without the delayed start, the progress bar does not appear right away
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onSubmitClick();
                    }
                }, 50);

            }
        });

        // Cancel button listener
        Button cancelButton = (Button) findViewById(R.id.cancelReportButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainScreen();
            }
        });
    }


    /**
     * Take a photo with the phone camera
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**
     * Get the photo from the phone's camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoList.add(imageBitmap);
            refreshPhotoView();
        }
    }


    /**
     * Refresh thumbnails of taken photos
     */
     private void refreshPhotoView() {
        LinearLayout photosLinearLayout = findViewById(R.id.photosLinearLayoutId);
        photosLinearLayout.removeAllViews();
        for(Bitmap photo : photoList)
        {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.public_transportation);
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(150,200));
            imageView.setMaxHeight(20);
            imageView.setMaxWidth(20);
            imageView.setImageBitmap(photo);
            photosLinearLayout.addView(imageView);
        }
    }


    private void backToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * Update the "Location" edit text field with the current GPS reading
     */
    private void updateCurrentLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsCoordinatesTextView.setText(location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "status changed to " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled provider = " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled provider = " + provider);
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        // ask for Permission to use GPS if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 1);
                return;
            } else {
                locationManager.requestLocationUpdates("gps", LOCATION_UPDATE_INTERVAL_MS,
                        0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates("gps", LOCATION_UPDATE_INTERVAL_MS,
                    0, locationListener);
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult requestCode = " + requestCode);
        Log.d(TAG, "permissions = " + permissions + ", grantReults = " + grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates("gps", LOCATION_UPDATE_INTERVAL_MS,
                            0, locationListener);
                }
                return;
        }
    }

    public void onSubmitClick() {
        // Update reportDetails with the user's entries
        updateReportDetails();

        // Post results to the server
        final String DB_URL = this.getString(R.string.server_url_and_port);
        PostDataToServer postDataObj = new PostDataToServer(reportDetails, photoList);
        String submitOutcome = "FAILURE: UNINITIALIZED!";
        try {
            submitOutcome = postDataObj.execute(DB_URL).get();
        } catch (ExecutionException exec_exc) {
            Log.e(TAG, "Execution exception: " + exec_exc);
        } catch (InterruptedException int_exc) {
            Log.e(TAG, "Interrupt exception: " + int_exc);
        }
        Log.i(TAG, "Result  of HTTP operation = " + submitOutcome);

        // Show either confirmation or failure to user
        if (submitOutcome == "SUCCESS") {
            startConfirmSubmissionActivity("Success");
        } else {
            startConfirmSubmissionActivity("Failure");
        }
    }

    private void updateReportDetails() {
        EditText summaryEditText = (EditText) findViewById(R.id.issueSummaryEditTextId);
        EditText locationEditText = (EditText) findViewById(R.id.locationEditTextId);
        TextView timestampTextView = (TextView) findViewById(R.id.requestCreatedTimeTextViewId);
        TextView gpsCoordinatesTextView = (TextView) findViewById(R.id.gpsCoordinatesTextViewId);

        reportDetails.put("reportTitle", titleTextView.getText().toString());
        reportDetails.put("summaryStr", summaryEditText.getText().toString());
        reportDetails.put("locationStr", locationEditText.getText().toString());
        reportDetails.put("timestampStr", timestampTextView.getText().toString());
        reportDetails.put("gpsCoordinates", gpsCoordinatesTextView.getText().toString());
    }

    public void startConfirmSubmissionActivity(String submitOutcome) {
        Intent intent = new Intent(this, ConfirmSubmission.class);
        intent.putExtra("submitOutcome", submitOutcome);
        intent.putExtra("reportStr", reportDetails.get("reportTitle"));
        intent.putExtra("summaryStr", reportDetails.get("summaryStr"));
        intent.putExtra("locationStr", reportDetails.get("locationStr"));
        intent.putExtra("timestampStr", reportDetails.get("timestampStr"));
        intent.putExtra("gpsStr", reportDetails.get("gpsCoordinates"));
        Log.d(TAG, "Sending intent with extras: " + intent.getExtras().toString());
        startActivity(intent);
    }

    /**
     * Pre-sets the title of the report page to
     * the type of report, e.g. Criminal Activity or Waste and Environment
     */
    private void update_title_of_report(MainActivity.IssueType report_type) {
        titleTextView = (TextView) findViewById(R.id.projTitleTextViewId);
        switch(report_type) {
            case ROADS_DRIVERS:
                titleTextView.setText("Report: Roads and Drivers");
                break;
            case WATER_GAS:
                titleTextView.setText("Report: Water and Gas");
                break;
            case WASTE_ENVIRONMENT:
                titleTextView.setText("Report: Waste and Environment");
                break;
            case CRIMINAL_ACTIVITY:
                titleTextView.setText("Report: Criminal Activity");
                break;
            case ELECTRICITY:
                titleTextView.setText("Report: Electrical Issues");
                break;
            case EMERGENCY_SERVICES:
                titleTextView.setText("Report: Emergency Services");
                break;
            case PUBLIC_TRANSPORTATION:
                titleTextView.setText("Report: Public Transportation");
                break;
            case PUBLIC_EDUCATION:
                titleTextView.setText("Report: Public Education");
                break;
            default:
                titleTextView.setText("Report an Issue");
        }
    }
}
