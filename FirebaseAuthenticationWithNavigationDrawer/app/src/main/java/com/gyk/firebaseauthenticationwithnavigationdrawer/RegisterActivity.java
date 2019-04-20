package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepeatedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();

        editTextEmail = (EditText)
                findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)
                findViewById(R.id.editTextPassword);
        editTextRepeatedPassword = (EditText)
                findViewById(R.id.editTextRepeatedPassword);
        Button buttonSignUp = (Button)
                findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String repeatedPassword = editTextRepeatedPassword.getText().toString();
                if (validate(email, password, repeatedPassword)) {
                    signUp(email, password);
                }
            }
        });
    }

    private void signUp(final String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    //intent.putExtra("email",email);
                    startActivity(intent);

                }
            }
        });
    }

    private boolean validate(String email, String password, String repeatedPassword) {
        boolean valid = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            this.editTextEmail.setError("Geçerli bir mail adresi girin!");
        } else if (password.isEmpty() || password.length() < 5 || password.length() > 32) {
            valid = false;
            this.editTextPassword.
                    setError("Parola boş bırakılamaz! Parolanız 6-32 " +
                            "karakter uzunluğunda olmalıdır!");
        } else if (repeatedPassword.isEmpty()) {
            valid = false;
            this.editTextRepeatedPassword.setError("Parola tekrarı boş bırakılamaz.");
        } else if (!password.equals(repeatedPassword)) {
            valid = false;
            this.editTextRepeatedPassword.setError("Parolalar uyuşmuyor!");
        }

        return valid;
    }

}
