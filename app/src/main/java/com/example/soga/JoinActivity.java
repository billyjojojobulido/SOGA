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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
                checkJoin(inputCode);

            }
            confirmBtn.setText("Confirm");
            clicked = false;
        }




    }

//    after entering the code, it will be checked to the firebase to see if the room truly exists
    private void checkJoin(String inputCode){
        db.collection("rooms").whereEqualTo("code", inputCode).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()){
                        textPop.setText("Wrong code. Please check it.");

                    }else {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            confirmBtn.setText("Joining");
                            // Get the "endpoints" field which is a hashmap
                            endpoints = (ArrayList<HashMap<String, Object>>) document.get("endpoints");

//                            testing purpose
//                            textPop.setText(endpoints.get(0).get("answer").toString());

                            if (endpoints == null) {
                                textPop.setText("The room no longer exists");
                                joinCode.setText("Code: ");

                                return;
                            }else{
//                                should start the game and enter in map page
                                startActivities(new Intent[]{new Intent(JoinActivity.this, MapsActivity.class)});
                            }
                        }
                    }
                } else {
                        Log.d(TAG, "Error retrieving documents: ", task.getException());
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