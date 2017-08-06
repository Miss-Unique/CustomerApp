package com.example.soumyaagarwal.customerapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.soumyaagarwal.customerapp.Model.Coordinator;
import com.example.soumyaagarwal.customerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoumyaAgarwal on 8/6/2017.
 */

public class coordinator_adapter extends RecyclerView.Adapter<coordinator_adapter.MyViewHolder> {
    List<Coordinator> list = new ArrayList<>();
    private Context context;
    coordinator_adapterListener listener;

    public coordinator_adapter(List<Coordinator> list, Context context, coordinator_adapterListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactcoordinator_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final coordinator_adapter.MyViewHolder holder, final int position) {

        Coordinator coordinator = list.get(position);
        holder.coordinatorname.setText(coordinator.getName());
        String caps = coordinator.getName().toUpperCase();
        holder.icon_text.setText(caps.charAt(0) + "");

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView coordinatorname, icon_text;
        LinearLayout employee_row;
        CardView callme, msgme;
        ImageView imgProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            coordinatorname = (TextView) itemView.findViewById(R.id.coordinatorName);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            employee_row = (LinearLayout) itemView.findViewById(R.id.employee_row);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            callme = (CardView) itemView.findViewById(R.id.callme);
            msgme = (CardView) itemView.findViewById(R.id.msgme);
        }
    }

    public interface coordinator_adapterListener {
        void onMSGMEclicked(int position);

        void onCALLMEclicked(int position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.callme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCALLMEclicked(position);
            }
        });

        holder.msgme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMSGMEclicked(position);
            }
        });
    }
}