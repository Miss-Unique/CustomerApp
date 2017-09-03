package com.example.soumyaagarwal.customerapp.Task;

import android.Manifest;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;

import com.bumptech.glide.Glide;
import com.example.soumyaagarwal.customerapp.adapter.ViewImageAdapter;
import com.example.soumyaagarwal.customerapp.helper.CompressMe;
import com.example.soumyaagarwal.customerapp.helper.DividerItemDecoration;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.Model.CompletedBy;
import com.example.soumyaagarwal.customerapp.Model.Quotation;
import com.example.soumyaagarwal.customerapp.Model.Task;
import com.example.soumyaagarwal.customerapp.Model.measurement;
import com.example.soumyaagarwal.customerapp.adapter.assignedto_adapter;
import com.example.soumyaagarwal.customerapp.adapter.bigimage_adapter;
import com.example.soumyaagarwal.customerapp.adapter.measurement_adapter;
import com.example.soumyaagarwal.customerapp.adapter.taskdetailDescImageAdapter;
import com.example.soumyaagarwal.customerapp.chat.ChatActivity;
import com.example.soumyaagarwal.customerapp.helper.MarshmallowPermissions;
import com.example.soumyaagarwal.customerapp.helper.TouchImageView;
import com.example.soumyaagarwal.customerapp.listener.ClickListener;
import com.example.soumyaagarwal.customerapp.listener.RecyclerTouchListener;
import com.example.soumyaagarwal.customerapp.services.DownloadFileService;
import com.example.soumyaagarwal.customerapp.services.UploadTaskPhotosServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

import static com.example.soumyaagarwal.customerapp.CustomerApp.AppName;
import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;
import static com.example.soumyaagarwal.customerapp.CustomerApp.sendNotif;
import static com.example.soumyaagarwal.customerapp.CustomerApp.sendNotifToAllCoordinators;

public class TaskDetail extends AppCompatActivity implements taskdetailDescImageAdapter.ImageAdapterListener, assignedto_adapter.assignedto_adapterListener, bigimage_adapter.bigimage_adapterListener, measurement_adapter.measurement_adapterListener {

