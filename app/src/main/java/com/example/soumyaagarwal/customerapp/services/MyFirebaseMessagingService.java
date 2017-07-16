package com.example.soumyaagarwal.customerapp.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.chat.ChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;


/**
 * Created by ghanendra on 20/06/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG1 = "MyFireMesgService";

    Bitmap largeIcon;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("remote message downloaded");
        Log.d(TAG1, "data1: " + remoteMessage.getData().get("sendertimestamp"));
        Log.d(TAG1, "data2: " + remoteMessage.getData().get("msgid"));
        Log.d(TAG1, "data3: " + remoteMessage.getData().get("chatref"));

        String type = remoteMessage.getData().get("type");
        String msg;
        if(type.equals("text"))
            msg = remoteMessage.getData().get("body");

        else
            msg = "Sent a "+type;
        String senderuid = remoteMessage.getData().get("senderuid");
        String title = remoteMessage.getData().get("title");

        String timestamp = remoteMessage.getData().get("sendertimestamp");
        String chatref = remoteMessage.getData().get("chatref");
        String msgid = remoteMessage.getData().get("msgid");
        if (msg != null && timestamp != null && chatref != null && msgid != null)
            sendNotification(title, msg, timestamp, chatref, msgid,senderuid);

    }


    private void sendNotification(String title, String msg, String timestamp, String chatref, String msgid,String senderuid) throws NullPointerException {
        final DatabaseReference dbr = DBREF.child("Chats").child(chatref).child("ChatMessages").child(msgid).child("status");
        Intent intent = new Intent(this, ChatActivity.class);

        intent.putExtra("otheruserkey",senderuid);
        intent.putExtra("dbTableKey",chatref);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        System.out.println(dbr + " setting value to 2 for " + chatref);
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.getValue().toString().matches("3"))
                        dbr.setValue("2");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_chat_white)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
//        else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }

        return isInBackground;
    }
}
