package com.bf.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/*
 * @author frielb 
 * Created on 27/03/2018
 */

@SuppressWarnings("ConstantConditions")
public class MoviesContentProvider extends ContentProvider {

    private static final String TAG = MoviesContentProvider.class.getSimpleName();
    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIE_BY_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, CODE_MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", CODE_MOVIE_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE_BY_ID: {

                cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_ID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case CODE_MOVIES: {
                cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: [" + uri + "]");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        if (sUriMatcher.match(uri) != CODE_MOVIES) {
            throw new UnsupportedOperationException(
                    "Unsupported URI for insert: " + uri);
        }

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnVal;
        long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
        if (id > 0) {
            returnVal = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(returnVal, null);
        }
        else{
            throw new SQLException("Exception on insert to URI" + uri);
        }

        return returnVal;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        if (sUriMatcher.match(uri) != CODE_MOVIE_BY_ID) {
            throw new UnsupportedOperationException(
                    "Unsupported URI for deletion: " + uri);
        }

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeletedCount;

        String idStr = uri.getLastPathSegment();
        //String where = MoviesContract.MovieEntry.COLUMN_ID + " = ?";
        String where = MoviesContract.MovieEntry.COLUMN_ID + " = " + idStr;
        Log.d(TAG, "delete: ["+where+"]");
        rowsDeletedCount = db.delete(
                MoviesContract.MovieEntry.TABLE_NAME,
                where,
                null);

        if (rowsDeletedCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeletedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
