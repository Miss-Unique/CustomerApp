package com.example.soumyaagarwal.customerapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.soumyaagarwal.customerapp.Model.CompletedBy;
import com.example.soumyaagarwal.customerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

public class assignedto_adapter extends  RecyclerView.Adapter<assignedto_adapter.MyViewHolder>
{
    List<CompletedBy> list = new ArrayList<>();
    private Context context;
    SharedPreferences sharedPreferences ;
    String type,taskId;
    assignedto_adapterListener listener;
    public CompletedBy emp = new CompletedBy();

    public assignedto_adapter(List<CompletedBy> list, Context context, String type, String taskId, assignedto_adapterListener listener) {
        this.list = list;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SESSION",Context.MODE_PRIVATE);
        this.type = type;
        this.taskId=taskId;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignedto_list_row,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final assignedto_adapter.MyViewHolder holder, final int position)
    {
        emp = list.get(position);

        DatabaseReference dbEmp = DBREF.child("Employee").child(emp.getEmpId()).getRef();
        dbEmp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String empname = dataSnapshot.child("name").getValue(String.class);
                holder.employeename.setText(empname);
                String empdesig = dataSnapshot.child("designation").getValue(String.class);
                holder.employeeDesig.setText(empdesig);
                String iconText = empname.toUpperCase();
                holder.icon_text.setText(iconText.charAt(0) + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        applyClickEvents(holder,position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView employeename,employeeDesig,icon_text;
        LinearLayout employee_row;

        public MyViewHolder(View itemView) {
            super(itemView);

            employeename = (TextView)
                    itemView.findViewById(R.id.employeeName);

            employeeDesig = (TextView)
                    itemView.findViewById(R.id.employeeDesig);

            icon_text = (TextView)itemView.findViewById(R.id.icon_text);
            employee_row = (LinearLayout)itemView.findViewById(R.id.employee_row);
        }
    }

    public interface assignedto_adapterListener {
        void onEmployeeRowClicked(int position);
    }
    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.employee_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEmployeeRowClicked(position);
            }
        });
    }
}
