package com.grassrootsreporting;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.content.Intent;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    enum IssueType {
        ROADS_DRIVERS,
        WATER_GAS,
        WASTE_ENVIRONMENT,
        CRIMINAL_ACTIVITY,
        ELECTRICITY,
        EMERGENCY_SERVICES,
        PUBLIC_TRANSPORTATION,
        PUBLIC_EDUCATION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scaleButtonSizes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void roads_drivers_click(android.view.View view) {
        this.startReportActivity(IssueType.ROADS_DRIVERS.toString());
    }

    public void water_gas_click(android.view.View view) {
        this.startReportActivity(IssueType.WATER_GAS.toString());
    }

    public void waste_environment_click(android.view.View view) {
        Log.i(TAG, "Clicked waste_environment_click");
        this.startReportActivity(IssueType.WASTE_ENVIRONMENT.toString());
    }

    public void criminal_activity_click(android.view.View view) {
        this.startReportActivity(IssueType.CRIMINAL_ACTIVITY.toString());
    }

    public void electricity_click(android.view.View view) {
        this.startReportActivity(IssueType.ELECTRICITY.toString());
    }

    public void emergency_services_click(android.view.View view) {
        this.startReportActivity(IssueType.EMERGENCY_SERVICES.toString());
    }

    public void public_transportation_click(android.view.View view) {
        this.startReportActivity(IssueType.PUBLIC_TRANSPORTATION.toString());
    }

    public void public_education_click(android.view.View view) {
        this.startReportActivity(IssueType.PUBLIC_EDUCATION.toString());
    }

    public void startReportActivity(String report_type_str) {
        Log.i(TAG, "------- start report activity()");
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("report_type", report_type_str);
        Log.i(TAG, "------ Sending intent with extras: " + intent.getExtras().toString());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Scale report button sizes to fit screen
     */
    private void scaleButtonSizes() {
        final double WIDTH_SCALER = 0.16;
        final double HEIGHT_SCALER = 0.10;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int bttnWidth = (int)(screenWidth * WIDTH_SCALER);
        int bttnHeight = (int)(screenHeight * HEIGHT_SCALER);

        updateLayoutParams((ImageButton) findViewById(R.id.roadsDriversImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.wasteEnvironmentImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.waterGasImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.publicEducationImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.publicTransportationImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.emergencyImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.electricityImageButton), bttnWidth, bttnHeight);
        updateLayoutParams((ImageButton) findViewById(R.id.criminalActivityImageButton), bttnWidth, bttnHeight);
    }

    /**
     * Scale button to fit screen
     * @param imgBttn
     */
    private void updateLayoutParams(ImageButton imgBttn, int width, int height) {
        LayoutParams layoutParams;
        layoutParams = imgBttn.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        imgBttn.setLayoutParams(layoutParams);
    }

}
