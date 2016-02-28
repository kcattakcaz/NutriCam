package com.jaghory.nutricam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        Button login_btn = (Button) findViewById(R.id.login_btn);
        Button newuser_btn = (Button) findViewById(R.id.newUser_btn);
        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);

        final Firebase fRef = new Firebase("https://nutricam.firebaseio.com/");


        final Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler(){
            @Override
            public void onAuthenticated(AuthData authData){
                fRef.child("users").child(authData.getUid()).child("login").setValue("jenna doesn't know why doctor who is awesome but dena does so dena is awesome");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError){
                Snackbar.make(password, firebaseError.toString(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        };

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fRef.authWithPassword(username.getText().toString(), password.getText().toString(), authResultHandler );
            }
        });

        newuser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
    }
}
