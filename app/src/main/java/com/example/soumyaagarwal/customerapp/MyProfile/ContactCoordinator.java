package com.example.soumyaagarwal.customerapp.MyProfile;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.Model.Coordinator;
import com.example.soumyaagarwal.customerapp.Model.Customer;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.adapter.coordinator_adapter;
import com.example.soumyaagarwal.customerapp.chat.ChatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class ContactCoordinator extends AppCompatActivity implements coordinator_adapter.coordinator_adapterListener {

    RecyclerView rec_coordinator_list;
    ArrayList<Coordinator> coordinator_list = new ArrayList<Coordinator>();
    coordinator_adapter coordinator_adapter;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference dbCoordinator;
    CustomerSession session;
    String dbTablekey,mykey,id,num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_coordinator);

        rec_coordinator_list = (RecyclerView)findViewById(R.id.coordinator_list);
        session = new CustomerSession(getApplicationContext());

        coordinator_adapter = new coordinator_adapter(coordinator_list,getApplicationContext(),this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rec_coordinator_list.setLayoutManager(linearLayoutManager);
        rec_coordinator_list.setItemAnimator(new DefaultItemAnimator());
        rec_coordinator_list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rec_coordinator_list.setAdapter(coordinator_adapter);

        dbCoordinator = DBREF.child("Coordinator").getRef();

        LoadData();
    }

    void LoadData()
    {

        dbCoordinator.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                {
                    Coordinator coordinator = dataSnapshot.getValue(Coordinator.class);
                    coordinator_list.add(coordinator);
                    coordinator_adapter.notifyDataSetChanged();
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

    private void checkChatref(final String mykey, final String otheruserkey) {
        DatabaseReference dbChat = DBREF.child("Chats").child(mykey + otheruserkey).getRef();
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("query1" + mykey + otheruserkey);
                System.out.println("datasnap 1" + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    System.out.println("datasnap exists1" + dataSnapshot.toString());
                    dbTablekey = mykey + otheruserkey;
                    goToChatActivity();
                } else {
                    checkChatref2(mykey, otheruserkey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkChatref2(final String mykey, final String otheruserkey) {
        final DatabaseReference dbChat = DBREF.child("Chats").child(otheruserkey + mykey).getRef();
        dbTablekey = otheruserkey + mykey;
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("query1" + otheruserkey + mykey);
                    goToChatActivity();
                } else {
                    DBREF.child("Users").child("Userchats").child(mykey).child(otheruserkey).setValue(dbTablekey);
                    DBREF.child("Users").child("Userchats").child(otheruserkey).child(mykey).setValue(dbTablekey);
                    goToChatActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToChatActivity() {
        Intent in = new Intent(this, ChatActivity.class);
        in.putExtra("dbTableKey", dbTablekey);
        in.putExtra("otheruserkey", id);
        startActivity(in);
    }

    @Override
    public void onMSGMEclicked(int position) {
        Coordinator coordinator = coordinator_list.get(position);
        id = coordinator.getUsername();
        mykey = session.getUsername();
        checkChatref(mykey, id);
    }

    @Override
    public void onCALLMEclicked(int position)
    {
        Coordinator coordinator = coordinator_list.get(position);
        num = coordinator.getContact();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + num));
        startActivity(callIntent);
    }
}
