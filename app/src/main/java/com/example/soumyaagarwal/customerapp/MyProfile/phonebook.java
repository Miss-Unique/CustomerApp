package com.example.soumyaagarwal.customerapp.MyProfile;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import com.example.soumyaagarwal.customerapp.helper.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.soumyaagarwal.customerapp.Model.Phonebook;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.adapter.phonebook_adapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class phonebook extends AppCompatActivity implements phonebook_adapter.phonebook_adapterListener {

    RecyclerView rec_contact_list;
    ArrayList<Phonebook> contact_list = new ArrayList<>();
    phonebook_adapter phonebook_adapter;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference dbCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook);

        rec_contact_list = (RecyclerView) findViewById(R.id.contact_list);

        phonebook_adapter = new phonebook_adapter(contact_list, getApplicationContext(), this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rec_contact_list.setLayoutManager(linearLayoutManager);
        rec_contact_list.setItemAnimator(new DefaultItemAnimator());
        rec_contact_list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rec_contact_list.setAdapter(phonebook_adapter);

        dbCoordinator = DBREF.child("Contacts").getRef();

        LoadData();
    }

    void LoadData() {

        dbCoordinator.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Phonebook phonebook = dataSnapshot.getValue(Phonebook.class);
                    contact_list.add(phonebook);
                    phonebook_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCALLMEclicked(int position) {
        Phonebook phonebook = contact_list.get(position);
        String num = phonebook.getContact();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + num));
        startActivity(callIntent);
    }
}