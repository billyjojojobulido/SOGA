package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RoomManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);
    }

    public void createEndpoint(View view){
        LinearLayout cardContainer = findViewById(R.id.card_layout);


        EditText roomName = findViewById(R.id.room_name);
        EditText roomCapacity = findViewById(R.id.room_capacity);



        Toast.makeText(this, "HAHAHAHAH", Toast.LENGTH_SHORT).show();

    }

}