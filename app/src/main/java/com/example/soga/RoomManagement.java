package com.example.soga;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoomManagement extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);
    }


    public void checkLocation(View view) {

        EditText addressInput = findViewById(R.id.location_input);
        String address = addressInput.getText().toString();

        if (address.length() == 0) {
            Toast.makeText(this, "Input an address first", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {

            // Request should be sent in other Thread than main Thread
            @Override
            public void run() {
                try {
                    String query = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                            address
                            + "&key=AIzaSyB_4H-LgBByty7rCuZR4DSagLK6c7y1rYY";
                    URL url = new URL(query);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();

                    Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = in.read()) >= 0; ) {
                        sb.append((char) c);
                    }
                    String response = sb.toString();
                    String msg = "";

                    connection.disconnect();

                    // Json Parser
                    JsonElement jsonElement = JsonParser.parseString(response);
//                    Toast.makeText(RoomManagement.this, response.toString(), Toast.LENGTH_SHORT).show();
                    String lat = "";
                    String lng = "";

                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
//                        String status = jsonObject.get("status").getAsString();
//                        Toast.makeText(RoomManagement.this, status, Toast.LENGTH_SHORT).show();

                        JsonArray results = jsonObject.get("results").getAsJsonArray();
                        if (!results.isEmpty()) {
                            JsonElement location = results.get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location");
                            lat = location.getAsJsonObject().get("lat").getAsString();
                            lng = location.getAsJsonObject().get("lng").getAsString();
                        } else {
                            lat = "0";
                            lng = "0";
                        }

                    }

                    // To update UI, should be executed in the main thread
                    String finalLat = lat;
                    String finalLng = lng;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView locVisual = findViewById(R.id.avail_text);
                            String locStr = "Location:" + finalLat + "," + finalLng;

                            if (finalLat.equals("0") && finalLng.equals("0")) {
                                locStr = "Invalid Location.";
                            }
                            // Update
                            locVisual.setText(locStr);

                        }
                    });

                } catch (IOException e) {
                    // Handle Exception
                    Toast.makeText(RoomManagement.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }).start();
    }

    public void createEndpoint(View view) {
        LinearLayout cardContainer = findViewById(R.id.card_layout);

        // Task Selected
        Spinner spinner = findViewById(R.id.end_task);
        String task = spinner.getSelectedItem().toString();

        TextView locStrView = findViewById(R.id.avail_text);
        String locStr = locStrView.getText().toString();
        String lat = "";
        String lng = "";
        if (locStr.equals("No Location Yet")) {
            // No location specified
            Toast.makeText(this, "Please specify your location", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String[] cord = locStr.split(":", 2)[1].split(",", 2);
            lat = cord[0];
            lng = cord[1];
        }

        Toast.makeText(this, lat, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, lng, Toast.LENGTH_SHORT).show();

        // Create New Card
        CardView newCard = new CardView(getApplicationContext());
        newCard.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        ));

        newCard.setCardElevation(4);

//        newCard.setCardCornerRadius(8);
        newCard.setCardBackgroundColor(Color.WHITE);
        TextView textView = new TextView(getApplicationContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        String cardTxt = " Location: " + lat + ", " + lng + "      " + task;
        textView.setText(cardTxt);
        textView.setPadding(16, 16, 16, 16);

        // Add TextView To CardView
        newCard.addView(textView);

        // Add new CardView to Container
        cardContainer.addView(newCard);

    }

    public void createRoom(View view) {

        // Create a new user with a first and last name
        Map<String, Object> room = new HashMap<>();

        room.put("name", "Hunter Union");
        room.put("capacity", 6);

        // Create an Embedded ArrayList for endpoints
        ArrayList<HashMap<String, Object>> endpointsList = new ArrayList<>();

        // First
        HashMap<String, Object> endpoint1 = new HashMap<>();
        endpoint1.put("task", 1);
        endpoint1.put("lat", 30.101010);
        endpoint1.put("lng", 128.12121);
        endpoint1.put("hint", "My home");
        endpointsList.add(endpoint1);

        // Second
        HashMap<String, Object> endpoint2 = new HashMap<>();
        endpoint2.put("task", 2);
        endpoint2.put("lat", -30.101010);
        endpoint2.put("lng", -128.12121);
        endpoint2.put("hint", "My Second home");
        endpointsList.add(endpoint2);

        room.put("endpoints", endpointsList);


// Add a new document with a generated ID
        db.collection("rooms")
                .add(room).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RoomManagement.this,"Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RoomManagement.this,"Failed", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
