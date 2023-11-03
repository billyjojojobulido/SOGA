package com.example.soga;


import static androidx.fragment.app.FragmentManager.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.soga.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private Sensor stepSensor;
    private LatLng currentLatLng;
    private ActivityMapsBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private ArrayList<HashMap<String, Object>> endpoints;
    private long startTime;

    private long currentTimeStamp;
    private int progress = 0;

    private SensorEventListener stepListener;
    private int steps;
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;
    private int initialStepCount = 0;
    private int appSteps = 0;

    private static final Long TASK_CODE_HORIZONTAL = 0L;
    private static final Long TASK_CODE_JUMP = 1L;

    private SensorManager sensorManager;
    private Sensor sensor;
    private Marker userMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    private FirebaseFirestore db;


    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // Here, no request code
                                Intent data = result.getData();
                                if (data != null) {
                                    int updatedProgress = data.getIntExtra("updatedProgress", 1);
                                    progress = updatedProgress;
                                    updateProgressUI();
                                    if (progress >= endpoints.size()) {
                                        leaderboard();
                                    }
                                }
                            }
                        }
                    });

    private void leaderboard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!");
        builder.setMessage("You have finished the game!");
        currentTimeStamp = System.currentTimeMillis() / 1000;

        long endTime = currentTimeStamp;
        FirebaseUser user = mAuth.getCurrentUser();
        String username = user.getEmail();

        db = FirebaseFirestore.getInstance();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username",username);
        userInfo.put("startTime",startTime);
        userInfo.put("endTime",endTime);
        userInfo.put("steps",appSteps);
        db.collection("userInfo").document(username).set(userInfo);
        // Add a new document with a generated ID
//        db.collection("userInfo").add(userInfo).addOnSuccessListener(
//                new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                        Intent intent = new Intent(MapsActivity.this, LeaderBoard.class);
//                        intent.putExtra("username", username);
//                        intent.putExtra("endTime", endTime);
//                        intent.putExtra("startTime",startTime);
//                        intent.putExtra("steps",appSteps);
//                        startActivity(intent);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MapsActivity.this,"Failed", Toast.LENGTH_SHORT).show();
//                Log.w(ContentValues.TAG, "Error adding document", e);
//            }
//        });

        // btn to quite
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });


        // create dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateProgressUI() {
        int totalProgress = endpoints.size();

        TextView progressView = findViewById(R.id.progress_text);
        progressView.setText("Progress: " + progress + " / " + totalProgress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        endpoints = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("endpoints");
        startTime =  (long) intent.getSerializableExtra("startTime");
        System.out.println(startTime);

        int totalProgress = endpoints.size();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView progressView = findViewById(R.id.progress_text);
        progressView.setText("Progress: " + progress + " / " + totalProgress);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // check location permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            // if do not have, ask user for the permission
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//        } else {
//            // if it has, get location.
//            getCurrentLocation();
////            enableUserLocation();
//        }

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
                } else {
                    appSteps = steps - initialStepCount;
                }//                simply for testing using firebase, should see a "userSteps" collection, and the instance is named Test
//                storeSteps ("Test");
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle accuracy changes
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.

        // The bottom will be covered by navigation bar, so padding some area
        mMap.setPadding(0, 0, 0, 150);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    System.out.println("on success onLocationResult: " + location.getLatitude());

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    float zoomLevel =20.0f; // Adjust the zoom level as needed

                    LatLng latLng = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));


                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)           // Sets the center of the map to the new location
                            .zoom(mMap.getCameraPosition().zoom)          // Sets the zoom
                            // .bearing(azimuth)      // Uncomment and set bearing if needed
                            // .tilt(tiltAngle)       // Uncomment and set tilt if needed
                            .build();

                    // Animate the camera to the new
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
//            zoomToUserLocation();
//            getCurrentLocation();

//            zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }
        // Add a marker in Sydney and move the camera
//        -37.79970759026579, 144.9636742373955
//        -37.80364308009827, 144.96373452399772
//        getCurrentLocation();
//        LatLng unimelb = new LatLng(-37.80364308009827, 144.96373452399772);
//        LatLng unimelb = currentLatLng;
//        mMap.addMarker(new MarkerOptions().position(unimelb).title("Marker in Unimelb"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unimelb, 18));

//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(currentLatLng)
////                .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction))
//                .anchor(0.5f, 0.5f);
//        int width = 50; // 标记宽度（像素）
//        int height = 50; // 标记高度（像素）
//        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.direction);
//        Bitmap b = bitmapdraw.getBitmap();
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));// 设置箭头图标
//        userMarker = mMap.addMarker(markerOptions);
//        userMarker.setRotation(headingAngle);

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            double latitude = locationResult.getLastLocation().getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();

            // Create a LatLng object

//            System.out.println("onLocationResult: " + locationResult.getLastLocation());
            currentLatLng = new LatLng(latitude,longitude);
