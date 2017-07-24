package com.example.soumyaagarwal.customerapp.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.soumyaagarwal.customerapp.Model.Notif;
import com.example.soumyaagarwal.customerapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.soumyaagarwal.customerapp.CustomerApp.formatter;

/**
 * Created by SoumyaAgarwal on 7/20/2017.
 */

public class notification_adapter extends  RecyclerView.Adapter<notification_adapter.MyViewHolder> {
    List<Notif> list = new ArrayList<>();
    private Context context;
    private NotificationAdapterListener listener;

    public notification_adapter(List<Notif> list, Context c, NotificationAdapterListener listener) {
        this.list = list;
        this.context = c;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView notif_message, notif_timestamp, icon_text, notif_sender;
        ImageView imgProfile;
        LinearLayout row;

        public MyViewHolder(View itemView) {
            super(itemView);
            notif_message = (TextView)itemView.findViewById(R.id.notification_message);
            notif_timestamp = (TextView) itemView.findViewById(R.id.notification_time);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            notif_sender = (TextView) itemView.findViewById(R.id.notification_sender);
            row = (LinearLayout)itemView.findViewById(R.id.row);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final notification_adapter.MyViewHolder holder, final int position)
    {
        Notif notif = list.get(position);

        holder.notif_sender.setText(notif.getSenderId());
        holder.notif_message.setText(notif.getContent());
        String caps = notif.getSenderId().toUpperCase();
        holder.icon_text.setText(caps.charAt(0)+"");
        applyClickEvents(holder,position);
        applyProfilePicture(holder);

        String timestamp = formatter.format(Calendar.getInstance().getTime());
        String senderTimestamp = notif.getTimestamp().substring(0,11);
        if(timestamp.equals(senderTimestamp))
            senderTimestamp = notif.getTimestamp().substring(12).trim();

        holder.notif_timestamp.setText(senderTimestamp);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface NotificationAdapterListener {
        void onNotificationRowClicked(int position);
    }

    private void applyProfilePicture(MyViewHolder holder) {

        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(getRandomMaterialColor("400"));

    }

    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.row.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                listener.onNotificationRowClicked(position);
            }
        });
    }
}