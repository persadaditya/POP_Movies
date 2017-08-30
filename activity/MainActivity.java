package com.app.phedev.popmovie.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.app.phedev.popmovie.R;
import com.app.phedev.popmovie.adapter.CostumCursorAdapter;
import com.app.phedev.popmovie.data.MovieContract;
import com.app.phedev.popmovie.listener.OnCursorClickListener;
import com.app.phedev.popmovie.pojo.Movie;
import com.app.phedev.popmovie.sync.MovieSyncUtils;

import java.util.ArrayList;


@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,OnCursorClickListener {


    private RecyclerView mRecyclerView;
    private CostumCursorAdapter costumCursorAdapter;
    private ArrayList<Movie> movieList;
    ProgressBar mLoadingIndicator;
    public static final String LOG_TAG = CostumCursorAdapter.class.getName();
    private LinearLayoutManager layoutManager;
    private static final int TASK_LOADER_ID = 100;
    private static final int TASK_LOADER_POP = 101;
    private static final int TASK_LOADER_RAT = 102;
    private static int loader;
    public static final String[] TABLE_PROJECTION = {
            MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIEID,
            MovieContract.FavoriteEntry.COLUMN_TITLE,
            MovieContract.FavoriteEntry.COLUMN_DATE,
            MovieContract.FavoriteEntry.COLUMN_USERRATING,
            MovieContract.FavoriteEntry.COLUMN_POSTER_PATH,
            MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            initViews();
            showLoading();
            MovieSyncUtils.initialize(this);

    }

    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }

    private void initViews() {
        mRecyclerView = (RecyclerView)findViewById(R.id.grid_list);
        mLoadingIndicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);

        boolean isPortrait = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int column = isPortrait ? 2 : 4;

        layoutManager = new GridLayoutManager(this, column);
        mRecyclerView.setLayoutManager(layoutManager);

        movieList = new ArrayList<>();
        costumCursorAdapter = new CostumCursorAdapter(null, this, this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(costumCursorAdapter);

        LoaderManager.enableDebugLogging(true);

        checkPreferences();
    }


    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        switch (menuItemThatWasSelected){
            case R.id.Settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void checkPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String sortOrder = preferences.getString(
                this.getString(R.string.sort_order_key),
                this.getString(R.string.most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.most_popular))){
            Log.d(LOG_TAG,"sort by most popular");
            loader = TASK_LOADER_POP;
            getSupportLoaderManager().initLoader(loader,null,this);
        }else if (sortOrder.equals(this.getString(R.string.favorite))){
            Log.d(LOG_TAG, "sort by favorite movies");
            loader = TASK_LOADER_ID;
            getSupportLoaderManager().initLoader(loader,null,this);
        }
        else{
            Log.d(LOG_TAG,"sort by vote average");
            loader = TASK_LOADER_RAT;
            getSupportLoaderManager().initLoader(loader,null,this);
        }

    }




    @Override
    public Loader<Cursor> onCreateLoader(final int id, Bundle args) {

        switch (id) {
            case TASK_LOADER_ID:
                Uri favUri = MovieContract.FavoriteEntry.CONTENT_URI;
                String sortOrd = MovieContract.FavoriteEntry._ID + " ASC";
                String selection = MovieContract.FavoriteEntry.COLUMN_DATE;

                return new CursorLoader(getActivity(),
                        favUri,
                        TABLE_PROJECTION,
                        selection,
                        null,
                        sortOrd);

            case TASK_LOADER_POP:
                Uri popUri = MovieContract.FavoriteEntry.CONTENT_URI_POP;
                String sortOrdPop = MovieContract.FavoriteEntry._ID + " ASC";
                String selectionPop = MovieContract.FavoriteEntry.COLUMN_DATE;

                return new CursorLoader(getActivity(),
                        popUri,
                        TABLE_PROJECTION,
                        selectionPop,
                        null,
                        sortOrdPop);

            case TASK_LOADER_RAT:
                Uri ratUri = MovieContract.FavoriteEntry.CONTENT_URI_RATED;
                String sortOrdRat = MovieContract.FavoriteEntry._ID + " ASC";
                String selectionRat = MovieContract.FavoriteEntry.COLUMN_DATE;

                return new CursorLoader(getActivity(),
                        ratUri,
                        TABLE_PROJECTION,
                        selectionRat,
                        null,
                        sortOrdRat);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);

        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        costumCursorAdapter.swapCursor(data);

        if (data.getCount() != 0)showDataView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

            costumCursorAdapter.swapCursor(null);

    }

    @Override
    public void onCursorClickListener(long movie_id) {
        Intent newIntent = new Intent(MainActivity.this, DetailActivity.class);
        if (loader == TASK_LOADER_POP){
            Uri clickedUri = MovieContract.FavoriteEntry.buildMoviePopUriWithID(movie_id);
            newIntent.setData(clickedUri);
            newIntent.putExtra("loader", loader);
            startActivity(newIntent);
        } else if (loader == TASK_LOADER_RAT){
            Uri clickedUri = MovieContract.FavoriteEntry.buildMovieRatUriWithID(movie_id);
            newIntent.setData(clickedUri);
            newIntent.putExtra("loader", loader);
            startActivity(newIntent);
        }else {
            Uri clickedUri = MovieContract.FavoriteEntry.buildMovieFavUriWithID(movie_id);
            newIntent.setData(clickedUri);
            newIntent.putExtra("loader", loader);
            startActivity(newIntent);
        }
    }



}
