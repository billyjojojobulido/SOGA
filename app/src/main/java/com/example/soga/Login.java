package com.example.soga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Login extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void openRegister(View view){
        startActivities(new Intent[]{new Intent(this, Register.class)});
    }
    public void signIn(View view){
        EditText email, password;
        email = findViewById(R.id.signInEmail);
        password = findViewById(R.id.signInPassword);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        if(!checkInput(emailText,passwordText)){
            return;
        }
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successfully
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this,"Login successfully!",Toast.LENGTH_SHORT).show();
                        // handle
                        startActivities(new Intent[]{new Intent(this, RoomManagement.class)});
                    } else {
                        // failed
                        // determine the reason of failure

                        String errorMessage = task.getException().getMessage();
                        if (errorMessage.contains("There is no user record corresponding to this identifier")) {
                            //
                            Toast.makeText(Login.this,"User not registered",Toast.LENGTH_SHORT).show();

                        } else if (errorMessage.contains("The password is invalid")) {
                            //
                            Toast.makeText(Login.this,"The password is invalid",Toast.LENGTH_SHORT).show();
//                            showPasswordErrorPrompt();
                        } else {
                            Toast.makeText(Login.this,errorMessage,Toast.LENGTH_SHORT).show();
                            //
//                            showGenericErrorPrompt();
                        }
                    }
                });






    }
    private boolean checkInput(String emailText, String passwordText){
        if(TextUtils.isEmpty(emailText)){
            Toast.makeText(Login.this,"Enter your Email",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(passwordText)){
            Toast.makeText(Login.this,"Enter your password",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}