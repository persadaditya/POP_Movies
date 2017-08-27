package com.app.phedev.popmovie.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.phedev.popmovie.R;

/**
 * Created by phedev in 2017.
 */

public final class MoviePreference {

    public static String preferedLoader(Context context, int i){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.sort_order_key);
        String defaultUnits = context.getString(R.string.most_popular);
        String preferredUnits = sp.getString(keyForUnits, defaultUnits);
        String pop = context.getString(R.string.most_popular);


        if (preferredUnits.equals(defaultUnits)){
            int loader = 100;
        }



        return preferredUnits;
    }
}
