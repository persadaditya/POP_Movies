package com.app.phedev.popmovie.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.app.phedev.popmovie.data.MovieContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by phedev in 2017.
 */

public class MovieSyncUtils {

    private static boolean sInitialized;

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static final String POPMOVIE_SYNC_TAG = "movie-sync";

    private static void scheduleMovieJobDispatcherSync (final Context context){
        Driver driver = new GooglePlayDriver (context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncMovieJob = dispatcher.newJobBuilder()
                .setService(MovieJobService.class)
                .setTag(POPMOVIE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMovieJob);
    }

    synchronized public static void initialize (final Context context){

        if (sInitialized) return;
        sInitialized = true;

        scheduleMovieJobDispatcherSync(context);


        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri MovieUri = MovieContract.FavoriteEntry.CONTENT_URI;
                String projectionColumns = MovieContract.FavoriteEntry._ID;
                String selection = MovieContract.FavoriteEntry.COLUMN_DATE;
                Cursor cursor = context.getContentResolver().query(
                        MovieUri,
                        new String[]{projectionColumns},
                        selection,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                /* Make sure to close the Cursor to avoid memory leaks! */
                else {
                    cursor.close();
                }
            }
        });
        checkForEmpty.start();
    }

    public static void startImmediateSync(final Context context){
        Intent intentToSyncImmediately = new Intent(context, MovieIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
