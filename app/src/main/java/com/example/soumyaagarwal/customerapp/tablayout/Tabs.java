package com.example.soumyaagarwal.customerapp.tablayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.widget.FrameLayout;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerLogin;
import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.MyProfile.MyProfile;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.drawer;
import com.example.soumyaagarwal.customerapp.helper.MarshmallowPermissions;

public class Tabs extends drawer implements TabLayout.OnTabSelectedListener {

    private TabLayout tab;
    private ViewPager vpager;
    int page;
    CustomerSession session;
    private MarshmallowPermissions marshmallowPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new CustomerSession(getApplicationContext());

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        getLayoutInflater().inflate(R.layout.activity_tabs, frame);
        if(session.get_ShortCutInstalled()==false)
        {
            createShortCut();
        }


        marshmallowPermissions = new MarshmallowPermissions(this);
        if (!marshmallowPermissions.checkPermissionForReadExternalStorage())
            marshmallowPermissions.requestPermissionForReadExternalStorage();

        if (!marshmallowPermissions.checkPermissionForCamera())
            marshmallowPermissions.requestPermissionForCamera();
        if (!marshmallowPermissions.checkPermissionForExternalStorage())
            marshmallowPermissions.requestPermissionForExternalStorage();
        if (!marshmallowPermissions.checkPermissionForLocations())
            marshmallowPermissions.requestPermissionForLocations();

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
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        Tabs.super.onBackPressed();
                    }


                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
    public void createShortCut(){
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), CustomerLogin.class));
        sendBroadcast(shortcutintent);
        session.set_ShortCutInstalled();

    }



}