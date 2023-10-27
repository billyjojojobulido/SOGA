package com.example.soga;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class StepsActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener stepListener;
    private TextView step_text;
    private int steps;
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;
    private int initialStepCount = 0;
    private int appSteps = 0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        step_text = findViewById(R.id.stepsCounting);

        db = FirebaseFirestore.getInstance();

        // Request the permission
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACTIVITY_RECOGNITION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACTIVITY_RECOGNITION"},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        } else {
            initSteps();
        }




    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);  // Call to superclass method

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, initialize step counter
                    initSteps();
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Permission denied. Fail to read steps.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isRegistered = sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (!isRegistered) {
            step_text.setText("Not reg");
        }


        // Register the listener with the Sensor Manager
        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Unregister the listener to conserve battery and system resources
//        sensorManager.unregisterListener(stepListener);
//    }

    private void initSteps() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        //        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) == null) {
        //            step_text.setText("Not available");
        //        }else{
        //            step_text.setText("Available");
        //        }

        stepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                steps = (int) event.values[0];
                // Handle step count update
                if (initialStepCount == 0) {
                    // First sensor event, store the initial step count
                    initialStepCount = steps;
                }else{
                    appSteps = steps - initialStepCount;
//                    testing for adding steps in the userInfo collection
//                    storeSteps ("test");
                }

                step_text.setText(String.valueOf(appSteps));
//                simply for testing using firebase, should see a "userSteps" collection, and the instance is named Test
//                storeSteps ("Test");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle accuracy changes
            }
        };
    }

    public void finish(){
//        stop using all the sensor
        sensorManager.unregisterListener(stepListener);
    }


    // the username should be the "user" from main acitivity,
    // this method should be called when the user arrives the final destination
    public void storeSteps (String username){
        Map<String,Object> userSteps = new HashMap<>();
        userSteps.put("steps", appSteps);
//        assuming we are using a data collection named userInfo, and there is an attribute called "name"
//        both data collection name and attribute name can be changed according to the real db
//        one problem here: what if the username is not unique
        db.collection("userInfo").whereEqualTo("name", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the document ID
                        String documentId = document.getId();

                        // Update the "steps" field in the document
                        db.collection("userInfo").document(documentId).update(userSteps)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Document successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    }
                } else {
                    Log.d(TAG, "Error retrieving documents: ", task.getException());
                }
            }
        });
        finish();

    }



}