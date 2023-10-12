package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

// 创建新的CardView
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
        textView.setText("新的卡片视图");
        textView.setPadding(16, 16, 16, 16);

        // 将TextView添加到CardView中
        newCard.addView(textView);

        // 将新的CardView添加到容器中
        cardContainer.addView(newCard);

        Toast.makeText(this, "HAHAHAHAH", Toast.LENGTH_SHORT).show();

    }

}