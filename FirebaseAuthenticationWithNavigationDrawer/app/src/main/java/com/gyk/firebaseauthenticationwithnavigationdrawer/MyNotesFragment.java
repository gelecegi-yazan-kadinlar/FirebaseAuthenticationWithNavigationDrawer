package com.gyk.firebaseauthenticationwithnavigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyNotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyNotesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private ListView listViewNote;
    private List<String> noteList;
    private List<String> keyList;
    private ProgressBar progressBar;
    private TextView textView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyNotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyNotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyNotesFragment newInstance(String param1, String param2) {
        MyNotesFragment fragment = new MyNotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_notes, container, false);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        noteList = new ArrayList<>();
        keyList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        listViewNote = (ListView)
                view.findViewById(R.id.listViewNotes);

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, noteList);
        listViewNote.setAdapter(arrayAdapter);

        textView = new TextView(getContext());
        textView.setText("Hiç notun yok :(");
        RelativeLayout relativeLayout = (RelativeLayout)
                view.findViewById(R.id.relativeLayout);
        relativeLayout.addView(textView);

        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)textView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textView.setLayoutParams(layoutParams);
        textView.setVisibility(View.GONE);

        registerForContextMenu(listViewNote);

        listViewNote.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

                if(view.getId() == R.id.listViewNotes){
                    AdapterView.AdapterContextMenuInfo info =
                            (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                    contextMenu.setHeaderTitle("Not işlemleri");
                    contextMenu.add(0,0,0,"Düzenle");
                    contextMenu.add(0,1,0,"Sil");
                }
            }
        });


        dbRef.child("Notes").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Log.d(TAG, "onDataChange: "+dataSnapshot);
                        noteList.clear();
                        keyList.clear();
                        for (DataSnapshot note : dataSnapshot.getChildren()) {
                            //Log.d(TAG, "onDataChange: "+note.getValue(String.class));
                            keyList.add(note.getKey());
                            Log.d(TAG, "onDataChange: "+note.getKey());
                            noteList.add(note.getValue(String.class));
                        }
                        if(noteList.isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                        }else{
                            textView.setVisibility(View.GONE);
                        }
                        arrayAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        ImageButton addNote = (ImageButton)
                view.findViewById(R.id.imageButtonAddNote);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAddNoteActivity();
            }
        });
        return view;
    }
    public void deleteNote(String id){
        dbRef.child("Notes").child(user.getUid()).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Silindi", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Hata:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean val = false;
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case 0:
                Toast.makeText(getContext(), "Düzenle", Toast.LENGTH_SHORT).show();
                updateNote(keyList.get(info.position),noteList.get(info.position));
                val = true;

                break;
            case 1:
                Toast.makeText(getContext(), "Sil", Toast.LENGTH_SHORT).show();
                deleteNote(keyList.get(info.position));
                val = true;
                break;
        }

        return val;
    }

    private void updateNote(String key, String note) {
        Intent intent = new Intent(getActivity(),UpdateNoteActivity.class);
        intent.putExtra("key",key);
        intent.putExtra("note",note);
        startActivity(intent);
    }

    public void goAddNoteActivity() {
        Intent addNoteIntent = new Intent(getActivity(), AddNoteActivity.class);
        startActivity(addNoteIntent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
