package com.example.soga;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class JoinActivity extends AppCompatActivity {

    private Button confirmBtn;
    private TextView joinCode;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        confirmBtn = findViewById(R.id.confirmBtn);
        joinCode = findViewById(R.id.textJoinCode);

        db = FirebaseFirestore.getInstance();


    }

//    the user need to click the button to confirm their join codes
    public void onButtonClickCode(View view){

        confirmBtn.setText("Checking");
        String inputCode = joinCode.getText().toString();

        if (inputCode.isEmpty()){
            joinCode.setText("Please input valid code.");
            return;
        }

        db.collection("rooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String codeFromDb = document.getString("code");
                        if (inputCode.equals(codeFromDb)) {
                            confirmBtn.setText("Joining");

                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }

//    after entering the code, it will be checked to the firebase to see if the room truly exists
    private void checkJoin(){

    }

//    after checking the join code, if true then download the game details and jump to map page
//    else jump to the main page or ask for the code again
    private void forward(){

    }



}