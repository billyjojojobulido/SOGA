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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    // 在这里执行网络请求
//                    // 例如，使用HttpURLConnection或者第三方网络请求库
//                    // 这里的网络请求代码应该在后台线程执行
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

                    connection.disconnect();
//
//                    // 如果需要更新UI，使用Handler或其他机制来在主线程中执行
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 更新UI或其他操作
                            Toast.makeText(RoomManagement.this, response, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    // 处理异常
                }
            }
        }).start();
//
//        try {
//            String query = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
//                    address
//                    + "&key=AIzaSyB_4H-LgBByty7rCuZR4DSagLK6c7y1rYY";
//
//            URL url = new URL(query);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            // timeout checking
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//
//            // Retrieve Response Status Code
//            int responseCode = connection.getResponseCode();
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                InputStream inputStream = connection.getInputStream();
//
//                Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
//                StringBuilder sb = new StringBuilder();
//                for (int c; (c = in.read()) >= 0; ) {
//                    sb.append((char) c);
//                }
//                String response = sb.toString();
//
//                // Json Parser
//                JsonElement jsonElement = JsonParser.parseString(response);
//
////                if (jsonElement.isJsonObject()) {
////                    JsonObject jsonObject = jsonElement.getAsJsonObject();
////
////                    // 从JsonObject中获取特定键的值
////                    JsonArray results = jsonObject.getAsJsonArray("results");
////                    JsonElement location = results.get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location");
////                    String lat = location.getAsJsonObject().get("lat").getAsString();
////                    String lng = location.getAsJsonObject().get("lng").getAsString();
////
////                    // 现在你可以使用name和age变量
////                    Toast.makeText(this, lat, Toast.LENGTH_SHORT).show();
////                    Toast.makeText(this, lng, Toast.LENGTH_SHORT).show();
////
////                }
//                Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();
////
////                return Decoder.rootDecode((JSONObject) parser.parse(response));
//
//                // TODO
//                // 注意：你需要在主线程之外处理UI操作
//            } else {
//                // Handle Error
//                Toast.makeText(this, "Request Failed", Toast.LENGTH_SHORT).show();
//            }
//
//            connection.disconnect();
//        } catch (IOException e) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//        }
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