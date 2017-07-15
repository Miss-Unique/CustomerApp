package com.example.soumyaagarwal.customerapp.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.Model.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class UploadFileService extends Service {
    private static String LOG_TAG = "UploadFileService";
    private IBinder mBinder = new MyBinder();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
       }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }


    public class MyBinder extends Binder {
        public UploadFileService getService() {
            return UploadFileService.this;
        }
    }
    public void uploadFile(final String path, String type, final String mykey, final String otheruserkey, final String receiverToken, final String dbTableKey, final DatabaseReference dbChat, final String timestamp, final long id) {
        //if there is a file to upload
        //put case
        System.out.println("uri found" + Uri.fromFile(new File(path)));
        if (Uri.fromFile(new File(path)) != null) {
            //displaying a progress dialog while upload is going on
            StorageReference riversRef = mStorageRef.child(dbTableKey).child("files");

            switch (type) {
                case "photo":
                    //create msg with 2 extra nodes

                    riversRef.putFile(Uri.fromFile(new File(path)))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    ChatMessage cm = new ChatMessage(mykey,otheruserkey,timestamp,"photo",id+"","0",downloadUrl.toString(),receiverToken,dbTableKey,100,path,"");
                                    dbChat.child(String.valueOf(id)).setValue(cm);
                                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                    DBREF.child("Chats").child(dbTableKey).child("lastMsg").setValue(id);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dbChat.child(String.valueOf(id)).removeValue();
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                    dbChat.child(String.valueOf(id)).child("percentUploaded").setValue(progress);
                                    //displaying percentage in progress dialog
      //                              progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                }
                            });
                            break;
                    //if there is not any file
                case "doc":
                    //create msg with 2 extra nodes

                    riversRef.putFile(Uri.fromFile(new File(path)))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    ChatMessage cm = new ChatMessage(mykey,otheruserkey,timestamp,"doc",id+"","0",downloadUrl.toString(),receiverToken,dbTableKey,100,path,"");
                                    dbChat.child(String.valueOf(id)).setValue(cm);
                                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                    DBREF.child("Chats").child(dbTableKey).child("lastMsg").setValue(id);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dbChat.child(String.valueOf(id)).removeValue();
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                    dbChat.child(String.valueOf(id)).child("percentUploaded").setValue(progress);
                                    //displaying percentage in progress dialog
                                    //                              progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                }
                            });

                    break;
            }

        }
    }
 }
