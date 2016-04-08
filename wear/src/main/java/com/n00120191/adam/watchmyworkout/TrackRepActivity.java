package com.n00120191.adam.watchmyworkout;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

//This activity is used to track a users repetitions while weight lifting. It uses the gravity
// sensor which detects the orientation of the watch, from face up to face down, on a
// scale of 9.8 to -9.8.
public class TrackRepActivity extends WearableActivity implements SensorEventListener{


    private TextView recStatus;

    private Button startButton;

    private SensorManager mSenManager;
    private Sensor gravitySensor;

    private float standardGravity, thresholdGravity;

    //This is a vibrator object which allows the device to vibrate, so that a user will know when a
    // single rep has been completed
    private Vibrator vibrator;
    private long[] vibrationPattern;
    //-1 - don't repeat
    private int indexInPatternToRepeat;

    //Variables used to count reps and keep track of the current stage;
    private boolean canRecord;
    private boolean isRecording;
    private int repCount;
    private int stageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep);
        setAmbientEnabled();

        isRecording = false;
        canRecord = false;
        repCount = 0;
        stageCount = 0;
        recStatus = (TextView)findViewById(R.id.recStatus);

        standardGravity = SensorManager.STANDARD_GRAVITY;
        thresholdGravity = standardGravity/2;

        startButton = (Button)findViewById(R.id.btn_start);

        //Creates the sensor manager which will give access to the gravity sensor.
        mSenManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = mSenManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        //sets up the device vibration and the vibration pattern
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrationPattern = new long[]{0, 50, 50, 50};
        indexInPatternToRepeat  = -1;
    }

    //Every time the sensor detects a change this function will be automatically run.
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor source = event.sensor;
        float z = event.values[2];

        //this algorithm will track the users wrist motion. They begin with their arm lowered and
        // the recording starts, then as their wrist moves up it will enter into the second stage
        // until the wrist is as high as it should go, the same will repeat until the users wrist
        // is lowered again and the rep count will be incremented and the user will be notified via
        // vibration.
        if(source.getType() == Sensor.TYPE_GRAVITY){
            if(z <= -6 && z >= -7 && !isRecording){
                resumeRepRecording();
            }
            if(isRecording && z <= -8 ){
                switch (stageCount){
                    case 0 :
                        stageCount = 1;
                        break;
                    case 2 :
                        stageCount = 3;
                        break;
                }
            }
            if(isRecording && (z <= -6 && z >= -7)){
                switch(stageCount){
                    case 1 :
                        stageCount = 2;
                        break;
                    case 3 :
                        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                        finishRepRecording();
                        break;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //updates the repetition count
    public void resumeRepRecording(){
        if(canRecord){
            isRecording = true;
            updateReps(repCount);
        }
    }

    //called when each rep is completed
    public void finishRepRecording(){
        stageCount = 0;
        isRecording = false;
        updateReps(repCount);
        repCount++;
    }

    //This on click event is used to determine when the button on the screen is clicked and what
    // should happen depending on the stage, either begin the recording or stop recording and send
    // the data
    public void btnBeginRecording(View v){
        if(!canRecord){
            recStatus.setText("Begin exercise");
            startButton.setText("Stop");
            canRecord = true;
        }   else if(canRecord){
            canRecord = false;

            // save data to be sent to phone
            Intent intent = new Intent(this, SendingDataActivity.class);
            intent.putExtra("activity", "rep");
            intent.putExtra("data", repCount);
            startActivity(intent);
            finish();
        }

    }

    public void btnStopRecording(View v){
        canRecord = false;
    }

    private void updateReps(int repCount){
        recStatus.setText(String.valueOf(repCount));
    }

    //Default functions
    @Override
    protected void onPause() {
        super.onPause();
        mSenManager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSenManager.registerListener(
                this,
                gravitySensor,
                10000);//10 ms
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
        if (isAmbient()) {
            // mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            //mContainerView.setBackground(null);
        }
    }
}
