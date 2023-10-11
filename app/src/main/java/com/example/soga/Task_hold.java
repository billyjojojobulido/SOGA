package com.example.soga;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Task_hold extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private boolean isStable;
    private boolean isFacingUpStable;
    private Handler stabilityHandler = new Handler();
    private Runnable stabilityRunnable;
    private boolean isTimeRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

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
        System.out.println("IN task");
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Threshold for detecting stability (near-zero angular velocity)
            float stabilityThreshold = 5f;

            // Assuming that if the device is stable and the z value is close to 0, the device is facing up.
            isFacingUpStable = isStable && Math.abs(z) < stabilityThreshold;

            if (isFacingUpStable) {
                if (!isTimeRunning){
                    start();
                }

            } else {
                pause();
                // Device is not facing up or is not stable
            }


        }
    }

    private void start(){
        isTimeRunning = true;

        stabilityRunnable = new Runnable() {
            @Override
            public void run() {
                // Device has remained stable for 10 seconds
                isFacingUpStable = true;
                // Handle the event here, e.g., show a notification or update the UI
            }
        };
        stabilityHandler.postDelayed(stabilityRunnable, 10000); // 10 seconds

    }


    private void pause(){
        isTimeRunning = false;
        stabilityHandler.removeCallbacks(stabilityRunnable);

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }
}

