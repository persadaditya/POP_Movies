package com.app.phedev.popmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phedev.popmovie.R;
import com.app.phedev.popmovie.pojo.Trailer;

import java.util.List;

/**
 * Created by phedev in 2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.myViewHolder> {

    private Context context;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList){
        this.context = context;
        this.trailerList = trailerList;
    }

    @Override
    public TrailerAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_card,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.myViewHolder holder, int position) {
        holder.title.setText(trailerList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thmbnail;

        public myViewHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.titles);
            thmbnail = (ImageView)itemView.findViewById(R.id.imageview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Trailer clickedData = trailerList.get(pos);
                        String videoId = trailerList.get(pos).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("VIDEO_ID", videoId);
                        context.startActivity(intent);
                        Toast.makeText(view.getContext(), "you clicked" + clickedData.getName(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
