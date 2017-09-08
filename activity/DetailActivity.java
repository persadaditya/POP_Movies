package com.app.phedev.popmovie.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
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
import com.app.phedev.popmovie.pojo.Review;
import com.app.phedev.popmovie.pojo.ReviewResponse;
import com.app.phedev.popmovie.pojo.Trailer;
import com.app.phedev.popmovie.pojo.TrailerResponse;
import com.app.phedev.popmovie.retrofit.Client;
import com.app.phedev.popmovie.retrofit.Service;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    TextView movieNames, plotSynopsis, userRating, releaseDate;
    ImageView posterImg;
    private RecyclerView recyclerView1, recyclerView2;
    private ArrayList<Trailer> trailerList;
    private TrailerAdapter trailerAdapter;
    private ArrayList<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private NestedScrollView nestedScrollView;
    private MovieDBHelper mOpenHelper;
    private static final int LOADER_DET = 500;
    private static final int FAV_LOADER = 100;

    public static final int INDEX_IDMOVIE = 1;
    public static final int INDEX_TITLE = 2;
    public static final int INDEX_RATING = 3;
    public static final int INDEX_DATE = 4;
    public static final int INDEX_POSTER = 5;
    public static final int INDEX_PLOT = 6;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String BUNDLE_TRAILER = "trailer_data";
    private static final String BUNDLE_REVIEW = "review_data";
    private static final String BUNDLE_SCROLL = "scroll_data";

    int movie_id, categories;
    FloatingActionButton fabut;
    private Uri mUri;
    private Boolean isFavorited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mainView();
        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        movie_id = Integer.parseInt(mUri.getPathSegments().get(INDEX_IDMOVIE));
        categories = getIntent().getIntExtra("loader",100);

        mOpenHelper = new MovieDBHelper(getApplicationContext());
        initActionbar();

        if(savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_TRAILER)) {
            Log.d(TAG, "ON CREATE: get saved instance state with DATA TRAILER");
            trailerList = savedInstanceState.getParcelableArrayList(BUNDLE_TRAILER);
        } else {
            Log.d(TAG, "NULL DATA: get data trailer from internet");
            trailerList = new ArrayList<>();
            new getTrailer().execute();
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_REVIEW)) {
            Log.d(TAG,"ON CREATE: get saved instance state with DATA REVIEW");
            reviewList = savedInstanceState.getParcelableArrayList(BUNDLE_REVIEW);
        }else {
            Log.d(TAG, "NULL DATA: get data review from internet");
            reviewList = new ArrayList<>();
            new getReview().execute();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SCROLL)){
            Log.d(TAG, "GET DATA SCROLL POSITION");
            final int[]scrollPosition = savedInstanceState.getIntArray(BUNDLE_SCROLL);
            if (scrollPosition != null && scrollPosition.length==2){
                nestedScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.scrollTo(scrollPosition[0],scrollPosition[1]);
                    }
                });
            }
        } else
        initViews();
        initViews2();

        getSupportLoaderManager().initLoader(LOADER_DET,null,this);
    }

    private void mainView(){
        posterImg = (ImageView)findViewById(R.id.thumbnail_image);
        plotSynopsis = (TextView)findViewById(R.id.synopsis);
        movieNames = (TextView)findViewById(R.id.title);
        userRating = (TextView)findViewById(R.id.rating);
        releaseDate = (TextView)findViewById(R.id.date);
        fabut = (FloatingActionButton)findViewById(R.id.fab);
        nestedScrollView = (NestedScrollView) findViewById(R.id.contentDetail);
        recyclerView2 = (RecyclerView)findViewById(R.id.reviewList);
        recyclerView1 = (RecyclerView)findViewById(R.id.trailerList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (trailerList != null){
            outState.putParcelableArrayList(BUNDLE_TRAILER, new ArrayList<Parcelable>(trailerList));
        }
        if (reviewList != null){
            outState.putParcelableArrayList(BUNDLE_REVIEW, new ArrayList<Parcelable>(reviewList));
        }
        outState.putIntArray(BUNDLE_SCROLL, new int[]{nestedScrollView.getScrollX(),nestedScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        trailerList = savedInstanceState.getParcelableArrayList(BUNDLE_TRAILER);
        reviewList = savedInstanceState.getParcelableArrayList(BUNDLE_REVIEW);
        final int[]scrollPosition = savedInstanceState.getIntArray(BUNDLE_SCROLL);
        if (scrollPosition != null && scrollPosition.length==2){
            nestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.scrollTo(scrollPosition[0],scrollPosition[1]);
                }
            });
        }
    }

    private void initActionbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void initViews(){
        trailerAdapter = new TrailerAdapter(getApplicationContext(), trailerList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setAdapter(trailerAdapter);
    }

    private void initViews2(){
        reviewAdapter = new ReviewAdapter(getApplicationContext(), reviewList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setAdapter(reviewAdapter);
    }

    class getTrailer extends AsyncTask<Void,Void,Trailer>{
        public getTrailer(){

        }

        @Override
        protected Trailer doInBackground(Void... voids) {
            try {
                if (BuildConfig.THE_MOVIE_DB_API.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please insert your API Key from themoviedb.org",Toast.LENGTH_LONG).show();

                }
                Client clients = new Client();
                Service apiService = Client.getClient().create(Service.class);
                Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API);
                call.enqueue(new Callback<TrailerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                        List<Trailer> trailers = response.body().getResults();
                        recyclerView1.setAdapter(new TrailerAdapter(getApplicationContext(),trailers));
                        trailerAdapter.notifyDataSetChanged();
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
            return null;
        }
    }

    class getReview extends AsyncTask<Void,Void,Review>{
        public getReview(){

        }

        @Override
        protected Review doInBackground(Void... voids) {
            try {
                if (BuildConfig.THE_MOVIE_DB_API.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please insert your API Key from themoviedb.org",Toast.LENGTH_LONG).show();

                }
                Client clients = new Client();
                Service apiService = Client.getClient().create(Service.class);
                Call<ReviewResponse> call = apiService.getMovieReview(movie_id, BuildConfig.THE_MOVIE_DB_API);
                call.enqueue(new Callback<ReviewResponse>() {
                    @Override
                    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                        List<Review> reviews = response.body().getResults();
                        recyclerView2.setAdapter(new ReviewAdapter(getApplicationContext(),reviews));
                        recyclerView2.smoothScrollToPosition(0);
                        reviewAdapter.notifyDataSetChanged();
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
            return null;
        }
    }


    public void saveFavorite(Cursor cursor){
        ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteEntry.COLUMN_MOVIEID, cursor.getInt(INDEX_IDMOVIE));
        values.put(MovieContract.FavoriteEntry.COLUMN_TITLE, cursor.getString(INDEX_TITLE));
        values.put(MovieContract.FavoriteEntry.COLUMN_DATE, cursor.getString(INDEX_DATE));
        values.put(MovieContract.FavoriteEntry.COLUMN_USERRATING, cursor.getDouble(INDEX_RATING));
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, cursor.getString(INDEX_POSTER));
        values.put(MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, cursor.getString(INDEX_PLOT));

        Uri uri = MovieContract.FavoriteEntry.CONTENT_URI;

        getContentResolver().insert(uri, values);
        getContentResolver().notifyChange(uri, null);
    }

    public void deleteFavorite(Cursor cursor){
        int id = cursor.getInt(0);
        String stringId = Integer.toString(id);
        Uri uri = MovieContract.FavoriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri,null,null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_DET:
                return new CursorLoader(DetailActivity.this,
                       mUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        movieNames.setText(data.getString(INDEX_TITLE));
        Double rate = data.getDouble(INDEX_RATING);
        userRating.setText(String.valueOf(rate));
        releaseDate.setText(data.getString(INDEX_DATE));
        plotSynopsis.setText(data.getString(INDEX_PLOT));
        String poster = data.getString(INDEX_POSTER);

        Glide.with(this)
                .load(poster)
                .into(posterImg);

        //compare data whether it's in favorites table or not
        //I found solution from https://stackoverflow.com/questions/12919686/compare-sqlite-with-string
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String table_name = MovieContract.FavoriteEntry.TABLE_NAME;
        String column_name = MovieContract.FavoriteEntry.COLUMN_MOVIEID;
        String idMovie = String.valueOf(movie_id);
        String query = "SELECT * FROM " + table_name +" WHERE "+ column_name + "=" + idMovie;
        Cursor cursor = db.rawQuery(query, null);

        if (categories==FAV_LOADER){
            fabut.setImageResource(R.drawable.delete1600);
            fabut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor =getSharedPreferences("com.app.phedev.popmovie.activity.DetailActivity", MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite Added", false);
                    editor.apply();
                    deleteFavorite(data);
                    Toast.makeText(getApplicationContext(),"Remove From Favorite", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }else if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                // Do whatever you like with the result.
                fabut.setBackgroundResource(R.drawable.delete1600);
                fabut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteFavorite(data);
                        Snackbar.make(view, "This Movie Already Favorited", Snackbar.LENGTH_SHORT).show();
                    }
                });
                cursor.moveToNext();
            }
        }else if (cursor.getCount() == 0){
            fabut.setBackgroundResource(R.drawable.favicon);
            fabut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveFavorite(data);
                    Toast.makeText(getApplicationContext(), "Added to Favorite",Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
