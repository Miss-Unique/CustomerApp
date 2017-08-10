package com.example.soumyaagarwal.customerapp.Task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import com.example.soumyaagarwal.customerapp.helper.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soumyaagarwal.customerapp.CustomerLogin.CustomerSession;
import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.adapter.taskAdapter;
import com.example.soumyaagarwal.customerapp.helper.MarshmallowPermissions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class TaskHome extends Fragment implements taskAdapter.TaskAdapterListener {
    RecyclerView task_list;
    DatabaseReference dbTask;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<String> TaskList = new ArrayList<>();
    private taskAdapter mAdapter;
    Activity context;
    MarshmallowPermissions marshMallowPermission;
    ProgressDialog progressDialog;
    private String custId = "nocust";
    CustomerSession session;
    FloatingActionButton create_task;

    public TaskHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_home, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            custId = bundle.getString("custId");
        }
        session = new CustomerSession(getActivity());
        marshMallowPermission = new MarshmallowPermissions(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        dbTask = DBREF.child("Customer").child(session.getUsername()).child("Task").getRef();
        task_list = (RecyclerView) getView().findViewById(R.id.task_list);
        create_task = (FloatingActionButton) getView().findViewById(R.id.create_task);
        LoadData();
        mAdapter = new taskAdapter(TaskList, getContext(), this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        task_list.setLayoutManager(linearLayoutManager);
        task_list.setItemAnimator(new DefaultItemAnimator());
        task_list.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        task_list.setAdapter(mAdapter);

        create_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbCustomerName = DBREF.child("Customer").child(session.getUsername()).getRef();
                dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String customername = dataSnapshot.child("name").getValue(String.class);
                        Intent intent = new Intent(getActivity(), CreateTask.class);
                        intent.putExtra("customerId", session.getUsername());
                        intent.putExtra("customerName", customername);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onMessageRowClicked(int position) {

        Intent intent = new Intent(getContext(), TaskDetail.class);
        String taskid = TaskList.get(position);
        intent.putExtra("task_id", taskid);
        startActivity(intent);
    }

    void LoadData() {

        dbTask.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String taskid = dataSnapshot.getKey();
                    TaskList.add(taskid);
                    mAdapter.notifyDataSetChanged();
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

}