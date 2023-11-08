package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TurnAroundTask extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;

    private TextView xResultTextView;

    private long lastTimestamp = 0;
    private float totalRotation = 0.0f;
    private boolean isCalculatingRotation = false;
    private Button startButton;
    private Button finishButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_around_task);

        xResultTextView = findViewById(R.id.x_result);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        startButton = findViewById(R.id.turnAroundStart);
        finishButton = findViewById(R.id.finishBtn);


    }
    public void startTurnAround(View view){
        isCalculatingRotation = true;
        startButton.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (lastTimestamp != 0 && isCalculatingRotation) {

                long timeDelta = event.timestamp - lastTimestamp;


                float angularSpeedX = event.values[0];
                float angularSpeedY = event.values[1];
                float angularSpeedZ = event.values[2];


                float angleX = angularSpeedX * timeDelta / 1e9f;
                float angleY = angularSpeedY * timeDelta / 1e9f;
                float angleZ = angularSpeedZ * timeDelta / 1e9f;


                if (angularSpeedZ > 0) {

                    totalRotation += Math.toDegrees(Math.sqrt(angleX * angleX + angleY * angleY + angleZ * angleZ));
                } else {

                    totalRotation -= Math.toDegrees(Math.sqrt(angleX * angleX + angleY * angleY + angleZ * angleZ));
                }

                if(Math.abs(totalRotation)>= 350.0f){
                    sensorManager.unregisterListener(this);
                    finishButton.setVisibility(View.VISIBLE);

                }
                xResultTextView.setText(""+totalRotation);
            }

            lastTimestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onButtonClickFinishTurn(View view) {
        Intent resultIntent = new Intent();
        int updatedProgress = getIntent().getIntExtra("progress", 0) + 1;
        resultIntent.putExtra("updatedProgress", updatedProgress);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}