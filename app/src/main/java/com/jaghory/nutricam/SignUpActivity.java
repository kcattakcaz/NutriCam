package com.jaghory.nutricam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * A sign up screen that offers sign up via username/password.
 */
public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        Button signup_btn = (Button) findViewById(R.id.signUp_btn);
        final EditText username = (EditText) findViewById(R.id.signup_username);
        final EditText password = (EditText) findViewById(R.id.signup_password);

        final Firebase fRef = new Firebase("https://nutricam.firebaseio.com/");


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            fRef.createUser(username.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Intent finRegister = new Intent(getApplicationContext(),LoginActivity.class);
                    finRegister.putExtra("username",username.getText().toString());
                    finRegister.putExtra("password",password.getText().toString());
                    startActivity(finRegister);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    String err = new String();
                    String usr_name = username.getText().toString();
                    switch (firebaseError.getCode()){
                        case FirebaseError.EMAIL_TAKEN: err = usr_name+" is taken, try again.";
                            break;
                        case FirebaseError.INVALID_EMAIL: err = usr_name+" is not a valid e-mail, try again.";
                            break;
                        case FirebaseError.INVALID_PASSWORD: err = "Not a valid password, try again.";
                            break;
                        default: err = "Unknown error creating account.";
                            break;
                    }
                    Snackbar.make(v, err, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            }
        });

    }
}
