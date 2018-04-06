package com.bf.popularmovies.db;

/*
 * @author frielb 
 * Created on 29/03/2018
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.bf.popularmovies.R;
import com.bf.popularmovies.model.TMDBMovie;

public class MovieDbTransactionManager {

    private static final String TAG = MovieDbTransactionManager.class.getSimpleName();

    private final Context mContext;
    private final onDbTransactionHandler mDbTransactionHandler;


    public interface onDbTransactionHandler{
        void onFaveMovieAdded_Ok();
        void onFaveMovieAdded_Fail(String errorMsg);
        void onFaveMovieRemoved();
    }

    public MovieDbTransactionManager(Context context, onDbTransactionHandler dbTranscationListener) {
        this.mContext = context;
        this.mDbTransactionHandler = dbTranscationListener;
    }

    public void doAddMovieToFavorites(TMDBMovie movie){

        // db insert
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINALTITLE, movie.getOriginalTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANG, movie.getOriginalLanguage());

        Uri uri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
        if (uri != null)
            mDbTransactionHandler.onFaveMovieAdded_Ok();
        else
            mDbTransactionHandler.onFaveMovieAdded_Fail(mContext.getString(R.string.erroroccurred));
    }

    public void doRemoveMovieFromFavorites(int movieId){
        // db delete
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movieId)).build();

        int rowsDeleted = mContext.getContentResolver().delete(uri,null,null);
        Log.d(TAG, "doRemoveMovieFromFavorites: rows="+String.valueOf(rowsDeleted));
        mDbTransactionHandler.onFaveMovieRemoved();
    }

    public boolean isMovieAFavourite(int movieId){
        // db query
        boolean match = false;

        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        String selection = MoviesContract.MovieEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(movieId)};
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    uri,
                    null,
                    selection,
                    selectionArgs,
                    null
            );
        }
        finally {
            if (cursor != null) {
                Log.d(TAG, "isMovieAFavourite: count=" + String.valueOf(cursor.getCount()));
                match = cursor.getCount() > 0;
                cursor.close();
             }
        }
        return match;
    }

}
