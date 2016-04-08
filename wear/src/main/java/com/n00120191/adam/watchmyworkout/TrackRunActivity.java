package com.n00120191.adam.watchmyworkout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import java.math.BigDecimal;
import java.math.RoundingMode;

//This class and activity is used to track the location of the user using the on board GPS. It
// retrieves the users location in Latitude and Longitude, which can then be converted into
// meters traveled
public class TrackRunActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Creating a reference to each view object on the screen, the Google API client, and the
    // current and previous location
    private Button startButton;
    private TextView trackRun;
    private TextView distanceRun;
    private TextView metersRun;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Location mLastLocation;

    private boolean isRecording;
    private double distanceCovered;
    private double distanceInKm;
    private int updates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        setAmbientEnabled();

        trackRun = (TextView)findViewById(R.id.trackRun);
        distanceRun = (TextView)findViewById(R.id.distanceRun);
        metersRun = (TextView)findViewById(R.id.metersRun);
        startButton = (Button)findViewById(R.id.btn_start);
        startButton.setVisibility(View.GONE);
        isRecording = false;
        updates = 0;

        //This is used to determine if the users device has a built in GPS
        if (!hasGps()) {
            Log.d("GPS", "This hardware doesn't have GPS.");
            // Fall back to functionality that does not use location or
            // warn the user that location function is not available.
        }

        //Creates a google API client which will be used to access the location services api
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Wearable.API)
                    .build();
        }

        mCurrentLocation = new Location("current");
        mLastLocation = new Location("previous");

        trackRun.setText("Connecting...");
    }

    //When the user has connected to the location services this function will run automatically.
    // The location request object is given a high accuracy which will retrieve the users position
    // to within a few meters/ The intervals will set the minimum and maximum time between the
    // location updates in milliseconds
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)//time between updates
                .setFastestInterval(2000);//fasted time allowed between updates

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(new ResultCallback() {
                    @Override
                    public void onResult(Result result) {
                        Log.d("RST", "Wrong onresult");

                    }

                    //@Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            if (Log.isLoggable("NLOC", Log.DEBUG)) {
                                Log.d("NLOC", "Successfully requested location updates");
                            }

                        } else {
                            Log.e("NLOC",
                                    "Failed in requesting location updates, "
                                            + "status code: "
                                            + status.getStatusCode()
                                            + ", message: "
                                            + status.getStatusMessage());
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Log.isLoggable("NLOC", Log.DEBUG)) {
            Log.d("NLOC", "connection to location client suspended");
        }
    }

    //This method is automatically called when the location has been changed. It gets the users
    // current position and calculates the distance from the users last position.
    @Override
    public void onLocationChanged(Location location) {
        if(updates == 0){
            trackRun.setText("Connected!");
            startButton.setVisibility(View.VISIBLE);
        }

        mLastLocation.setLatitude(mCurrentLocation.getLatitude());
        mLastLocation.setLongitude(mCurrentLocation.getLongitude());

        mCurrentLocation.setLatitude(location.getLatitude());
        mCurrentLocation.setLongitude(location.getLongitude());

        if(isRecording && updates > 1){
            updateDistance(mLastLocation.distanceTo(mCurrentLocation));
        }

        updates++;
    }

    //This function calculates the difference that the user has covered
    public void updateDistance(double distance){
        Log.d("UPDATE", String.valueOf(round(distance, 1)));
        if(distance > 1){
            distanceCovered = distanceCovered + distance;
            distanceInKm = distanceCovered / 1000;
            distanceRun.setText(String.valueOf(round(distanceInKm, 1)) + "km");
            metersRun.setText(String.valueOf(round(distanceCovered, 1)) + "m");
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //The on click handler for the button which will determine if the user wishes to start or stop recodring.
    public void btnBeginRecording(View v){
        if(!isRecording){
            trackRun.setText("Begin exercise");
            startButton.setText("Stop");
            isRecording = true;
        }   else if(isRecording){
            startButton.setText("Start");
            isRecording = false;

            // save data to be sent to phone
            Intent intent = new Intent(this, SendingDataActivity.class);
            intent.putExtra("activity", "run");
            intent.putExtra("data", distanceInKm);
            startActivity(intent);
            finish();
        }
    }

    //default methods
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    protected void onStart() {

        Log.d("LOC", "start");
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {

    }
}
