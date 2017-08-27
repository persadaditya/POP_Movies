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
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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

import static com.app.phedev.popmovie.activity.DetailActivity.EXTRA_ID;


@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,OnCursorClickListener {


    private RecyclerView mRecyclerView;
    private CostumCursorAdapter costumCursorAdapter;
    private ArrayList<Movie> movieList;
    ProgressBar mLoadingIndicator;
    public static final String LOG_TAG = CostumCursorAdapter.class.getName();
    private static final String BUNDLE_RECYCLER_STATE = "recyclerview.state";
    private static final String BUNDLE_ADAPTER_DATA = "adapter_data";
    private static final String BUNDLE_ITEM_VIEW_POSITION = "item_view_position";
    private static final String STATE_ITEMS = "items";
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;
    private static final int TASK_LOADER_ID = 100;
    private static final int TASK_LOADER_POP = 101;
    private static final int TASK_LOADER_RAT = 102;
    public static final String[] TABLE_PROJECTION = {
            MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIEID,
            MovieContract.FavoriteEntry.COLUMN_TITLE,
            MovieContract.FavoriteEntry.COLUMN_DATE,
            MovieContract.FavoriteEntry.COLUMN_USERRATING,
            MovieContract.FavoriteEntry.COLUMN_POSTER_PATH
    };
    private static boolean dataDownloaded = false;
    private static Cursor oldCursor;
    SimpleCursorAdapter cursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState != null ){
            checkPreferences();
            dataDownloaded = savedInstanceState.getBoolean(BUNDLE_ADAPTER_DATA);

            if (mListState != null){
                mListState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);
                layoutManager.onRestoreInstanceState(mListState);
            }

        }else {

            initViews();
            showLoading();
            MovieSyncUtils.initialize(this);
        }
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
        //pDialog = new ProgressDialog(this);
        //pDialog.setMessage("Fetch Your Movie");
        //pDialog.show();
        //pDialog.setCancelable(false);

        mRecyclerView = (RecyclerView)findViewById(R.id.grid_list);
        mLoadingIndicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);

        boolean isPortrait = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int column = isPortrait ? 2 : 4;

        layoutManager = new GridLayoutManager(this, column);
        mRecyclerView.setLayoutManager(layoutManager);

        movieList = new ArrayList<>();
        //moviePopAdapter = new MoviePopAdapter(this, movieList);
        costumCursorAdapter = new CostumCursorAdapter(null, this, this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(costumCursorAdapter);
        //moviePopAdapter.notifyDataSetChanged();

        LoaderManager.enableDebugLogging(true);

        checkPreferences();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putIntArray("ARTICLE_SCROLL_POSITION",
               // new int[]{ scrView.getScrollX(), scrView.getScrollY()});
        outState.putSerializable(STATE_ITEMS, movieList);
       // outState.putParcelableArrayList(BUNDLE_ADAPTER_DATA, movieList);
       // outState.putParcelableArrayList("TES", (ArrayList<? extends Parcelable>) moviePopAdapter.getMoviesData());
        outState.putBoolean(BUNDLE_ADAPTER_DATA, dataDownloaded);
        oldCursor = costumCursorAdapter.swapCursor(null);

        mListState = layoutManager.onSaveInstanceState();
        if (mListState != null){
            outState.putParcelable(BUNDLE_RECYCLER_STATE, mListState);
        }
        outState.putInt(BUNDLE_ITEM_VIEW_POSITION, layoutManager.findFirstCompletelyVisibleItemPosition());


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
        int loader = TASK_LOADER_POP;
        String sortOrder = preferences.getString(
                this.getString(R.string.sort_order_key),
                this.getString(R.string.most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.most_popular))){
            Log.d(LOG_TAG,"sort by most popular");
            loader = TASK_LOADER_POP;
            //loadJSON();
            getSupportLoaderManager().initLoader(loader,null,this);
        }else if (sortOrder.equals(this.getString(R.string.favorite))){
            Log.d(LOG_TAG, "sort by favorite movies");
            loader = TASK_LOADER_ID;
            getSupportLoaderManager().initLoader(loader,null,this);
        }
        else{
            Log.d(LOG_TAG,"sort by vote average");
            //loadJSONrated();
            loader = TASK_LOADER_RAT;
            getSupportLoaderManager().initLoader(loader,null,this);
        }

    }

    @Override
    protected void onRestart() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int loader;
        String sortOrder = preferences.getString(
                this.getString(R.string.sort_order_key),
                this.getString(R.string.most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.most_popular))){
            Log.d(LOG_TAG,"sort by most popular");
            loader = TASK_LOADER_POP;
            //loadJSON();
            getSupportLoaderManager().initLoader(loader, null,this);
        }else if (sortOrder.equals(this.getString(R.string.favorite))){
            Log.d(LOG_TAG, "sort by favorite movies");
            loader = TASK_LOADER_ID;
            getSupportLoaderManager().initLoader(loader, null, this);
        }
        else{
            Log.d(LOG_TAG,"sort by vote average");
            //loadJSONrated();
            loader = TASK_LOADER_RAT;
            getSupportLoaderManager().initLoader(loader,null,this);
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int loader;
        String sortOrder = preferences.getString(
                this.getString(R.string.sort_order_key),
                this.getString(R.string.most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.most_popular))){
            Log.d(LOG_TAG,"sort by most popular");
            loader = TASK_LOADER_POP;
            getSupportLoaderManager().initLoader(loader, null,this);
        }else if (sortOrder.equals(this.getString(R.string.favorite))){
            Log.d(LOG_TAG, "sort by favorite movies");
            loader = TASK_LOADER_ID;
            getSupportLoaderManager().initLoader(loader, null, this);
        }
        else{
            Log.d(LOG_TAG,"sort by vote average");
            loader = TASK_LOADER_RAT;
            getSupportLoaderManager().initLoader(loader,null,this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //positionIndex = layoutManager.findFirstCompletelyVisibleItemPosition();
        //View startView = mRecyclerView.getChildAt(0);
        //topView = (startView == null) ? 0 : startView.getTop() - mRecyclerView.getPaddingTop();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if(position != null)

        mListState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);

    }


    @Override
    public Loader<Cursor> onCreateLoader(final int id, Bundle args) {
        CursorLoader cursorLoader;
        if (dataDownloaded) {
            cursorLoader = new CursorLoader(getActivity(),
                    null, TABLE_PROJECTION, null, null, null);
            cursorLoader.deliverResult(oldCursor);
        } else {

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
        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //costumCursorAdapter.updateList(data);
        if (costumCursorAdapter!=null){
            data.moveToPosition(0);
            costumCursorAdapter.swapCursor(data);
            showDataView();
            dataDownloaded = true;
        } else {
            Log.v(LOG_TAG,"on load finished, adapter is null");
        }
        //if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        //mRecyclerView.smoothScrollToPosition(mPosition);
        //costumCursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //costumCursorAdapter.updateList(null);

        if (costumCursorAdapter != null){
            costumCursorAdapter.swapCursor(null);
        }else {
            Log.v(LOG_TAG,"on loader reset, adapter is null");
        }

    }

    @Override
    public void onCursorClickListener(int movie_id) {
        Intent newIntent = new Intent(MainActivity.this, DetailActivity.class);
        newIntent.putExtra(EXTRA_ID, movie_id);
    }



}
