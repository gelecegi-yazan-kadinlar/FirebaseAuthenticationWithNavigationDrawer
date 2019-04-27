package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTextNote;
    //Real Time Database için
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    //Giriş yapan kullanıcı bilgilerini almak için
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editTextNote = (EditText) findViewById(R.id.editTextNote);
        Button buttonAddNote = (Button) findViewById(R.id.buttonAddNote);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = editTextNote.getText().toString();
                if(!note.isEmpty()){
                    addNote(note);
                    Toast.makeText(AddNoteActivity.this, "Not eklendi",
                            Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                else
                    editTextNote.setError("Not boş bırakılamaz!");
            }
        });
    }
    public void addNote(String note){
        String key = dbRef.push().getKey();
        dbRef.child("Notes").child(user.getUid()).child(key).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddNoteActivity.this, "Başarılı", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(AddNoteActivity.this, "Başarısız", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
