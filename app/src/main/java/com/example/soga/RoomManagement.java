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
import java.util.Random;


public class RoomManagement extends AppCompatActivity {

    protected final int CODE_LENGTH = 6;
    protected ArrayList<HashMap<String, Object>> endpointsList = new ArrayList<>();

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
                    String lat = "";
                    String lng = "";

                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

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
        int taskID = spinner.getSelectedItemPosition();

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

        newCard.setCardBackgroundColor(Color.WHITE);
        TextView textView = new TextView(getApplicationContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        String cardTxt = " Location: " + locStr + "      " + task;
        textView.setText(cardTxt);
        textView.setPadding(16, 16, 16, 16);

        HashMap<String, Object> endpoint = new HashMap<>();
        endpoint.put("task", taskID);
        endpoint.put("lat", lat);
        endpoint.put("lng", lng);
        endpoint.put("hint", "TODO");
        endpointsList.add(endpoint);

        // Add TextView To CardView
        newCard.addView(textView);

        // Add new CardView to Container
        cardContainer.addView(newCard);

    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public void createRoom(View view) {

        // Create a new user with a first and last name
        Map<String, Object> room = new HashMap<>();

        TextView roomNameView = findViewById(R.id.avail_text); // Room Name
        String roomName = roomNameView.getText().toString();
        int capacity = 6;
        try{
            TextView capacityView = findViewById(R.id.room_capacity); // Capacity
            capacity = Integer.parseInt(capacityView.getText().toString());
        } catch (Exception e){
            Toast.makeText(this, "Invalid Capacity, set to Default (6 people)", Toast.LENGTH_SHORT).show();
        }

        // Marshalling the Struct
        if (roomName.length() == 0){
            Toast.makeText(this, "Invalid Room Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endpointsList.size() == 0){
            Toast.makeText(this, "Locate some Endpoints first.", Toast.LENGTH_SHORT).show();
            return;
        }
        String code = generateRandomString(CODE_LENGTH);

        room.put("name", roomName);
        room.put("capacity", capacity);
        room.put("code", code);
        room.put("endpoints", endpointsList);

        // Add a new document with a generated ID
        db.collection("rooms")
                .add(room).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RoomManagement.this,"Success, the code is " + code, Toast.LENGTH_SHORT).show();
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