//            double latitude = locationResult.getLastLocation().getLatitude();
//            double longitude = locationResult.getLastLocation().getLongitude();
            float zoomLevel =  20.0f; // Adjust the zoom level as needed

            LatLng latLng = new LatLng(latitude, longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)           // Sets the center of the map to the new location
                    .zoom(mMap.getCameraPosition().zoom)          // Sets the zoom
                    // .bearing(azimuth)      // Uncomment and set bearing if needed
                    // .tilt(tiltAngle)       // Uncomment and set tilt if needed
                    .build();

            // Animate the camera to the new
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            if (mMap != null) {
//                setUserLocationMarker(locationResult.getLastLocation());
//            }
        }
    };

    private void startLocationUpdates() {

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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            // you need to request permissions...
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }


    /**
     * This method get the current location via locationManager
     * */
    public void getCurrentLocation() {
        double Default_Lat = -37.7983459;
        double Default_Lng = 144.960974;
        LocationManager locationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        List<String> providers = locationManager.getProviders(true);
        Location location;
        for (String provider : providers) {
            try {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    Default_Lat = location.getLatitude();
                    Default_Lng = location.getLongitude();
                    break;
                }

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        currentLatLng = new LatLng(Default_Lat, Default_Lng);
        System.out.println(Default_Lat + Default_Lng);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
    }

    @Override
    public void onLocationChanged(Location location) {
        // 获取到新的位置信息后，在地图上更新标记或移动地图视图
        System.out.println(location.getLatitude() + location.getLongitude());
        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        userMarker.setPosition(newLocation);
    }

    public void getHint(View view) {
        if (progress >= endpoints.size()) {
            return;
        }

        String hint = (String) endpoints.get(progress).get("hint");
        showHintDialog(hint);
    }


    private void showHintDialog(String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hint For Current Endpoint");
        builder.setMessage("Hint:\n" + hint);

        // btn to quite
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        // create dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMessageDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage(message);

        // btn to quit

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        // create dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void arrivalCheck(View view) {
        System.out.println("arrival check "+currentLatLng.latitude);
        String endpointLat = (String) endpoints.get(progress).get("lat");
        String endpointLon = (String) endpoints.get(progress).get("lng");
        Long taskCode = (Long) endpoints.get(progress).get("task");

        try {
            double lat = Double.parseDouble(endpointLat);
            double lon = Double.parseDouble(endpointLon);
            boolean isNearBy = isLocationNearby(lat, lon, 100);
            if (isNearBy) {
                System.out.println("You are here.");
                /**
                 * TODO
                 * Write to DB
                 * */

                Intent intent;
                if (taskCode == TASK_CODE_HORIZONTAL) {
                    intent = new Intent(this, HoldActivity.class);
                } else if (taskCode == TASK_CODE_JUMP) {
                    intent = new Intent(this, JumpActivity.class);
                } else {
                    intent = new Intent(this, TurnAroundTask.class);
                }
                intent.putExtra("progress", progress);
                activityResultLauncher.launch(intent);
            } else {
                showMessageDialog("You are not at right place");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public boolean isLocationNearby(double targetLat, double targetLon, double distanceThreshold) {
        Location targetLocation = new Location("target");
        targetLocation.setLatitude(targetLat);
        targetLocation.setLongitude(targetLon);

        Location currentLocation = new Location("current");
        getCurrentLocation();
        currentLocation.setLatitude(currentLatLng.latitude);
        currentLocation.setLongitude(currentLatLng.longitude);

        float distance = currentLocation.distanceTo(targetLocation);

        // Return true if the distance is less than or equal to the threshold, false otherwise
        return distance <= distanceThreshold;
//        return true;
    }


    public void signOut(View view) {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "User logged out.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
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
                mMap.setMyLocationEnabled(true);
                LatLng unimelb = currentLatLng;
//                mMap.addMarker(new MarkerOptions().position(unimelb).title("Marker in Unimelb"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unimelb,18));
            } else {
//                getCurrentLocation();
                mMap.setMyLocationEnabled(true);
                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        System.out.println("on success onLocationResult: " + location.getLatitude());

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
//                        float zoomLevel =  mMap.getCameraPosition().zoom; // Adjust the zoom level as needed
                        float zoomLevel =  20.0f; // Adjust the zoom level as needed

                        LatLng latLng = new LatLng(latitude, longitude);

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)           // Sets the center of the map to the new location
                                .zoom(mMap.getCameraPosition().zoom)          // Sets the zoom
                                // .bearing(azimuth)      // Uncomment and set bearing if needed
                                // .tilt(tiltAngle)       // Uncomment and set tilt if needed
                                .build();

                        // Animate the camera to the new
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
//                zoomToUserLocation();
                Toast.makeText(this, "Location permission dined.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}