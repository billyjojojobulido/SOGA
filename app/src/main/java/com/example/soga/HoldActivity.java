package com.example.soga;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HoldActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private boolean isStable;
    private boolean isFacingUpStable;
    private Handler stabilityHandler = new Handler();
    private Runnable stabilityRunnable;
    private boolean isTimeRunning = false;

    private Button button_test;
    private int stabilityCheckCount = 0; // Counter for stability checks

    private boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold);
        button_test = findViewById(R.id.hold_btn);


//        Log.d("DEBUG", "isTimeRunning value: " + isTimeRunning);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        isStable = false;
        isFacingUpStable = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag){
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Threshold for detecting stability (near-zero angular velocity)
                float stabilityThreshold = 0.5f;
                isStable = Math.abs(x) < stabilityThreshold && Math.abs(y) < stabilityThreshold && Math.abs(z) < stabilityThreshold;

                // Assuming that if the device is stable and the z value is close to 0, the device is facing up.
                isFacingUpStable = isStable && Math.abs(z) < stabilityThreshold;

                if (isFacingUpStable) {

                    if (!isTimeRunning) {
                        button_test.setText("Checking");
                        start();
                    }

                } else {
                    pause();
                    // Device is not facing up or is not stable
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void start(){
//        Log.d("DEBUG", "start() called - isTimeRunning before: " + isTimeRunning);

        if (isTimeRunning) return; // Prevent multiple calls

        isTimeRunning = true;
//        Log.d("DEBUG", "start() called - isTimeRunning after: " + isTimeRunning);



        stabilityRunnable = new Runnable() {
            @Override
            public void run() {
                if (isFacingUpStable) {
                    stabilityCheckCount++;
                    if (stabilityCheckCount == 10) {
                        button_test.setText("Stable and Facing up");
                        finish();
                    } else {
                        // Continue checking
                        stabilityHandler.postDelayed(stabilityRunnable, 1000);
                    }
                }else{
                    // Device is not stable, reset the countdown
                    stabilityCheckCount = 0;
                    pause();
                }

            }
        };
        stabilityHandler.postDelayed(stabilityRunnable, 1000); // 10 seconds

    }


    private void pause(){
        if (!isTimeRunning) return; // Prevent multiple calls
        flag = false;
        isTimeRunning = false;
        stabilityCheckCount = 0; // Reset the stability check counter
        button_test.setText("Now not stable and stop");


    }

    public void finish(){
        isTimeRunning = false;
        flag = false;
        sensorManager.unregisterListener(this);
        stabilityHandler.removeCallbacks(stabilityRunnable);
        button_test.setText("finished");
    }

    public void onButtonClick(View view) {
//        progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.VISIBLE);

        flag = true;
//        updateProgressBar(progressBar);
    }


//    public void hold(View view){
//        startActivities(new Intent[]{new Intent(this, HoldActivity.class)});
//    }


}
