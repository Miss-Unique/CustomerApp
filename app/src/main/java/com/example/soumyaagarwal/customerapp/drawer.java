package com.example.soumyaagarwal.customerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.Model.CustomerAccount;
import com.example.soumyaagarwal.customerapp.MyProfile.ContactCoordinator;
import com.example.soumyaagarwal.customerapp.MyProfile.MyProfile;
import com.example.soumyaagarwal.customerapp.MyProfile.phonebook;
import com.example.soumyaagarwal.customerapp.notification.NotificationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CustomerSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new CustomerSession(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView nav_name = (TextView) header.findViewById(R.id.nav_name);
        nav_name.setText(session.getName());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.first:
                Intent intent2 = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.second:
                Intent intent = new Intent(getApplicationContext(), ContactCoordinator.class);
                startActivity(intent);
                finish();
                break;
            case R.id.third:
                Intent intent1 = new Intent(getApplicationContext(), phonebook.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.fourth:
                //TODO About the firm
                break;
            case R.id.fifth:
                final AlertDialog customerAccountDialog;

                customerAccountDialog = new AlertDialog.Builder(this)
                        .setView(R.layout.account_info_layout)
                        .create();
                customerAccountDialog.show();
                final Button edit;
                final EditText total, advance, balance;
                total = (EditText) customerAccountDialog.findViewById(R.id.total);
                advance = (EditText) customerAccountDialog.findViewById(R.id.advance);
                balance = (EditText) customerAccountDialog.findViewById(R.id.balance);
                edit = (Button) customerAccountDialog.findViewById(R.id.okButton);
                DatabaseReference dbaccountinfo = DBREF.child("Customer").child(session.getUsername()).child("Account").getRef();
                dbaccountinfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            CustomerAccount customerAccount = dataSnapshot.getValue(CustomerAccount.class);
                            total.setText("Total "+customerAccount.getTotal() + "");
                            advance.setText("Advance Paid "+customerAccount.getAdvance() + "");
                            balance.setText("Balance "+(customerAccount.getTotal() - customerAccount.getAdvance()) + "");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customerAccountDialog.dismiss();
                    }
                });
                    break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notif:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

}
