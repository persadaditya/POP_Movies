package com.app.phedev.popmovie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.phedev.popmovie.R;
import com.app.phedev.popmovie.listener.OnCursorClickListener;
import com.bumptech.glide.Glide;

/**
 * Created by phedev in 2017.
 */

public class CostumCursorAdapter extends RecyclerView.Adapter<CostumCursorAdapter.TaskViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    OnCursorClickListener mClickHandler;


    public CostumCursorAdapter(Cursor c, Context ctx, OnCursorClickListener clickHandler){
        this.mCursor = c;
        this.mContext = ctx;
        this.mClickHandler = clickHandler;
    }

    public void updateList(Cursor c){
        this.mCursor = c;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_list_item, parent, false);

        return new TaskViewHolder(view);

    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {


        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(0);
        String title = mCursor.getString(2);
        Double rate = mCursor.getDouble(3);
        String date = mCursor.getString(4);
        String poster = mCursor.getString(5);
        //String plot = mCursor.getString(6);

        holder.itemView.setTag(id);
        holder.txtTitle.setText(title);
        holder.txtRate.setText(String.valueOf(rate));
        holder.txtRelease.setText(date);

        Glide.with(mContext)
                .load(poster)
                .into(holder.thumbnail_fav);


    }

    public Cursor swapCursor(Cursor c){
        mCursor = c;
        notifyDataSetChanged();
        if (mCursor == c){
            return  null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null){
            this.notifyDataSetChanged();
        }

        return temp;
        //return c;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }



    public class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView txtTitle, txtRate, txtPlot = null, txtRelease;
        final ImageView thumbnail_fav;


        public TaskViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView)itemView.findViewById(R.id.txv_title);
            txtRate = (TextView)itemView.findViewById(R.id.tx_rate);
            txtRelease = (TextView)itemView.findViewById(R.id.tx_date);
            thumbnail_fav = (ImageView)itemView.findViewById(R.id.img_row);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mCursor.moveToPosition(position);
                    mClickHandler.onCursorClickListener((int) mCursor.getLong(1));


                }
            });
        }
    }
}
