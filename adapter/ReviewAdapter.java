package com.app.phedev.popmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.phedev.popmovie.R;
import com.app.phedev.popmovie.pojo.Review;

import java.util.List;

/**
 * Created by phedev in 2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.authors.setText(reviewList.get(position).getAuthor());
        holder.contents.setText(reviewList.get(position).getContent());

    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authors;
        public TextView contents;


        public ViewHolder(View itemView) {
            super(itemView);

            authors = (TextView)itemView.findViewById(R.id.tx_author);
            contents = (TextView)itemView.findViewById(R.id.tx_content);

        }
    }
}
