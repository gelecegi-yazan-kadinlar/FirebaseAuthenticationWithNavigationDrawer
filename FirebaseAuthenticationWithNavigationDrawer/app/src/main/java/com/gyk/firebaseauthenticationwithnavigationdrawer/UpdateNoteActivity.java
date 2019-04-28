package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateNoteActivity extends AppCompatActivity {
    private EditText editTextNote;
    //Real Time Database için
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    //Giriş yapan kullanıcı bilgilerini almak için
    private FirebaseAuth auth;
    private FirebaseUser user;
    String key;
    String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        editTextNote = (EditText) findViewById(R.id.editTextNote);
        try {
            key = getIntent().getStringExtra("key");
            note = getIntent().getStringExtra("note");

        } catch (Exception e) {
            Log.w("IntentExtra", "onCreate: ", e);
            onBackPressed();
            Toast.makeText(this, "Beklenmedik bir hata!", Toast.LENGTH_SHORT).show();
        }

        editTextNote.setText(note);


        Button buttonAddNote = (Button) findViewById(R.id.buttonAddNote);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = editTextNote.getText().toString();
                if (!note.isEmpty()) {
                    updateNote(note, key);

                } else
                    editTextNote.setError("Not boş bırakılamaz!");
            }
        });
    }

    public void updateNote(String note, String key) {

        dbRef.child("Notes").child(user.getUid()).child(key).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateNoteActivity.this, "Not Güncellendi",
                            Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else
                    Toast.makeText(UpdateNoteActivity.this,
                            "Başarısız" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}