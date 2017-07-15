package com.example.soumyaagarwal.customerapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.soumyaagarwal.customerapp.Model.Task;
import com.example.soumyaagarwal.customerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class taskAdapter extends  RecyclerView.Adapter<taskAdapter.MyViewHolder>
        {
        ArrayList<String> list = new ArrayList<>();
        private Context context;
        private TaskAdapterListener listener;

        public taskAdapter(ArrayList<String> list, Context context, TaskAdapterListener listener)
        {
            this.list = list;
            this.listener = listener;
            this.context = context;
        }

            public class MyViewHolder extends RecyclerView.ViewHolder
            {
                TextView taskname,customername,timestamp,icon_text;
                ImageView imgProfile;
                public LinearLayout messageContainer;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    taskname = (TextView) itemView.findViewById(R.id.tv_taskname);
                    customername = (TextView) itemView.findViewById(R.id.tv_customerName);
                    timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                    icon_text =(TextView)itemView.findViewById(R.id.icon_text);
                    imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);
                    messageContainer = (LinearLayout)itemView.findViewById(R.id.message_container);
                }
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row,parent,false);
                return new MyViewHolder(view);

            }

            @Override
            public void onBindViewHolder(final taskAdapter.MyViewHolder holder, final int position) {
                DatabaseReference refh = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").child(list.get(position)).getRef();

                refh.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            Task task = dataSnapshot.getValue(Task.class);
                            holder.taskname.setText(task.getName());
                            String iconText = task.getName().toUpperCase();
                            holder.icon_text.setText(iconText.charAt(0) + "");
                            holder.imgProfile.setImageResource(R.drawable.bg_circle);
                            holder.imgProfile.setColorFilter(task.getColor());
                            holder.timestamp.setText(task.getStartDate());
                            applyClickEvents(holder, position);
                            DatabaseReference dbCustomerName = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(task.getCustomerId()).getRef();
                            dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String customername = dataSnapshot.child("name").getValue(String.class);
                                    holder.customername.setText(customername);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public int getItemCount() {
                return list.size();
            }
            public interface TaskAdapterListener
            {
                void onMessageRowClicked(int position);
            }
            private void applyClickEvents(MyViewHolder holder, final int position) {

                holder.messageContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onMessageRowClicked(position);
                    }
                });
            }
        }
