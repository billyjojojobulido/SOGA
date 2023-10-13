package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RoomManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);
    }

    public void checkLocation(View view){

        EditText addressInput = findViewById(R.id.location_input);
        String address = addressInput.getText().toString();

        if (address.length() == 0) {
            Toast.makeText(this, "Input an address first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String query = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    address
                    + "&key=AIzaSyB_4H-LgBByty7rCuZR4DSagLK6c7y1rYY";

            URL url = new URL(query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // timeout checking
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Retrieve Response Status Code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();

                

                // TODO
                // 注意：你需要在主线程之外处理UI操作
            } else {
                // Handle Error
                Toast.makeText(this, "Request Failed", Toast.LENGTH_SHORT).show();
            }

            connection.disconnect();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void createEndpoint(View view){
        LinearLayout cardContainer = findViewById(R.id.card_layout);

        // Task Selected
        Spinner spinner = findViewById(R.id.end_task);
        String task = spinner.getSelectedItem().toString();

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
        textView.setText(task);
        textView.setPadding(16, 16, 16, 16);

        // Add TextView To CardView
        newCard.addView(textView);

        // Add new CardView to Container
        cardContainer.addView(newCard);

    }

}