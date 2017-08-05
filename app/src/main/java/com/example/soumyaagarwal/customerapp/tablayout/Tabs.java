package com.example.soumyaagarwal.customerapp.tablayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.MyProfile.MyProfile;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.drawer;
import com.example.soumyaagarwal.customerapp.helper.MarshmallowPermissions;
import com.example.soumyaagarwal.customerapp.notification.NotificationActivity;

public class Tabs extends drawer implements TabLayout.OnTabSelectedListener {

    private TabLayout tab;
    private ViewPager vpager;
    int page;
    CustomerSession session;
    private MarshmallowPermissions marshmallowPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        getLayoutInflater().inflate(R.layout.activity_tabs, frame);

        marshmallowPermissions = new MarshmallowPermissions(this);
        if (!marshmallowPermissions.checkPermissionForCamera())
            marshmallowPermissions.requestPermissionForCamera();
        if (!marshmallowPermissions.checkPermissionForExternalStorage())
            marshmallowPermissions.requestPermissionForExternalStorage();
        if (!marshmallowPermissions.checkPermissionForLocations())
            marshmallowPermissions.requestPermissionForLocations();

        session = new CustomerSession(getApplicationContext());
        if (getIntent().getExtras() != null)
            page = getIntent().getIntExtra("page", 0);
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
}