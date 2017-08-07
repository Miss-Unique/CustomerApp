package com.example.soumyaagarwal.customerapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.soumyaagarwal.customerapp.R;
import com.example.soumyaagarwal.customerapp.helper.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class bigimage_adapter extends  RecyclerView.Adapter<bigimage_adapter.MyViewHolder>
{
    ArrayList<String> list = new ArrayList<>();
    private Context context;
    bigimage_adapterListener listener;

    public bigimage_adapter(ArrayList<String> list, Context context,     bigimage_adapterListener listener)
    {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TouchImageView img;
        public ImageButton download_taskdetail_image;
        public ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (TouchImageView) itemView.findViewById(R.id.image);
            download_taskdetail_image = (ImageButton) itemView.findViewById(R.id.download_taskdetail_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progresshere);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bigimage_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String topic = list.get(position);
        Picasso.with(context).load(Uri.parse(topic)).into(holder.img);
        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface bigimage_adapterListener {
        void ondownloadButtonClicked(int position, MyViewHolder holder);
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {

        holder.download_taskdetail_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                listener.ondownloadButtonClicked(position,holder);
            }
        });
    }
}