    private DatabaseReference dbRef, dbTask, dbAssigned, dbMeasurement, dbDescImages;
    ImageButton download;
    ProgressBar progressBar;
    private Task task;
    private String mykey, id, dbTablekey, task_id;
    EditText startDate, endDate, quantity, description;
    RecyclerView rec_assignedto, rec_measurement, rec_DescImages;
    assignedto_adapter adapter_assignedto;
    taskdetailDescImageAdapter adapter_taskimages;
    ArrayList<String> DescImages = new ArrayList<>();
    List<CompletedBy> assignedtoList = new ArrayList<>();
    ArrayList<measurement> measurementList = new ArrayList<>();
    measurement_adapter adapter_measurement;
    TextView appByCustomer, uploadStatus, measure_and_hideme, assign_and_hideme;
    DatabaseReference dbQuotation;
    ProgressDialog progressDialog;
    private MarshmallowPermissions marshmallowPermissions;
    private AlertDialog viewSelectedImages, edit_description, viewSelectedImages_measure;
    LinearLayoutManager linearLayoutManager;
    bigimage_adapter adapter;
    CustomerSession session;
    String num;
    private Button approveQuote, approveMeasurement, approveDesc;
    ImageButton written_desc, photo_desc;
    private int REQUEST_CODE = 1;
    private ArrayList<String> mResults;
    CompressMe compressMe;
    private ArrayList<String> picUriList = new ArrayList<>();
    ViewImageAdapter madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        session = new CustomerSession(getApplicationContext());
        marshmallowPermissions = new MarshmallowPermissions(this);
        dbRef = DBREF;
        compressMe = new CompressMe(this);
        progressDialog = new ProgressDialog(this);
        download = (ImageButton) findViewById(R.id.download);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        uploadStatus = (TextView) findViewById(R.id.uploadStatus);
        appByCustomer = (TextView) findViewById(R.id.appByCustomer);
        assign_and_hideme = (TextView) findViewById(R.id.assign_and_hideme);
        measure_and_hideme = (TextView) findViewById(R.id.measure_and_hideme);
        approveQuote = (Button) findViewById(R.id.approveQuote);
        approveDesc = (Button) findViewById(R.id.approveDesc);
        approveMeasurement = (Button) findViewById(R.id.approveMeasurement);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity = (EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        rec_assignedto = (RecyclerView) findViewById(R.id.rec_assignedto);
        rec_measurement = (RecyclerView) findViewById(R.id.rec_measurement);
        rec_DescImages = (RecyclerView) findViewById(R.id.rec_DescImages);
        photo_desc = (ImageButton) findViewById(R.id.photo_desc);
        written_desc = (ImageButton) findViewById(R.id.written_desc);

        mykey = session.getUsername();

        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");

        rec_assignedto.setLayoutManager(new LinearLayoutManager(this));
        rec_assignedto.setItemAnimator(new DefaultItemAnimator());
        rec_assignedto.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_assignedto = new assignedto_adapter(assignedtoList, getApplicationContext(), "AssignedTo", task_id, this);
        rec_assignedto.setAdapter(adapter_assignedto);

        rec_measurement.setLayoutManager(new LinearLayoutManager(this));
        rec_measurement.setItemAnimator(new DefaultItemAnimator());
        rec_measurement.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_measurement = new measurement_adapter(measurementList, this);
        rec_measurement.setAdapter(adapter_measurement);

        rec_DescImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rec_DescImages.setItemAnimator(new DefaultItemAnimator());
        rec_DescImages.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        adapter_taskimages = new taskdetailDescImageAdapter(DescImages, getApplicationContext(), this);
        rec_DescImages.setAdapter(adapter_taskimages);

        dbTask = dbRef.child("Task").child(task_id);
        dbQuotation = dbTask.child("Quotation").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();
        dbMeasurement = dbTask.child("Measurement").getRef();
        dbDescImages = dbTask.child("DescImages").getRef();

        approveDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotifToAllCoordinators(mykey, "approveDescription", session.getName() + " approved the description for " + task.getName(), task_id);
                sendNotif(mykey, mykey, "approveMeasurement", "You approved the description for " + task.getName(), task_id);
                Toast.makeText(TaskDetail.this, "You approved the description for " + task.getName(), Toast.LENGTH_LONG).show();

            }
        });
        prepareListData();

        dbTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
                getSupportActionBar().setTitle(task.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!marshmallowPermissions.checkPermissionForCamera()) {
                    marshmallowPermissions.requestPermissionForExternalStorage();
                    if (!marshmallowPermissions.checkPermissionForExternalStorage())
                        showToast("Cannot Download because external storage permission not granted");
                    else
                        launchLibrary();
                } else {

                    launchLibrary();
                }
            }
        });

        written_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_description = new AlertDialog.Builder(TaskDetail.this)
                        .setView(R.layout.edit_description).create();
                edit_description.show();

                final EditText description2 = (EditText) edit_description.findViewById(R.id.description);
                final EditText olddescription = (EditText) edit_description.findViewById(R.id.olddescription);
                Button oksave = (Button) edit_description.findViewById(R.id.oksave);
                Button okcancel = (Button) edit_description.findViewById(R.id.okcancel);

                String desc;
                if (description.getVisibility() == View.VISIBLE) {
                    desc = description.getText().toString().trim();
                    olddescription.setText(desc);
                }
                oksave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newdesc = description2.getText().toString().trim();
                        DBREF.child("Task").child(task_id).child("desc").setValue(olddescription.getText().toString() + " " + newdesc);
                        String contentforme = "You created a new Job " + task.getName();
                        sendNotif(session.getUsername(), session.getUsername(), "createJob", contentforme, task_id);
                        String contentforother = session.getName() + " created new Job " + task.getName();
                        sendNotifToAllCoordinators(session.getUsername(), "createJob", contentforother, task_id);
                        edit_description.dismiss();
                    }
                });

                okcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_description.dismiss();
                    }
                });
            }
        });

        photo_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!marshmallowPermissions.checkPermissionForCamera() && !marshmallowPermissions.checkPermissionForExternalStorage()) {
                    ActivityCompat.requestPermissions(TaskDetail.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            2);
                } else {
                    FilePickerBuilder.getInstance().setMaxCount(10)
                            .setActivityTheme(R.style.AppTheme)
                            .pickPhoto(TaskDetail.this);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (data != null) {
                mResults = new ArrayList<>();
                mResults.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                assert mResults != null;

                System.out.println(String.format("Totally %d images selected:", mResults.size()));
                for (String result : mResults) {
                    String l = compressMe.compressImage(result, getApplicationContext());
                    picUriList.add(l);
                }
                if (picUriList.size() > 0) {
                    viewSelectedImages = new AlertDialog.Builder(TaskDetail.this)
                            .setView(R.layout.activity_view_selected_image).create();
                    viewSelectedImages.show();

                    final ImageView ImageViewlarge = (ImageView) viewSelectedImages.findViewById(R.id.ImageViewlarge);
                    ImageButton cancel = (ImageButton) viewSelectedImages.findViewById(R.id.cancel);
                    ImageButton canceldone = (ImageButton) viewSelectedImages.findViewById(R.id.canceldone);
                    ImageButton okdone = (ImageButton) viewSelectedImages.findViewById(R.id.okdone);
                    RecyclerView rv = (RecyclerView) viewSelectedImages.findViewById(R.id.viewImages);

                    linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setItemAnimator(new DefaultItemAnimator());
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

                    madapter = new ViewImageAdapter(picUriList, this);
                    rv.setAdapter(madapter);

                    final String[] item = {picUriList.get(0)};
                    ImageViewlarge.setImageURI(Uri.parse(item[0]));

                    rv.addOnItemTouchListener(new RecyclerTouchListener(this, rv, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            madapter.selectedPosition = position;
                            madapter.notifyDataSetChanged();
                            item[0] = picUriList.get(position);
                            ImageViewlarge.setImageURI(Uri.parse(item[0]));
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int i = picUriList.indexOf(item[0]);
                            if (i == picUriList.size() - 1)
                                i = 0;
                            if (picUriList.size() == 1) {
                                picUriList.clear();
                                viewSelectedImages.dismiss();

                            } else {
                                picUriList.remove(item[0]);
                                madapter.selectedPosition = i;
                                madapter.notifyDataSetChanged();
                                item[0] = picUriList.get(i);
                                ImageViewlarge.setImageURI(Uri.parse(item[0]));
                            }
                        }
                    });

                    canceldone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            picUriList.clear();
                            viewSelectedImages.dismiss();
                        }
                    });

                    okdone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (picUriList.size() > 0) {
                                Intent serviceIntent = new Intent(getApplicationContext(), UploadTaskPhotosServices.class);
                                serviceIntent.putStringArrayListExtra("picUriList", picUriList);
                                serviceIntent.putExtra("taskid", task_id);
                                startService(serviceIntent);
                                finish();
                            } else {
                                viewSelectedImages.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void launchLibrary() {
        final String[] url = new String[1];
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    url[0] = quotation.getUrl();
                    Intent serviceIntent = new Intent(getApplicationContext(), DownloadFileService.class);
                    serviceIntent.putExtra("url", url[0]);
                    startService(serviceIntent);
                } else {
                    Toast.makeText(TaskDetail.this, "No Quotation Uploaded Yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareListData() {
        dbAssigned.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    assign_and_hideme.setVisibility(View.GONE);
                    CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                    assignedtoList.add(item);
                    adapter_assignedto.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                assignedtoList.remove(item);
                adapter_assignedto.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbMeasurement.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    measure_and_hideme.setVisibility(View.GONE);
                    measurement item = dataSnapshot.getValue(measurement.class);
                    measurementList.add(item);
                    adapter_measurement.notifyDataSetChanged();
                    approveMeasurement.setVisibility(View.VISIBLE);
                    approveMeasurement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendNotifToAllCoordinators(mykey, "approveMeaurement", session.getName() + " approved the measurements for " + task.getName(), task_id);
                            sendNotif(mykey, mykey, "approveMeasurement", "You approved the measurements for " + task.getName(), task_id);
                            Toast.makeText(TaskDetail.this, "You approved the measurements for " + task.getName(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    approveMeasurement.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                measurement item = dataSnapshot.getValue(measurement.class);
                measurementList.remove(item);
                adapter_measurement.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbDescImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    rec_DescImages.setVisibility(View.VISIBLE);
                    String item = dataSnapshot.getValue(String.class);
                    DescImages.add(item);
                    adapter_taskimages.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String item = dataSnapshot.getKey();
                DescImages.remove(item);
                adapter_taskimages.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setValue(final Task task) {
        startDate.setText(task.getStartDate());
        endDate.setText(task.getExpEndDate());
        quantity.setText(task.getQty());
        if (!task.getDesc().equals("")) {
            description.setVisibility(View.VISIBLE);
            description.setText(task.getDesc());
        }
        dbQuotation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    appByCustomer.setVisibility(View.VISIBLE);
                    final Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    if (quotation.getApprovedByCust() != null)
                        appByCustomer.setText(" " + quotation.getApprovedByCust());
                    uploadStatus.setText(" Yes");
                    approveQuote.setVisibility(View.VISIBLE);
                    download.setVisibility(View.VISIBLE);
                    approveQuote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!quotation.getApprovedByCust().equals("Yes")) {
                                sendNotifToAllCoordinators(mykey, "approveQuotation", session.getName() + " approved the quotation for " + task.getName(), task_id);
                                sendNotif(mykey, mykey, "approveQuotation", "You approved the quotation for " + task.getName(), task_id);
                                dbQuotation.child("approvedByCust").setValue("Yes");
                                Toast.makeText(TaskDetail.this, "You approved the quotation for " + task.getName(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TaskDetail.this, "Quotation Already Approved", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else {
                    appByCustomer.setText(" No");
                    uploadStatus.setText(" No");
                    approveQuote.setVisibility(View.GONE);
                    download.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onImageClicked(int position) {
        viewSelectedImages = new AlertDialog.Builder(TaskDetail.this)
                .setView(R.layout.view_image_on_click).create();
        viewSelectedImages.show();

        RecyclerView bigimage = (RecyclerView) viewSelectedImages.findViewById(R.id.bigimage);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        bigimage.setLayoutManager(linearLayoutManager);
        bigimage.setItemAnimator(new DefaultItemAnimator());
        bigimage.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

        adapter = new bigimage_adapter(DescImages, this, this);
        bigimage.setAdapter(adapter);

        bigimage.scrollToPosition(position);
    }

    private void checkChatref(final String mykey, final String otheruserkey) {
        DatabaseReference dbChat = DBREF.child("Chats").child(mykey + otheruserkey).getRef();
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("query1" + mykey + otheruserkey);
                System.out.println("datasnap 1" + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    System.out.println("datasnap exists1" + dataSnapshot.toString());
                    dbTablekey = mykey + otheruserkey;
                    goToChatActivity();
                } else {
                    checkChatref2(mykey, otheruserkey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkChatref2(final String mykey, final String otheruserkey) {
        final DatabaseReference dbChat = DBREF.child("Chats").child(otheruserkey + mykey).getRef();
        dbTablekey = otheruserkey + mykey;
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("query1" + otheruserkey + mykey);
                    goToChatActivity();
                } else {
                    DBREF.child("Users").child("Userchats").child(mykey).child(otheruserkey).setValue(dbTablekey);
                    DBREF.child("Users").child("Userchats").child(otheruserkey).child(mykey).setValue(dbTablekey);
                    goToChatActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToChatActivity() {
        Intent in = new Intent(this, ChatActivity.class);
        in.putExtra("dbTableKey", dbTablekey);
        in.putExtra("otheruserkey", id);
        startActivity(in);
    }

    @Override
    public void onMSGMEclicked(int position) {
        CompletedBy completedBy = assignedtoList.get(position);
        id = completedBy.getEmpId();
        checkChatref(mykey, id);
    }

    @Override
    public void onCALLMEclicked(int position) {

        CompletedBy completedBy = assignedtoList.get(position);
        id = completedBy.getEmpId();
        DBREF.child("Employee").child(id).child("phone_num").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                num = dataSnapshot.getValue(String.class);
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + num));
                startActivity(callIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void ondownloadButtonClicked(final int position, final bigimage_adapter.MyViewHolder holder) {
        if (!marshmallowPermissions.checkPermissionForExternalStorage()) {
            marshmallowPermissions.requestPermissionForExternalStorage();
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.download_taskdetail_image.setVisibility(View.GONE);
            String url = DescImages.get(position);

            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            final String[] ext = new String[1];
            mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    ext[0] = storageMetadata.getContentType();
                    int p = ext[0].lastIndexOf("/");
                    String l = "." + ext[0].substring(p + 1);

                    File rootPath = new File(Environment.getExternalStorageDirectory(), AppName + "/TaskDetailImages");

                    if (!rootPath.exists()) {
                        rootPath.mkdirs();
                    }
                    String uriSting = System.currentTimeMillis() + l;
                    final File localFile = new File(rootPath, uriSting);

                    mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                            holder.download_taskdetail_image.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(TaskDetail.this, "Image " + position + 1 + " Downloaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                            holder.download_taskdetail_image.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(TaskDetail.this, "Failed to download image " + position + 1, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.taskcomplete:
                DatabaseReference dbTaskCompleteStatus = DBREF.child("Customer").child(task.getCustomerId()).child("Task").child(task_id).getRef();
                dbTaskCompleteStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String status = dataSnapshot.getValue(String.class);
                            if (status.equals("complete")) {
                                final AlertDialog.Builder builderCompleteTask = new AlertDialog.Builder(TaskDetail.this);
                                builderCompleteTask.setMessage("Are you sure you want to mark this task as complete??")
                                        .setCancelable(false)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, final int id) {
                                                sendNotifToAllCoordinators(mykey, "completeJob", session.getName() + " marked the task" + task.getName() + " as complete", task_id);
                                                sendNotif(mykey, mykey, "completeJob", "You marked task " + task.getName() + " as successfully completed", task_id);
                                                Toast.makeText(TaskDetail.this, "You marked task " + task.getName() + " as successfully completed", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();

                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builderCompleteTask.create();
                                alert.show();
                            } else {
                                Toast.makeText(TaskDetail.this, "Task is yet to be completed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }
        return true;
    }

    @Override
    public void onImageClicked(int position, measurement_adapter.MyViewHolder holder) {
        viewSelectedImages_measure = new AlertDialog.Builder(TaskDetail.this)
                .setView(R.layout.viewmeasureimage).create();
        viewSelectedImages_measure.show();

        measurement m = measurementList.get(position);
        String uri = m.getFleximage();

        TouchImageView viewchatimage = (TouchImageView) viewSelectedImages_measure.findViewById(R.id.chatimage);
        ImageButton backbutton = (ImageButton) viewSelectedImages_measure.findViewById(R.id.back);

        Glide.with(getApplicationContext())
                .load(Uri.parse(uri))
                .placeholder(R.color.black)
                .crossFade()
                .centerCrop()
                .into(viewchatimage);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSelectedImages_measure.dismiss();
            }
        });
    }
}