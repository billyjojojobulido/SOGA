package com.example.soga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.soga.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // 用户已登录，跳转到主界面
            // 你可以在这里启动主界面的 Activity 或 Fragment
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish(); // 可以选择关闭当前活动
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish(); // 可以选择关闭当前活动
            // 用户未登录，显示登录界面
            // 你可以在这里启动登录界面的 Activity 或 Fragment
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }


    public void openRegister(View view){
        startActivities(new Intent[]{new Intent(this, Register.class)});
    }

}