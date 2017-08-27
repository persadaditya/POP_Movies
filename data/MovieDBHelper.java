package com.app.phedev.popmovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.phedev.popmovie.pojo.Movie;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.COLUMN_DATE;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.COLUMN_MOVIEID;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.COLUMN_TITLE;

/**
 * Created by phedev in 2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public static final String LOGTAG = "MOVIES";

    SQLiteOpenHelper dbhandler;
    SQLiteDatabase db;

    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.FavoriteEntry.COLUMN_MOVIEID + " INTEGER, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_FAVORITE_TABLE2 = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME2 + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.FavoriteEntry.COLUMN_MOVIEID + " INTEGER, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_FAVORITE_TABLE3 = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME3 + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.FavoriteEntry.COLUMN_MOVIEID + " INTEGER, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE2);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE3);
    }

    private static final String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
            + COLUMN_DATE + " ADD COLUMN " + COLUMN_MOVIEID + " string;";

    private static final String DATABASE_ALTER_TEAM_2 = "ALTER TABLE "
            + _ID + " ADD COLUMN " + COLUMN_PLOT_SYNOPSIS + " string;";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(DATABASE_ALTER_TEAM_1);
        }
        if (oldVersion < 3) {
            db.execSQL(DATABASE_ALTER_TEAM_2);
        }

    }

    public void addFavorite(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteEntry.COLUMN_MOVIEID, movie.getId());
        values.put(COLUMN_TITLE, movie.getOriTitle());
        values.put(MovieContract.FavoriteEntry.COLUMN_USERRATING, movie.getVoteAvg());
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlot());

        db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteFavorite(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MovieContract.FavoriteEntry.TABLE_NAME, MovieContract.FavoriteEntry.COLUMN_MOVIEID+ "=" + id, null);
    }

    public List<Movie> getAllFavorite(){
        String[] columns = {
                _ID,
                MovieContract.FavoriteEntry.COLUMN_MOVIEID,
                COLUMN_TITLE,
                MovieContract.FavoriteEntry.COLUMN_USERRATING,
                MovieContract.FavoriteEntry.COLUMN_POSTER_PATH,
                MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS

        };
        String sortOrder =
                _ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_MOVIEID))));
                movie.setOriTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                movie.setVoteAvg(Double.parseDouble(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setPlot(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS)));

                favoriteList.add(movie);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoriteList;
    }
}
