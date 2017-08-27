package com.app.phedev.popmovie.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.app.phedev.popmovie.BuildConfig;
import com.app.phedev.popmovie.data.MovieContract;
import com.app.phedev.popmovie.pojo.Movie;
import com.app.phedev.popmovie.pojo.MovieResponse;
import com.app.phedev.popmovie.retrofit.Client;
import com.app.phedev.popmovie.retrofit.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_URI_POP;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_URI_RATED;

/**
 * Created by phedev in 2017.
 */

public class MovieSyncTask {
    private static int movie_id;

    synchronized public static void syncMovie (final Context context){

        // FETCH DATA MOVIE POPULAR
        try {
            if (BuildConfig.THE_MOVIE_DB_API.isEmpty()){
                Toast.makeText(context,"Please insert your API Key from themoviedb.org",Toast.LENGTH_LONG).show();
                //pDialog.dismiss();
            }

            final Client client = new Client();
            Service apiService =
                    Client.getClient().create(Service.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResult();
                    ContentValues[] contentValues = new ContentValues[movies.size()];
                    for (int i =0; i<movies.size(); i++) {
                        Movie listObj = movies.get(i);
                        movie_id = listObj.getId();

                        ContentValues cv = new ContentValues();
                        cv.put(MovieContract.FavoriteEntry.COLUMN_MOVIEID, movie_id);
                        cv.put(MovieContract.FavoriteEntry.COLUMN_DATE, listObj.getRelease());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_TITLE, listObj.getOriTitle());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_USERRATING, listObj.getVoteAvg());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, listObj.getPosterPath());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, listObj.getPlot());

                        contentValues[i] = cv;
                    }
                    Uri uri = CONTENT_URI_POP;
                    context.getContentResolver().delete(
                            uri,null,null
                    );
                    context.getContentResolver().bulkInsert(
                            uri,
                            contentValues);
                    context.getContentResolver().notifyChange(uri, null);

                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(context,"Fail to Connect Data",Toast.LENGTH_LONG).show();
                    //pDialog.dismiss();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
            //pDialog.dismiss();

        }

        //FETCH DATA MOVIE TOP RATED
        //---------------------------
        try {
            if (BuildConfig.THE_MOVIE_DB_API.isEmpty()){
                Toast.makeText(context,"Please insert your API Key from themoviedb.org",Toast.LENGTH_LONG).show();

            }

            final Client client = new Client();
            Service apiService =
                    Client.getClient().create(Service.class);
            Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<com.app.phedev.popmovie.pojo.Movie> movies = response.body().getResult();
                    ContentValues[] contentValues = new ContentValues[movies.size()];
                    for (int i =0; i<movies.size(); i++) {
                        Movie listObj2 = movies.get(i);
                       movie_id = listObj2.getId();

                        ContentValues cv = new ContentValues();
                        cv.put(MovieContract.FavoriteEntry.COLUMN_MOVIEID, movie_id);
                        cv.put(MovieContract.FavoriteEntry.COLUMN_DATE, listObj2.getRelease());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_TITLE, listObj2.getOriTitle());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_USERRATING, listObj2.getVoteAvg());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, listObj2.getPosterPath());
                        cv.put(MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, listObj2.getPlot());

                        contentValues[i] = cv;
                    }

                    Uri uri = CONTENT_URI_RATED;
                    context.getContentResolver().delete(
                            uri,null,null
                    );
                    context.getContentResolver().bulkInsert(
                            uri,
                            contentValues);
                    context.getContentResolver().notifyChange(uri, null);

                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(context,"Fail to Connect Data",Toast.LENGTH_LONG).show();

                }
            });
        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();

        }

        //FETCH DATA TRAILER FOR MOVIE


    }
}

