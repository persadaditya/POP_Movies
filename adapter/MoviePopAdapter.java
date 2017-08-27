package com.app.phedev.popmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.phedev.popmovie.R;
import com.app.phedev.popmovie.activity.DetailActivity;
import com.app.phedev.popmovie.pojo.Movie;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by phedev in 2017.
 */

public class MoviePopAdapter extends RecyclerView.Adapter<MoviePopAdapter.MovieAdapterViewHolder> {
    private Context mCtx;
    private List<Movie> movieList ;



    public MoviePopAdapter(Context mCtx, List<com.app.phedev.popmovie.pojo.Movie> movieList) {
        this.mCtx = mCtx;
        this.movieList = movieList;
    }




    @Override
    public MoviePopAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item,parent,false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviePopAdapter.MovieAdapterViewHolder holder, int position) {
        holder.title.setText(movieList.get(position).getOriTitle());
        String vote = Double.toString(movieList.get(position).getVoteAvg());
        holder.userRating.setText(vote);
        holder.release.setText(movieList.get(position).getRelease());

        String poster =  movieList.get(position).getPosterPath();

        Glide.with(mCtx)
                .load(poster)
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView title,userRating,release;
        public ImageView thumbnail;


        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.txv_title);
            userRating = (TextView)itemView.findViewById(R.id.tx_rate);
            release = (TextView)itemView.findViewById(R.id.tx_date);
            thumbnail = (ImageView)itemView.findViewById(R.id.img_row);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(mCtx, DetailActivity.class);
                        intent.putExtra("movies", clickedDataItem );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                    }
                }
            });
        }
    }

    public void setMoviesData(List<Movie> moviesData) {
        movieList = moviesData;
        notifyDataSetChanged();
    }

    public List<Movie> getMoviesData() {
        return movieList;
    }
}
