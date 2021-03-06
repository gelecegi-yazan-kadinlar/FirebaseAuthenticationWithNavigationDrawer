package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        editTextEmail = (EditText)
                findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)
                findViewById(R.id.editTextPassword);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkboxRememberMe);
        Button buttonSignIn = (Button)
                findViewById(R.id.buttonSignIn);
        Button buttonSignUp = (Button)
                findViewById(R.id.buttonSignUp);
        getUserEmail();
        //startActivity(new Intent(LoginActivity.this,MainActivity.class));
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if(validate(email,password)){

                    signIn(email,password);
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignUp();
            }
        });
    }

    private boolean validate(String email,String password){
        boolean valid = true;

        if(email.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            valid = false;
            this.editTextEmail.setError("Geçerli bir mail adresi girin!");
        }
        else if(password.isEmpty() || password.length() < 5 || password.length() > 32 ){
            valid = false;
            this.editTextPassword.
                    setError("Parola boş bırakılamaz! Parolanız 6-32 " +
                            "karakter uzunluğunda olmalıdır!");
        }

        return valid;
    }

    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Giriş yapıldı Main activity'e git
                    if(checkBoxRememberMe.isChecked()){
                        saveUserEmail();
                    }else{
                        deleteUserEmail();

                    }
                    Intent intent =
                            new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Giriş yapılamadı: "+task.getException(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void saveUserEmail(){
        SharedPreferences.Editor editor =
                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
        editor.putString("email", editTextEmail.getText().toString());
        editor.apply();
    }
    public void deleteUserEmail(){
        SharedPreferences.Editor editor =
                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
        editor.remove("email");
        editor.apply();
    }
    public void getUserEmail(){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String restoredText = prefs.getString("email", "");
        if (restoredText != null) {
            editTextEmail.setText(restoredText);
            if(restoredText.length() > 0) {checkBoxRememberMe.setChecked(true);}
        }
    }
    private void goToSignUp(){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }


}
