package com.example.soga;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
//        onSensorChanged()
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
        float[] values = event.values;

        // Detect upward acceleration
        float acceleration = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        long currentTime = 0;
        if (acceleration > 10) {
            currentTime = System.currentTimeMillis();

            // Check if enough time has passed since the last jump
            if (currentTime - lastJumpTime > JUMP_DETECTION_INTERVAL) {
                // Count this as a jump
                jumpCount++;
                lastJumpTime = currentTime;

                // Do something when a jump is detected (e.g., increment a counter)

            }
        }
        Log.d("mytag",Integer.toString(jumpCount));
        if(jumpCount > 5){
            myEditText = findViewById(R.id.acceValue);
            // Now you can work with myEditText

            myEditText.setText("True");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private boolean isJumping(float[] accelerometerValues) {
        // Implement your logic to detect jumping based on accelerometer readings
        // This can involve analyzing the values of the accelerometer to recognize a specific motion pattern
        // For simplicity, let's assume jumping if the Y-axis acceleration is above a certain threshold
        float yAcceleration = accelerometerValues[1];
        return yAcceleration > 10.0; // Adjust the threshold as needed
    }



}