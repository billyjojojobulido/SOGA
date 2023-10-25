package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class JoinActivity extends AppCompatActivity {

    private Button confirmBtn;
    private TextView joinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        confirmBtn = findViewById(R.id.confirmBtn);
        joinCode = findViewById(R.id.textJoinCode);

    }

//    the user need to click the button to confirm their join codes
    private void onButtonClick(View view){

    }

//    after entering the code, it will be checked to the firebase to see if the room truly exists
    private void checkJoin(){

    }

//    after checking the join code, if true then download the game details and jump to map page
//    else jump to the main page or ask for the code again
    private void forward(){

    }



}