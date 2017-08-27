package com.app.phedev.popmovie.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by phedev in 2017.
 */

public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.app.phedev.popmovie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "favorites";
    public static final String PATH_MOVIE2 = "popular";
    public static final String PATH_MOVIE3 = "rating";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class FavoriteEntry implements BaseColumns {


        // Name of Tables
        public static final String TABLE_NAME = "favorites";
        public static final String TABLE_NAME2 = "popular";
        public static final String TABLE_NAME3 = "rating";
        public static final String TABLE_TRAILER = "trailer";
        public static final String TABLE_REVIEW = "review";

        // Name of Column
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USERRATING = "userrating";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_PLOT_SYNOPSIS = "overview";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";


        //Content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final Uri CONTENT_URI_POP = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE2)
                .build();

        public static final Uri CONTENT_URI_RATED = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE3)
                .build();

        public static final Uri CONTENT_URI_TRAILER = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILER)
                .build();

        public static final Uri CONTENT_URI_REVIEW = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW)
                .build();

        public static final String CONTENT_DIR_FAV =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_DIR_POP =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME2;

        public static final String CONTENT_DIR_RATED =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME3;

        public static final String CONTENT_DIR_TRAILER =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_TRAILER;

        public static final String CONTENT_DIR_REVIEW =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEW;
    }


    private MovieContract() {
        throw new AssertionError("No instances for you!");
    }
}
