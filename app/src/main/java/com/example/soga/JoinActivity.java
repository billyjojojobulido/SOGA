package com.example.soga;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        textPop.setText("");

        String inputCode = joinCode.getText().toString();

        if (inputCode.isEmpty()){
            textPop.setText("Please input valid code.");
            joinCode.setText("Code: ");
            return;
        }else{
            confirmBtn.setText("Checking");
            checkJoin(inputCode);
        }




    }

//    after entering the code, it will be checked to the firebase to see if the room truly exists
    private void checkJoin(String inputCode){
        db.collection("rooms").whereEqualTo("code", inputCode).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()){
                        textPop.setText("No such a room");
                    }else {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // Get the "endpoints" field which is a hashmap
                            endpoints = (ArrayList<HashMap<String, Object>>) document.get("endpoints");

//                            testing purpose
//                            textPop.setText(endpoints.get(0).get("answer").toString());

                            if (endpoints != null) {
                                // Do something with the "endpoints" hashmap
                            }
                        }
                    }
                } else {
                        Log.d(TAG, "Error retrieving documents: ", task.getException());
                }
            }
        });



    }

//    after checking the join code, if true then download the game details and jump to map page
//    else jump to the main page or ask for the code again

    public ArrayList<HashMap<String, Object>> getEndpoints() {
        return endpoints;
    }
}