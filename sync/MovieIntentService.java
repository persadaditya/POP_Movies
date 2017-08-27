package com.app.phedev.popmovie.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by phedev in 2017.
 */

public class MovieIntentService extends IntentService {

    public MovieIntentService(){
        super("MovieIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        MovieSyncTask.syncMovie(this);
    }
}
