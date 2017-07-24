package com.example.soumyaagarwal.customerapp.chat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.Space;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.Model.ChatMessage;
import com.example.soumyaagarwal.customerapp.Model.Customer;
import com.example.soumyaagarwal.customerapp.Model.NameAndStatus;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.adapter.ViewImageAdapter;
import com.example.soumyaagarwal.customerapp.adapter.chatAdapter;
import com.example.soumyaagarwal.customerapp.helper.CompressMe;
import com.example.soumyaagarwal.customerapp.helper.MarshmallowPermissions;
import com.example.soumyaagarwal.customerapp.helper.TouchImageView;
import com.example.soumyaagarwal.customerapp.listener.ClickListener;
import com.example.soumyaagarwal.customerapp.listener.RecyclerTouchListener;
import com.example.soumyaagarwal.customerapp.services.UploadFileService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class ChatActivity extends AppCompatActivity implements chatAdapter.ChatAdapterListener, View.OnClickListener {
    private static final int REQUEST_CODE = 101;
    private EditText typeComment;
    private ImageButton sendButton;
    Intent intent;
    private RecyclerView recyclerView;
    DatabaseReference dbChat, dbOnlineStatus;
    ValueEventListener dbOnlineStatusVle;
    private String otheruserkey, mykey;
    LinearLayoutManager linearLayoutManager;
    private MarshmallowPermissions marshmallowPermissions;
    LinearLayout emptyView;
    private ArrayList<String> mResults = new ArrayList<>();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    UploadFileService uploadFileService;
    boolean mServiceBound = false;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
    private String lastDate = "20-01-3000 00:00";
    private chatAdapter mAdapter;
    private ArrayList<ChatMessage> chatList = new ArrayList<>();
    String receiverToken = "nil";
    private ChildEventListener dbChatlistener;
    ImageButton photoattach, docattach;
    public String dbTableKey;
    private CustomerSession session;
    private ArrayList<String> docPaths, photoPaths;
    CompressMe compressMe;
    private AlertDialog viewSelectedImages;
    ViewImageAdapter adapter;
    Space space;
    ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        marshmallowPermissions = new MarshmallowPermissions(this);
        compressMe = new CompressMe(this);
        actionModeCallback = new ActionModeCallback();

        intent = getIntent();
        dbTableKey = intent.getStringExtra("dbTableKey");
        otheruserkey = intent.getStringExtra("otheruserkey");

        dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(otheruserkey).getRef();
        dbOnlineStatusVle = dbOnlineStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    getSupportActionBar().setTitle(nameAndStatus.getName());
                    if(nameAndStatus.getOnline())
                    {
                        getSupportActionBar().setSubtitle("Online");
                    }
                    else
                    {
                        getSupportActionBar().setSubtitle("Offline");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("recevier token chat act oncreate" + getRecivertoken(otheruserkey));

        session = new CustomerSession(this);

        mykey = session.getUsername();
        dbChat = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);

        typeComment = (EditText) findViewById(R.id.typeComment);
        sendButton = (ImageButton) findViewById(R.id.sendButton);

        photoattach = (ImageButton) findViewById(R.id.photoattach);
        docattach = (ImageButton) findViewById(R.id.docattach);

        photoattach.setOnClickListener(this);
        docattach.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new chatAdapter(chatList, this, dbTableKey, this);
        recyclerView.setAdapter(mAdapter);
        sendButton.setOnClickListener(this);

        typeComment.setFocusableInTouchMode(true);
        typeComment.setFocusable(true);

        typeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatList.size()>0)
                    recyclerView.scrollToPosition(chatList.size()-1);
            }
        });

        loadData();



    }

    private String getRecivertoken(String otheruserkey) {
        System.out.println(otheruserkey + "recd token in chat act ");
        DBREF.child("Fcmtokens").child(otheruserkey).child("token").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    receiverToken = dataSnapshot.getValue().toString();
                    System.out.println(dataSnapshot.getValue() + "recd token in chat act " + receiverToken);
                } else {
                    receiverToken = "nil";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return receiverToken;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                    if (photoPaths.size() > 0) {
                        viewSelectedImages = new AlertDialog.Builder(ChatActivity.this)
                                .setTitle("Selected Images").setView(R.layout.activity_view_selected_image).create();
                        viewSelectedImages.show();

                        final ImageView ImageViewlarge = (ImageView) viewSelectedImages.findViewById(R.id.ImageViewlarge);
                        ImageButton cancel = (ImageButton) viewSelectedImages.findViewById(R.id.cancel);
                        Button canceldone = (Button) viewSelectedImages.findViewById(R.id.canceldone);
                        Button okdone = (Button) viewSelectedImages.findViewById(R.id.okdone);
                        RecyclerView rv = (RecyclerView) viewSelectedImages.findViewById(R.id.viewImages);

                        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        rv.setLayoutManager(linearLayoutManager);
                        rv.setItemAnimator(new DefaultItemAnimator());
                        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

                        adapter = new ViewImageAdapter(photoPaths, this);
                        rv.setAdapter(adapter);


                        final String[] item = {photoPaths.get(0)};
                        ImageViewlarge.setImageURI(Uri.parse(item[0]));

                        rv.addOnItemTouchListener(new RecyclerTouchListener(this, rv, new ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                adapter.selectedPosition = position;
                                adapter.notifyDataSetChanged();
                                item[0] = photoPaths.get(position);
                                ImageViewlarge.setImageURI(Uri.parse(item[0]));
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int i = photoPaths.indexOf(item[0]);
                                if (i == photoPaths.size() - 1)
                                    i = 0;
                                photoPaths.remove(item[0]);
                                adapter.selectedPosition = i;
                                adapter.notifyDataSetChanged();
                                item[0] = photoPaths.get(i);
                                ImageViewlarge.setImageURI(Uri.parse(item[0]));
                            }
                        });

                        canceldone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewSelectedImages.dismiss();
                            }
                        });

                        okdone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int i = photoPaths.size();
                                if (i > 0) {
                                    for (String result : photoPaths) {
                                        String l = compressMe.compressImage(result, getApplicationContext());
                                        uploadFile(l, "photo");

                                    }
                                    viewSelectedImages.dismiss();

                                } else {
                                    viewSelectedImages.dismiss();
                                }
                            }
                        });
                    }

                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    for (String result : docPaths) {
                        uploadFile(result, "doc");
                    }
                }
                break;
        }
    }

    private void uploadFile(String filePath, String type) {
        final String timestamp = formatter.format(Calendar.getInstance().getTime());
        long curTime = Calendar.getInstance().getTimeInMillis();
        final long id = curTime;

        ChatMessage cm = new ChatMessage(mykey, otheruserkey, timestamp, "photo", id + "", "0", "nourl", receiverToken, dbTableKey, 0, filePath, "");
        dbChat.child(String.valueOf(id)).setValue(cm);

        uploadFileService.uploadFile(filePath, type, mykey, otheruserkey, receiverToken, dbTableKey, dbChat, timestamp, id);
    }


    public void loadData() {

        dbChatlistener = dbChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(ChatActivity.this, "No more comments", Toast.LENGTH_SHORT).show();
                } else {
                    ChatMessage comment = dataSnapshot.getValue(ChatMessage.class);
                    if (!comment.getSenderUId().equals(mykey)) {

                        dbChat.child(comment.getId()).child("status").setValue("3");
                        comment.setStatus("3");  // all message status set to read
                    } else {
                        if (comment.getStatus().equals("0"))
                            dbChat.child(comment.getId()).child("status").setValue("1");
                        comment.setStatus("1");  // all message status set to read
                    }

                    chatList.add(comment);
                    mAdapter.notifyDataSetChanged();

                    if(chatList.size()>0)
                        recyclerView.scrollToPosition(chatList.size()-1);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            if (mServiceConnection != null)
                unbindService(mServiceConnection);
            mServiceBound = false;
        }
        Intent intent = new Intent(ChatActivity.this,
                UploadFileService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbChatlistener != null)
            dbChat.removeEventListener(dbChatlistener);
        if (dbOnlineStatusVle != null)
            dbOnlineStatus.removeEventListener(dbOnlineStatusVle);
        mAdapter.removeListeners();
    }

    ////maintain all the clicks on buttons on this page
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                if (receiverToken.matches("nil")) {
                    getRecivertoken(otheruserkey);
                    System.out.println("calling receiver token from send message" + receiverToken);
                }
                String commentString = typeComment.getText().toString().trim();
                if (TextUtils.isEmpty(commentString)) {
                    Toast.makeText(ChatActivity.this, "What?? No Comment!!", Toast.LENGTH_SHORT).show();
                } else {
                    long curTime = Calendar.getInstance().getTimeInMillis();
                    long id = curTime;
                    String timestamp = formatter.format(Calendar.getInstance().getTime());
                    System.out.println(commentString + "time stamp" + timestamp);
                    ChatMessage cm = new ChatMessage(mykey, otheruserkey, timestamp, "text", id + "", "0", commentString, receiverToken, dbTableKey);
                    dbChat.child(String.valueOf(id)).setValue(cm);
                    DBREF.child("Chats").child(dbTableKey).child("lastMsg").setValue(id);
                    typeComment.setText("");

                }


                break;

            case R.id.photoattach:
                mResults = new ArrayList<>();
                if (!marshmallowPermissions.checkPermissionForCamera())
                    marshmallowPermissions.requestPermissionForCamera();
                if (!marshmallowPermissions.checkPermissionForExternalStorage())
                    marshmallowPermissions.requestPermissionForExternalStorage();

                if (marshmallowPermissions.checkPermissionForCamera() && marshmallowPermissions.checkPermissionForExternalStorage()) {
                    FilePickerBuilder.getInstance().setMaxCount(10)
                            .setActivityTheme(R.style.AppTheme)
                            .pickPhoto(this);
                }
                break;

            case R.id.docattach:

                if (!marshmallowPermissions.checkPermissionForExternalStorage())
                    marshmallowPermissions.requestPermissionForExternalStorage();

                if (marshmallowPermissions.checkPermissionForExternalStorage()) {

                    FilePickerBuilder.getInstance().setMaxCount(10)
                            .setActivityTheme(R.style.AppTheme)
                            .pickFile(this);
                }
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UploadFileService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    ////////////////////binding the service
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UploadFileService.MyBinder myBinder = (UploadFileService.MyBinder) service;
            uploadFileService = myBinder.getService();
            mServiceBound = true;
        }
    };


    ///////////Everything below is for action mode
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action, menu);

            // disable swipe refresh if action mode is enabled
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }

    }

    @Override
    public void onMessageRowClicked(int position) {
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            ChatMessage comment = chatList.get(position);
            String type = comment.getType();
            String uri;
            if (comment.getSenderUId().equals(session.getUsername())) {
                uri = comment.getMesenderlocal_storage();
            } else {
                uri = comment.getOthersenderlocal_storage();
            }
            switch (type) {
                case "photo":
                    viewSelectedImages = new AlertDialog.Builder(ChatActivity.this)
                            .setView(R.layout.viewchatimage).create();
                    viewSelectedImages.show();

                    TouchImageView viewchatimage = (TouchImageView) viewSelectedImages.findViewById(R.id.chatimage);
                    ImageButton backbutton = (ImageButton) viewSelectedImages.findViewById(R.id.back);

                    viewchatimage.setImageURI(Uri.parse(uri));

                    backbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewSelectedImages.dismiss();
                        }
                    });
                    break;
                case "doc":
                    File file = new File(uri);
                    if (file.exists()) {

                        Uri pdfPath = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(pdfPath, "application/pdf");

                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            //if user doesn't have pdf reader instructing to download a pdf reader
                        }

                    }
                    break;
            }


        }
    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    @Override
    public void download_chatimageClicked(final int position, final chatAdapter.MyViewHolder holder) {

        mAdapter.showProgressBar(holder);
        final ChatMessage comment = chatList.get(position);
        StorageReference str = FirebaseStorage.getInstance().getReferenceFromUrl(comment.getImgurl());
        String type = comment.getType();

        switch (type) {
            case "photo":
                File rootPath = new File(Environment.getExternalStorageDirectory(), "MeChat/Images");
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                String uriSting = System.currentTimeMillis() + ".jpg";

                final File localFile = new File(rootPath, uriSting);
                final String localuri = (rootPath.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
                str.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                        dbChat = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();
                        dbChat.child(comment.getId()).child("othersenderlocal_storage").setValue(localuri);
                        mAdapter.dismissProgressBar(holder);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mAdapter.dismissProgressBar(holder);
                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    }
                });
                break;
            case "doc":
                rootPath = new File(Environment.getExternalStorageDirectory(), "MeChat/Docs");
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                uriSting = System.currentTimeMillis() + ".jpg";

                final File localdocFile = new File(rootPath, uriSting);
                final String localdocuri = (rootPath.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
                str.getFile(localdocFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        dbChat = DBREF.child("Chats").child(dbTableKey).child("ChatMessages").getRef();
                        dbChat.child(comment.getId()).child("othersenderlocal_storage").setValue(localdocuri);
                        mAdapter.dismissProgressBar(holder);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mAdapter.dismissProgressBar(holder);
                        Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                    }
                });
                break;
        }
    }


}