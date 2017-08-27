package com.app.phedev.popmovie.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_DIR_FAV;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_DIR_POP;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_DIR_RATED;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_URI;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_URI_POP;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.CONTENT_URI_RATED;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.TABLE_NAME;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.TABLE_NAME2;
import static com.app.phedev.popmovie.data.MovieContract.FavoriteEntry.TABLE_NAME3;

/**
 * Created by phedev in 2017.
 */

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_POPULAR = 101;
    public static final int CODE_MOVIE_RATED = 102;
    public static final int CODE_MOVIE_ID_POP = 201;
    public static final int CODE_MOVIE_ID_RAT = 202;

    @Override
    public boolean onCreate() {

        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE );
        matcher.addURI(authority, MovieContract.PATH_MOVIE2, CODE_MOVIE_POPULAR );
        matcher.addURI(authority, MovieContract.PATH_MOVIE3, CODE_MOVIE_RATED);
        matcher.addURI(authority, MovieContract.PATH_MOVIE2 +"/#", CODE_MOVIE_ID_POP);
        matcher.addURI(authority, MovieContract.PATH_MOVIE3 + "/#", CODE_MOVIE_ID_RAT);

       return matcher;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                int rowsInserted = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insert(MovieContract.FavoriteEntry.TABLE_NAME,
                                null, value
                        );
                        rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                }
                catch (Exception e) {
                    rowsInserted = 0;
                }finally {
                    db.endTransaction();
                }
                return rowsInserted;

            case CODE_MOVIE_POPULAR:
                db.beginTransaction();
                int rowsInsertedPop = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(TABLE_NAME2,
                                null,
                                value);
                        rowsInsertedPop++;
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    rowsInsertedPop = 0;
                }finally {
                    db.endTransaction();
                }
                return rowsInsertedPop;

            case CODE_MOVIE_RATED:
                db.beginTransaction();
                int rowsInsertedRat = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(TABLE_NAME3,
                                null,
                                value);
                        rowsInsertedRat++;
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    rowsInsertedRat = 0;
                }finally {
                    db.endTransaction();
                }
                return rowsInsertedRat;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MOVIE:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_POPULAR:
                cursor = db.query(TABLE_NAME2,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_RATED:
                cursor = db.query(TABLE_NAME3,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MOVIE:{
                return CONTENT_DIR_FAV;
            }
            case CODE_MOVIE_POPULAR:{
                return CONTENT_DIR_POP;
            }
            case CODE_MOVIE_RATED:{
                return CONTENT_DIR_RATED;
            }

        }
        throw new RuntimeException("We are not implementing getType in POP Movie.");
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case CODE_MOVIE:
                long id = db.insert(TABLE_NAME, null, contentValues);
                if (id >0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                }else {
                    throw new android.database.SQLException("failed to insert row into " + uri);
                }break;
            case CODE_MOVIE_POPULAR:
                long idpop = db.insert(TABLE_NAME2, null, contentValues);
                if (idpop >0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI_POP, idpop);
                }else {
                    throw new android.database.SQLException("failed to insert row into " + uri);
                }break;
            case CODE_MOVIE_RATED:
                long idrat = db.insert(TABLE_NAME3, null, contentValues);
                if (idrat >0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI_RATED, idrat);
                }else {
                    throw new android.database.SQLException("failed to insert row into " + uri);
                }break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case CODE_MOVIE_POPULAR:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        TABLE_NAME2,
                        selection,
                        selectionArgs);

                break;
            case CODE_MOVIE_RATED:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        TABLE_NAME3,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:
                return mOpenHelper.getWritableDatabase().update(
                        TABLE_NAME,
                        contentValues,
                        s,
                        strings
                );

            case CODE_MOVIE_POPULAR:
                return mOpenHelper.getWritableDatabase().update(
                        TABLE_NAME2,
                        contentValues,
                        s,
                        strings
                );

            case CODE_MOVIE_RATED:
                return mOpenHelper.getWritableDatabase().update(
                        TABLE_NAME3,
                        contentValues,
                        s,
                        strings
                );
        }

        throw new RuntimeException("We are not implementing update in POP Movie");
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
