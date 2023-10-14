package com.example.soga;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.soga.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import android.Manifest;

import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private LatLng currentLatLng;
    private ActivityMapsBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final int REQUEST_LOCATION_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // if do not have, ask user for the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // if it has, get location.
            getCurrentLocation();
        }

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

        // The bottom will be covered by navigation bar, so padding some area
        mMap.setPadding(0, 0, 0, 150);

        // Add a marker in Sydney and move the camera
//        -37.79970759026579, 144.9636742373955
//        -37.80364308009827, 144.96373452399772
        getCurrentLocation();
//        LatLng unimelb = new LatLng(-37.80364308009827, 144.96373452399772);
        LatLng unimelb = currentLatLng;
        mMap.addMarker(new MarkerOptions().position(unimelb).title("Marker in Unimelb"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unimelb,18));


    }

    /**
     * This method get the current location via locationManager
     * */
    public void getCurrentLocation() {
        double Default_Lat = -37.80364308009827;
        double Default_Lng = 144.96373452399772;
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
    }




    public void signOut(View view){
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "User logged out.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
                LatLng unimelb = currentLatLng;
                mMap.addMarker(new MarkerOptions().position(unimelb).title("Marker in Unimelb"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unimelb,18));
            } else {
                getCurrentLocation();
                Toast.makeText(this, "Location permission dined.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}