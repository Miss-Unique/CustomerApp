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

import com.example.soumyaagarwal.customerapp.Model.NameAndStatus;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.chat.ChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.List;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;


/**
 * Created by ghanendra on 20/06/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG1 = "MyFireMesgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //TODO get the type of notifiction handle separately for chats, quotation and normal assigned tasks
        String type = remoteMessage.getData().get("type");
        if(type.equals("chat")||type==null){
            String msg = remoteMessage.getData().get("body");
            String senderuid = remoteMessage.getData().get("senderuid");
            String chatref = remoteMessage.getData().get("chatref");
            String msgid = remoteMessage.getData().get("msgid");
            if (msg != null && chatref != null && msgid != null)
                sendChatNotification(msg,chatref, msgid,senderuid);

        }
        else
        {
            String body = remoteMessage.getData().get("body");
            String senderuid = remoteMessage.getData().get("senderuid");
            String taskId = remoteMessage.getData().get("taskId");
            String id = remoteMessage.getData().get("id");
            if(body!=null&&taskId!=null&&senderuid!=null)
                sendGeneralNotification(body,senderuid,taskId,id);
        }
    }

    private void sendGeneralNotification(final String body, String senderuid, String taskId, final String id) {
        Intent intent = new Intent(this, ChatActivity.class); //TODO set the Intent to notification activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(senderuid).getRef();
        dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                            .setSmallIcon(R.mipmap.ic_chat_white)
                            .setContentTitle("New Notification from " + nameAndStatus.getName())
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String notifid = id.substring(8);
                    notificationManager.notify(Integer.parseInt(notifid) /* ID of notification */, notificationBuilder.build());


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendChatNotification(final String msg, String chatref, final String msgid, String senderuid) throws NullPointerException {
        final DatabaseReference dbr = DBREF.child("Chats").child(chatref).child("ChatMessages").child(msgid).child("status");
        Intent intent = new Intent(this, ChatActivity.class);

        intent.putExtra("otheruserkey",senderuid);
        intent.putExtra("dbTableKey",chatref);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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

        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(senderuid).getRef();
        dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                            .setSmallIcon(R.mipmap.ic_chat_white)
                            .setContentTitle("New Message from " + nameAndStatus.getName())
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String notifid = msgid.substring(8);
                    notificationManager.notify(Integer.parseInt(notifid) /* ID of notification */, notificationBuilder.build());


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}