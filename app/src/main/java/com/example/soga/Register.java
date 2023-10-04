package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class Register extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }
    public void register(View view){
        EditText email, password;
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.password);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        if(TextUtils.isEmpty(emailText)){
            Toast.makeText(Register.this,"Enter your Email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(passwordText)){
            Toast.makeText(Register.this,"Enter your password",Toast.LENGTH_SHORT).show();
            return;
        }



        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // register successfully
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Register.this,"Successful",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // failed
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(Register.this,errorMessage,Toast.LENGTH_SHORT).show();
                    }
                });




    }

    public void back(View view){
        finish();
    }
}