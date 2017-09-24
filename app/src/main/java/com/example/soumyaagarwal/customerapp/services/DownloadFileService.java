package com.example.soumyaagarwal.customerapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static com.example.soumyaagarwal.customerapp.CustomerApp.AppName;

public class DownloadFileService extends IntentService {

    String url;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    public DownloadFileService() {
        super("Upload");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int icon = R.mipmap.ic_download;
        mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        mBuilder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_download))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getApplicationContext().getResources().getColor(R.color.white))
                .setContentText("Downloading Quotation...");
        synchronized (this) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            url = intent.getStringExtra("url");
            downloadFile(url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void downloadFile(final String url) {

        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        final String[] ext = new String[1];
        mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                ext[0] = storageMetadata.getContentType();
                int p = ext[0].lastIndexOf("/");
                String l = "." + ext[0].substring(p + 1);
                final String open = ext[0].substring(0, p);
                File rootPath = new File(Environment.getExternalStorageDirectory(), AppName + "/Images");
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                String uriSting = System.currentTimeMillis() + l;
                final File localFile = new File(rootPath, uriSting);

                mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                        Toast.makeText(DownloadFileService.this, "Downloaded Quotation", Toast.LENGTH_SHORT).show();
                        updateNotification("Succesfully Downloaded", open, localFile);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(DownloadFileService.this, "Download Failed", Toast.LENGTH_SHORT).show();
                        updateNotification("Download failed", open, localFile);
                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                       }
                });
            }
        });

    }

    private void updateNotification(String information, String open, File localFile) {
        notificationManager.cancel(0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Toast.makeText(this, open, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (localFile.exists()) {
            Uri pdfPath = Uri.fromFile(localFile);
            if (open.equals("application")) {
                intent.setDataAndType(pdfPath, "application/pdf");
            }
            else {
                intent.setDataAndType(pdfPath, "image/*");
            }
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        int icon = R.mipmap.ic_launcher;
        mBuilder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getApplicationContext().getResources().getColor(R.color.white))
                .setContentText(information)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(information))
                .setContentIntent(contentIntent);

        notificationManager.notify(0, mBuilder.build());
        stopSelf();

    }


}
