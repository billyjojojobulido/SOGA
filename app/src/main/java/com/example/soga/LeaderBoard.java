package com.example.soga;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LeaderBoard extends AppCompatActivity {

    private long startTime;
    private long endTime;
    private int steps;
    private String user;
    private FirebaseFirestore db;
    String[] dataArray;
    private HashMap<String, Long> userRank = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
//        CollectionReference collectionRef = db.collection("userInfo");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        // Perform a query to get all documents in the collection
        db.collection("userInfo")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                System.out.println( document.getId() + " => " + document.getData());
                            startTime = (long)document.getData().get("startTime");
                            endTime = (long)document.getData().get("endTime");
                            user = (String)document.getData().get("username");
                            userRank.put(user,endTime-startTime);

                        }
                        Map<String, Long> sortedMap = sortByValues(userRank);
                        dataArray = sortedMap.keySet().toArray(new String[0]);

                        ArrayAdapter adapter = new ArrayAdapter<String>(LeaderBoard.this,R.layout.activity_list_view,R.id.listText,dataArray);
                        ListView listView = findViewById(R.id.LeaderList);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String selectedItem = (String) parent.getItemAtPosition(position);
                                Toast.makeText(getApplicationContext(), "Clicked: " + selectedItem, Toast.LENGTH_SHORT).show();
                            }
                        });

                        System.out.println(dataArray[0]);
                        // Print the sorted map
//                            for (Map.Entry<String, Long> entry : sortedMap.entrySet()) {
//                                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//                            }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        // Sort the HashMap by values
//        System.out.println(userRank);
//        System.out.println(dataArray[0]);
//        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_leader_board,dataArray);
//        ListView listView = findViewById(R.id.LeaderList);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItem = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), "Clicked: " + selectedItem, Toast.LENGTH_SHORT).show();
//            }
//        });

//

    }

    public void onClickBack(View view){
        startActivities(new Intent[]{new Intent(this, MainActivity.class)});
    }
    // Method to sort a HashMap by its values
    private static Map<String, Long> sortByValues(Map<String, Long> map) {
        List<Map.Entry<String, Long>> list = new LinkedList<>(map.entrySet());

        // Sort the list based on values
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Map.Entry::getValue));
        }

        // Convert the sorted list back to a LinkedHashMap to maintain the order
        Map<String, Long> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}