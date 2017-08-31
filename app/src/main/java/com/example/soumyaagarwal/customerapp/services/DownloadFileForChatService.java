package com.example.soumyaagarwal.customerapp.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.Task.TaskDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.IDN;

import static com.example.soumyaagarwal.customerapp.CustomerApp.AppName;
import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class DownloadFileForChatService extends IntentService {

    String type, url, dbTableKey, Id;

    public DownloadFileForChatService() {
        super("Upload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            type = intent.getStringExtra("type");
            url = intent.getStringExtra("url");
            dbTableKey = intent.getStringExtra("dbTableKey");
            Id = intent.getStringExtra("Id");
            downloadFile(url, type, dbTableKey);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void downloadFile(final String url, final String type, final String dbTableKey)
    {
        final DatabaseReference[] dbChat = {DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef()};
        final StorageReference str = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        switch (type) {
            case "photo":
                final String[] ext = new String[1];
                str.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        ext[0] =  storageMetadata.getContentType();
                        int p = ext[0].lastIndexOf("/");
                        String l = "."+ext[0].substring(p + 1);

                        File rootPath = new File(Environment.getExternalStorageDirectory(), AppName+"/Docs");
                        if (!rootPath.exists()) {
                            rootPath.mkdirs();
                        }

                        String uriSting = System.currentTimeMillis() + l;
                        final File localFile = new File(rootPath, uriSting);
                        final String localuri = (rootPath.getAbsolutePath() + "/" + uriSting);
                        str.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                                dbChat[0] = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();
                                dbChat[0].child(Id).child("othersenderlocal_storage").setValue(localuri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                            }
                        });
                    }
                });

                break;
            case "doc":

                final String[] ext1 = new String[1];
                str.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        ext1[0] =  storageMetadata.getContentType();
                        int p = ext1[0].lastIndexOf("/");
                        String l = "."+ ext1[0].substring(p + 1);
                        String uriSting1 = System.currentTimeMillis() + l;
                        File rootPath = new File(Environment.getExternalStorageDirectory(), AppName+"/Docs");
                        if (!rootPath.exists()) {
                            rootPath.mkdirs();
                        }
                        final File localdocFile = new File(rootPath, uriSting1);
                        final String localdocuri = (rootPath.getAbsolutePath() + "/" + uriSting1);
                        str.getFile(localdocFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                dbChat[0] = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();
                                dbChat[0].child(Id).child("othersenderlocal_storage").setValue(localdocuri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                            }
                        });
                    }
                });

                break;
        }
    }
}