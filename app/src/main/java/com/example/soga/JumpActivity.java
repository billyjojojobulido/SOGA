package com.example.soga;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JumpActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    // Variables for jump detection
    private static final float GRAVITY_THRESHOLD = 9.8f; // Gravity threshold for upward acceleration
    private static final long JUMP_DETECTION_INTERVAL = 1000; // Time interval to count as a jump
    private long lastJumpTime = 0;
    private int jumpCount = 0;
    TextView myEditText;
    private ProgressBar progressBar;
    private int realValue = 0;
    private boolean flag = false;
    Button finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        finish = findViewById(R.id.finish);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener((SensorListener) this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag == true) {
            float[] values = event.values;

            // Detect upward acceleration
            float acceleration = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
            long currentTime = 0;
            if (acceleration > 11) {
                currentTime = System.currentTimeMillis();

                // Check if enough time has passed since the last jump
                if (currentTime - lastJumpTime > JUMP_DETECTION_INTERVAL) {
                    // Count this as a jump
                    jumpCount++;
                    lastJumpTime = currentTime;

                    // Do something when a jump is detected (e.g., increment a counter)

                }
            }
            if(jumpCount >= 10) {
                jumpCount = 10;
            }
            Log.d("mytag",Integer.toString(jumpCount));

            progressBar.setProgress(10*jumpCount);
//        if(jumpCount > 5){
            myEditText = findViewById(R.id.acceValue);
            // Now you can work with myEditText

            myEditText.setText(Integer.toString(jumpCount) + "/10");
//        }
        }
        if(jumpCount >= 10) {
//            flag = false;
            finish.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    // Example method to simulate a time-consuming task
    private void updateProgressBar(ProgressBar progressBar) {
        // Reference the ProgressBar
        // Show the ProgressBar
        // Update the real value (replace this with your actual logic)
        realValue = 10;
//
//        // Ensure the real value doesn't exceed the maximum progress
//        if (realValue > progressBar.getMax()) {
//            realValue = progressBar.getMax();
//        }

        // Update the ProgressBar based on the real value
        progressBar.setProgress(realValue*jumpCount/2);


    }
    public void onButtonClick(View view) {
//        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        flag = true;
//        updateProgressBar(progressBar);
    }

    public void onButtonClickFinish(View view) {
//        progressBar = findViewById(R.id.progressBar);
        startActivities(new Intent[]{new Intent(this, MapsActivity.class)});

//        updateProgressBar(progressBar);
    }



}