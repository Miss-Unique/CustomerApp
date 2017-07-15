package com.example.soumyaagarwal.customerapp.CheckInternetConnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.soumyaagarwal.customerapp.CustomerApp;

public class NetWatcher
        extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public NetWatcher() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {

            if (activeNetwork.isConnected()) {

            } else {

            }
        }
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) CustomerApp.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {

            if (activeNetwork.isConnected()) {
               // Intent intent1 = new Intent(context, SetReminderAlarmService.class);
                //Intent intent2 = new Intent(context, MarkedBookReminderService.class);
               // context.startService(intent1);
                //context.startService(intent2);

            }
            else {
             /*   Intent intent1 = new Intent(context, SetReminderAlarmService.class);
                context.stopService(intent1);
                Intent intent2 = new Intent(context, MarkedBookReminderService.class);
                context.stopService(intent2);
           */

            }
        }

        boolean status = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        return status;
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}