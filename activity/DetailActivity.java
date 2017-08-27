package com.app.phedev.popmovie.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phedev.popmovie.BuildConfig;
import com.app.phedev.popmovie.R;
import com.app.phedev.popmovie.adapter.ReviewAdapter;
import com.app.phedev.popmovie.adapter.TrailerAdapter;
import com.app.phedev.popmovie.data.MovieContract;
import com.app.phedev.popmovie.data.MovieDBHelper;
import com.app.phedev.popmovie.data.MovieProvider;
import com.app.phedev.popmovie.retrofit.Client;
import com.app.phedev.popmovie.retrofit.Service;
import com.app.phedev.popmovie.pojo.Movie;
import com.app.phedev.popmovie.pojo.Review;
import com.app.phedev.popmovie.pojo.ReviewResponse;
import com.app.phedev.popmovie.pojo.Trailer;
import com.app.phedev.popmovie.pojo.TrailerResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    TextView movieNames, plotSynopsis, userRating, releaseDate;
    ImageView posterImg;
    private RecyclerView recyclerView1, recyclerView2;
    private List<Trailer> trailerList;
    private TrailerAdapter trailerAdapter;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private MovieDBHelper movieDBHelper;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;
    private MovieProvider movieProvider;

    Movie movie;
    int movie_id;
    String thumbnail, movieName, synopsis, dateOfRelease, ratings;
    FloatingActionButton fabut;
    public static final String EXTRA_ID = "movie_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movie_id = getIntent().getIntExtra(EXTRA_ID,0);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Movie");

        initCollapsingToolbar();

        posterImg = (ImageView)findViewById(R.id.thumbnail_image);
        plotSynopsis = (TextView)findViewById(R.id.synopsis);
        movieNames = (TextView)findViewById(R.id.title);
        userRating = (TextView)findViewById(R.id.rating);
        releaseDate = (TextView)findViewById(R.id.date);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("movies")){

            movie = getIntent().getParcelableExtra("movies");

            thumbnail = movie.getPosterPath();
            movieName = movie.getOriTitle();
            synopsis = movie.getPlot();
            ratings = Double.toString(movie.getVoteAvg());
            dateOfRelease = movie.getRelease();
            movie_id = movie.getId();
            Log.v("ID DETAIL MOVIE =", String.valueOf(movie_id));

            String poster = thumbnail;

            Glide.with(this)
                    .load(poster)
                    .into(posterImg);

            movieNames.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(ratings);
            releaseDate.setText(dateOfRelease);

        }else{
            Toast.makeText(this,"No DATA",Toast.LENGTH_LONG).show();
        }

        initViews();
        initViews2();
    }



    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout)findViewById(R.id.colapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange +verticalOffset == 0){
                    collapsingToolbarLayout.setTitle("pop movie");
                    isShow = true;
                }else if (isShow){
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }

            }
        });

    }

    private void initViews(){

        recyclerView1 = (RecyclerView)findViewById(R.id.trailerList);
        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this, trailerList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView1.setLayoutManager(layoutManager);
        //recyclerView1.setAdapter(trailerAdapter);
        loadJSON();
        trailerAdapter.notifyDataSetChanged();
    }

    private void initViews2(){
        recyclerView2 = (RecyclerView)findViewById(R.id.reviewList);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(layoutManager);
        //recyclerView2.setAdapter(reviewAdapter);
        loadJSON2();
        reviewAdapter.notifyDataSetChanged();
    }

    private void loadJSON(){
        try {
            if (BuildConfig.THE_MOVIE_DB_API.isEmpty()){
                Toast.makeText(getApplicationContext(),"Please insert your API Key from themoviedb.org",Toast.LENGTH_LONG).show();
                return;
            }
            Client clients = new Client();
            Service apiService = clients.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse( Call<TrailerResponse> call,  Response<TrailerResponse> response) {
                    List<Trailer> trailers = response.body().getResults();
                    recyclerView1.setAdapter(new TrailerAdapter(getApplicationContext(),trailers));
                    recyclerView1.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(DetailActivity.this,"Failed to Connect Data",Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(DetailActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }

    }

    private void loadJSON2(){
        try {
            if (BuildConfig.THE_MOVIE_DB_API.isEmpty()){
                Toast.makeText(getApplicationContext(),"Please insert your API Key from themoviedb.org",Toast.LENGTH_LONG).show();
                return;
            }
            Client clients = new Client();
            Service apiService = clients.getClient().create(Service.class);
            Call<ReviewResponse> call = apiService.getMovieReview(movie_id, BuildConfig.THE_MOVIE_DB_API);
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    List<Review> reviews = response.body().getResults();
                    recyclerView2.setAdapter(new ReviewAdapter(getApplicationContext(),reviews));
                    recyclerView2.smoothScrollToPosition(0);
                    Log.d("adapter review", String.valueOf(reviews));
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(DetailActivity.this,"Failed to Connect Data",Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(DetailActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }



    Boolean changeFav = false;


    public void clickFav(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        changeFav = !changeFav;

        if (changeFav = true){
            SharedPreferences.Editor editor =getSharedPreferences("com.app.phedev.popmovie.activity.DetailActivity", MODE_PRIVATE).edit();
            editor.putBoolean("Favorite Added", true);
            editor.apply();
            saveFavorite();
            //it still error whenever I change by setImageResources or setBackground
            //fabut.setBackgroundColor(Color.YELLOW);
            Snackbar.make(view, "Added to favorite", Snackbar.LENGTH_SHORT).show();

        }else {
            int movie_id = getIntent().getExtras().getInt("id");
            movieDBHelper = new MovieDBHelper(DetailActivity.this);
            movieDBHelper.deleteFavorite(movie_id);
            SharedPreferences.Editor editor = getSharedPreferences("com.app.phedev.popmovie.activity.DetailActivity", MODE_PRIVATE).edit();
            editor.putBoolean("Favorite Removed", false);
            editor.apply();
            //fabut.setBackgroundColor(Color.parseColor("#FF4081"));
            Snackbar.make(view, "Removed from favorite", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void saveFavorite(){
        movieDBHelper = new MovieDBHelper(activity);
        favorite = new Movie();
        Double rate = movie.getVoteAvg();

        favorite.setId(movie_id);
        favorite.setOriTitle(movieName);
        favorite.setPosterPath(thumbnail);
        favorite.setRelease(dateOfRelease);
        favorite.setVoteAvg(rate);
        favorite.setPlot(synopsis);
        //movieDBHelper.addFavorite(favorite);

        ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteEntry.COLUMN_MOVIEID, movie.getId());
        values.put(MovieContract.FavoriteEntry.COLUMN_TITLE, movie.getOriTitle());
        values.put(MovieContract.FavoriteEntry.COLUMN_DATE, movie.getRelease());
        values.put(MovieContract.FavoriteEntry.COLUMN_USERRATING, movie.getVoteAvg());
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlot());

        Uri uri = getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);

        if (uri != null){
            Toast.makeText(getBaseContext(), uri.toString(),Toast.LENGTH_SHORT).show();
        }

        finish();
    }

}
