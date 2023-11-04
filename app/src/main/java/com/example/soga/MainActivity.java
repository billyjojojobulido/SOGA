package com.example.soga;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.soga.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseUser user;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;

    private final static int FEATURED_HUNT_CODE_CITY = 1;
    private final static int FEATURED_HUNT_CODE_ENGINEERING = 2;
    private final static int FEATURED_HUNT_CODE_CAMPUS = 3;
    private final static int FEATURED_HUNT_CODE_GARDEN = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;

        if (user == null) {
            intent = new Intent(this, Login.class);
            startActivity(intent);
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.home_dashboard);
    }

    public void onButtonClickCreate(View view){
        startActivities(new Intent[]{new Intent(this, RoomManagement.class)});
    }

    public void onButtonClickJoin(View view){
        startActivities(new Intent[]{new Intent(this, JoinActivity.class)});
    }
    public void onButtonClickBoard(View view){
        startActivities(new Intent[]{new Intent(this, LeaderBoard.class)});
    }

    public void logOut(View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "User logged out.",Toast.LENGTH_SHORT).show();
    }


    public void CityHunt(View view){
        featuredHunt(FEATURED_HUNT_CODE_CITY);
    }

    public void EngineeringHunt(View view){
        featuredHunt(FEATURED_HUNT_CODE_ENGINEERING);
    }

    public void CampusHunt(View view){
        featuredHunt(FEATURED_HUNT_CODE_CAMPUS);
    }

    public void GardenHunt(View view){
        featuredHunt(FEATURED_HUNT_CODE_GARDEN);
    }

    private void featuredHunt(int code){
        /** Check if the user is properly logged in
         * */
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Toast.makeText(this, "Invalid User Id, please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();

        /**
         * Starting the Game
         * */

        ArrayList<HashMap<String, Object>> endpoints = getFeaturedEndpoints(code);
        long currentTimeStamp = System.currentTimeMillis() / 1000;

        Map<String, Object> game = new HashMap<>();
        game.put("uid", uid);
        game.put("roomCode", code);
        game.put("progress", 0L);
        game.put("startTime", currentTimeStamp);
        game.put("steps", 0);
        game.put("endpoints", endpoints);

        // Add a new document with a generated ID
        db.collection("games").add(game).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra("endpoints", endpoints);
                        intent.putExtra("code", "XXXIX" + code);
                        intent.putExtra("startTime",currentTimeStamp);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error adding document", e);
            }
        });

    }

    private ArrayList<HashMap<String, Object>> getFeaturedEndpoints(int code){
        ArrayList<HashMap<String, Object>> endpoints = new ArrayList<>();
        switch (code){
            case FEATURED_HUNT_CODE_CITY:
                HashMap<String, Object> map1 = new HashMap<>();
                /** Point1 - State Lirbary
                 * */
                map1.put("answer", "Victoria State Library");
                map1.put("hint", "State landmark opened in 1856, spanning one city block & featuring a grand reading room.");
                map1.put("task", 0L);
                map1.put("lat", "-37.8099889");
                map1.put("lng", "144.9643298");
                endpoints.add(map1);
                /** Point2 - Emporium Melbourne
                 * */
                HashMap<String, Object> map2 = new HashMap<>();
                map2.put("answer", "Emporium Melbourne");
                map2.put("hint", "Contemporary shopping mall offering a variety of upscale retailers, plus restaurants & a food court.");
                map2.put("task", 0L);
                map2.put("lat", "-37.8123836");
                map2.put("lng", "144.9638398");
                endpoints.add(map2);

                /** Point3 - China town
                 * */
                HashMap<String, Object> map3 = new HashMap<>();
                map3.put("answer", "China town");
                map3.put("hint", "Vibrant, historic neighborhood offering numerous Chinese eateries, shops & cultural events.");
                map3.put("task", 0L);
                map3.put("lat", "-37.81188876");
                map3.put("lng", " 144.9668043");
                endpoints.add(map3);
                break;
            case FEATURED_HUNT_CODE_ENGINEERING:

                break;
            case FEATURED_HUNT_CODE_CAMPUS:

                break;
            default:
                /**
                 * Botanical Gardens Hunt
                 * */

        }
        return endpoints;
    }


}