package com.example.soumyaagarwal.customerapp.tablayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.notification.NotificationActivity;

public class Tabs extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private TabLayout tab;
    private ViewPager vpager;
    int page;
    CustomerSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        session = new CustomerSession(getApplicationContext());
        if(getIntent().getExtras()!=null)
            page = getIntent().getIntExtra("page",0);
        else
            page = 0;

        tab = (TabLayout) findViewById(R.id.tabLayout);

        tab.addTab(tab.newTab().setText("Tasks"));
        tab.addTab(tab.newTab().setText("Chat"));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        vpager = (ViewPager) findViewById(R.id.pager);

        pager adapter = new pager(getSupportFragmentManager(), tab.getTabCount());

        vpager.setAdapter(adapter);

        tab.setOnTabSelectedListener(this);
        vpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        vpager.setCurrentItem(page);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.notif:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                intent.putExtra("Username",session.getUsername());
                startActivity(intent);
                break;
        }
        return true;
    }
}