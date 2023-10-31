package com.example.soga;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    private Button confirmBtn;
    private EditText joinCode;
    private FirebaseFirestore db;
    private ArrayList<HashMap<String, Object>> endpoints;
    private TextView textPop;
    private Boolean clicked = false;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        confirmBtn = findViewById(R.id.confirmBtn);
        joinCode = findViewById(R.id.textJoinCode);
        textPop = findViewById(R.id.textPop);

        db = FirebaseFirestore.getInstance();


    }

//    the user need to click the button to confirm their join codes
    public void onButtonClickCode(View view){

        if (!clicked) {
            clicked = true;
            textPop.setText("");

            String inputCode = joinCode.getText().toString();

            if (inputCode.isEmpty()) {
                textPop.setText("Please input a valid code.");
                joinCode.setText("Code: ");


            } else {
                confirmBtn.setText("Checking");
                joinRoom(inputCode);

            }
            confirmBtn.setText("Confirm");
            clicked = false;
        }

    }

    /**
     * Join a New Game
     * */
    private void createGame(String code, String uid){
        confirmBtn.setText("Joining");
        long currentTimeStamp = System.currentTimeMillis() / 1000;

        Map<String, Object> game = new HashMap<>();
        game.put("uid", uid);
        game.put("roomCode", code);
        game.put("progress", 0);
        game.put("startTime", currentTimeStamp);
        game.put("steps", 0);
        game.put("endpoints", endpoints);

        // Add a new document with a generated ID
        db.collection("games").add(game).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        startActivities(new Intent[]{new Intent(JoinActivity.this, MapsActivity.class)});
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JoinActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error adding document", e);
            }
        });

    }

    private void joinRoom(String code){
        /** Check if the user is properly logged in
         * */
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            textPop.setText("Invalid User Id, please log in again.");
            return;
        }
        String uid = user.getUid();

        /** Check if the room exists
         * */
        db.collection("rooms").whereEqualTo("code", code).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        textPop.setText("Room does not exist, please try a valid code.");
                    }else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            endpoints = (ArrayList<HashMap<String, Object>>) document.get("endpoints");
                            if (endpoints == null) {
                                textPop.setText("The room no longer exists");
                                return;
                            }
                            break;
                        }
                    }
                    /**
                     * Create the game after successfully found the room.
                     * */
                    createGame(code, uid);

                } else {
                    Log.d(TAG, "Error retrieving Rooms documents: ", task.getException());
                }
            }
        });


    }


    public void back(View view){
        finish();
    }


    public ArrayList<HashMap<String, Object>> getEndpoints() {
        return endpoints;
    }
}