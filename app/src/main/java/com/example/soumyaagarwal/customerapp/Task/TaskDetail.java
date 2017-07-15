package com.example.soumyaagarwal.customerapp.Task;

import android.app.ProgressDialog;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.Model.CompletedBy;
import com.example.soumyaagarwal.customerapp.Model.Quotation;
import com.example.soumyaagarwal.customerapp.Model.Task;
import com.example.soumyaagarwal.customerapp.Model.measurement;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.adapter.assignedto_adapter;
import com.example.soumyaagarwal.customerapp.adapter.bigimage_adapter;
import com.example.soumyaagarwal.customerapp.adapter.measurement_adapter;
import com.example.soumyaagarwal.customerapp.adapter.taskdetailDescImageAdapter;
import com.example.soumyaagarwal.customerapp.chat.ChatActivity;
import com.example.soumyaagarwal.customerapp.helper.MarshmallowPermissions;
import com.example.soumyaagarwal.customerapp.services.DownloadFileService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class TaskDetail extends AppCompatActivity implements taskdetailDescImageAdapter.ImageAdapterListener, assignedto_adapter.assignedto_adapterListener{

    private DatabaseReference dbRef, dbTask,dbAssigned,dbMeasurement,dbDescImages;
    ImageButton download;
    ProgressBar progressBar;
    private Task task;
    private String customername,mykey,id,dbTablekey,task_id;
    EditText startDate,endDate,custId,taskName,quantity,description;
    RecyclerView rec_assignedto,rec_measurement, rec_DescImages ;
    assignedto_adapter adapter_assignedto;
    taskdetailDescImageAdapter adapter_taskimages;
    ArrayList<String> DescImages = new ArrayList<>();
    List<CompletedBy> assignedtoList = new ArrayList<>();
    ArrayList<measurement> measurementList = new ArrayList<>();
    measurement_adapter adapter_measurement;
    TextView open_assignedto,open_measurement,appByCustomer,uploadStatus;
    DatabaseReference dbQuotation;
    ProgressDialog progressDialog ;
    private MarshmallowPermissions marshmallowPermissions;
    private AlertDialog viewSelectedImages ;
    LinearLayoutManager linearLayoutManager;
    bigimage_adapter adapter;
    CustomerSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        session = new CustomerSession(getApplicationContext());
        marshmallowPermissions =new MarshmallowPermissions(this);
        dbRef = FirebaseDatabase.getInstance().getReference().child("MeChat");
        progressDialog = new ProgressDialog(this);
        download = (ImageButton)findViewById(R.id.download);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        uploadStatus = (TextView)findViewById(R.id.uploadStatus);
        appByCustomer = (TextView)findViewById(R.id.appByCustomer);

        taskName = (EditText) findViewById(R.id.taskName);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity=(EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        custId = (EditText) findViewById(R.id.custId);
        rec_assignedto = (RecyclerView)findViewById(R.id.rec_assignedto);
        rec_measurement = (RecyclerView)findViewById(R.id.rec_measurement);
        rec_DescImages = (RecyclerView)findViewById(R.id.rec_DescImages);
        open_assignedto = (TextView)findViewById(R.id.open_assignedto);
        open_measurement = (TextView)findViewById(R.id.open_measurement);

        mykey = session.getUsername();

        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");

        rec_assignedto.setLayoutManager(new LinearLayoutManager(this));
        rec_assignedto.setItemAnimator(new DefaultItemAnimator());
        rec_assignedto.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_assignedto = new assignedto_adapter(assignedtoList, getApplicationContext(),"AssignedTo",task_id,this);
        rec_assignedto.setAdapter(adapter_assignedto);

        rec_measurement.setLayoutManager(new LinearLayoutManager(this));
        rec_measurement.setItemAnimator(new DefaultItemAnimator());
        rec_measurement.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_measurement = new measurement_adapter(measurementList, this);
        rec_measurement.setAdapter(adapter_measurement);

        rec_DescImages.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rec_DescImages.setItemAnimator(new DefaultItemAnimator());
        rec_DescImages.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        adapter_taskimages = new taskdetailDescImageAdapter(DescImages, getApplicationContext(),this);
        rec_DescImages.setAdapter(adapter_taskimages);

        dbTask = dbRef.child("Task").child(task_id);
        dbQuotation = dbTask.child("Quotation").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();
        dbMeasurement = dbTask.child("Measurement").getRef();
        dbDescImages = dbTask.child("DescImages").getRef();

        prepareListData();

        open_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (rec_measurement.getVisibility()== View.GONE)
                {
                    rec_measurement.setVisibility(View.VISIBLE);
                }
                else
                {
                    rec_measurement.setVisibility(View.GONE);
                }
            }
        });

        open_assignedto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rec_assignedto.getVisibility()== View.GONE)
                {
                    rec_assignedto.setVisibility(View.VISIBLE);
                }
                else
                {
                    rec_assignedto.setVisibility(View.GONE);
                }
            }
        });

        dbTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
                getSupportActionBar().setTitle(task.getName());
                DatabaseReference dbCustomerName = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(task.getCustomerId()).getRef();
                dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customername = dataSnapshot.child("name").getValue(String.class);
                        getSupportActionBar().setSubtitle(customername);
                        custId.setText(task.getCustomerId()+ ": "+customername);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!marshmallowPermissions.checkPermissionForCamera())
                {
                    marshmallowPermissions.requestPermissionForExternalStorage();
                    if(!marshmallowPermissions.checkPermissionForExternalStorage())
                        showToast("Cannot Download because external storage permission not granted");
                    else
                        launchLibrary();
                } else {

                    launchLibrary();
                }
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void launchLibrary()
    {
        //TODO : loader aur completion of download show karna hai
        //download.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);
        final String[] url = new String[1];
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    url[0] = quotation.getUrl();
                    Intent serviceIntent = new Intent(getApplicationContext(), DownloadFileService.class);
                    serviceIntent.putExtra("TaskId", task_id);
                    serviceIntent.putExtra("url", url[0]);
                    startService(serviceIntent);
                }
                else
                {
                    Toast.makeText(TaskDetail.this, "No Quotation Uploaded Yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void prepareListData()
    {
        dbAssigned.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
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
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists()) {
                    measurement item = dataSnapshot.getValue(measurement.class);
                    measurementList.add(item);
                    adapter_measurement.notifyDataSetChanged();
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

        //TODO : image ko download karwana hai kya ?
        //image forwarding ka koi option ?
        dbDescImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
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

    void setValue(Task task)
    {
        startDate.setText(task.getStartDate());
        endDate.setText(task.getExpEndDate());
        taskName.setText(task.getName());
        quantity.setText(task.getQty());
        if (!task.getDesc().equals("")) {
            description.setVisibility(View.VISIBLE);
            description.setText(task.getDesc());
        }
        dbQuotation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    appByCustomer.setVisibility(View.VISIBLE);
                    Quotation quotation = dataSnapshot.getValue(Quotation.class);
                    appByCustomer.setText(" "+quotation.getApprovedByCust());
                    uploadStatus.setText(" Yes");
                }
                else
                {
                    appByCustomer.setVisibility(View.GONE);
                    uploadStatus.setText(" No");
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
                .setTitle("Images").setView(R.layout.view_image_on_click).create();
        viewSelectedImages.show();

        RecyclerView bigimage = (RecyclerView)viewSelectedImages.findViewById(R.id.bigimage);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        bigimage.setLayoutManager(linearLayoutManager);
        bigimage.setItemAnimator(new DefaultItemAnimator());
        bigimage.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

        adapter = new bigimage_adapter(DescImages, this);
        bigimage.setAdapter(adapter);

        bigimage.scrollToPosition(position);
    }

    @Override
    public void onEmployeeRowClicked(int position) {
        // open chat activity
        CompletedBy completedBy = assignedtoList.get(position);
        id = completedBy.getEmpId();
        checkChatref(mykey,id);
    }

    private void checkChatref(final String mykey, final String otheruserkey) {
        DatabaseReference dbChat = DBREF.child("Chats").child(mykey+otheruserkey).getRef();
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("query1" + mykey+otheruserkey);
                System.out.println("datasnap 1" + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    System.out.println("datasnap exists1" + dataSnapshot.toString());
                    dbTablekey = mykey+otheruserkey;
                    goToChatActivity();
                }
                else
                {
                    checkChatref2(mykey,otheruserkey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkChatref2(final String mykey, final String otheruserkey) {
        final DatabaseReference dbChat = DBREF.child("Chats").child(otheruserkey+mykey).getRef();
        dbTablekey = otheruserkey+mykey;
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    System.out.println("query1" + otheruserkey+mykey);
                    goToChatActivity();
                }
                else
                {
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

    private void goToChatActivity()
    {
        Intent in = new Intent(this, ChatActivity.class);
        in.putExtra("dbTableKey",dbTablekey);
        in.putExtra("otheruserkey",id);
        startActivity(in);
    }
}
