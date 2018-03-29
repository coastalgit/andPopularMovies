package com.bf.popularmovies.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.MoviesAdapter;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.db.MoviesContract;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.presenter.TMDBMoviesPresenterImpl;
import com.bf.popularmovies.presenter.MVP_TMDBMovies;
import com.bf.popularmovies.utility.HelperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings({"ConstantConditions", "WeakerAccess"})
public class MainActivity extends AppCompatActivity implements MVP_TMDBMovies.IView, MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final Integer TMDB_QUERY_PAGECOUNT = 10;
    private static final int LOADER_ID = 0;

    private MVP_TMDBMovies.IPresenter mPresenter;
    private MoviesAdapter mMovieAdapter;
    private boolean mFilterMoviesByPopularity = true;
    private Menu mMenuOptions;
    private Snackbar mSnackbar;

    // NOTE: abandoned attempt to allow an optional grid/linear layout on the fly (experiencing problems with image caching)
    private final boolean mLayoutAsGrid = true;
    private boolean mViewAsFavourites = false;

    @BindView(R.id.layoutMain)
    RelativeLayout mLayoutMain;
    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerViewMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        attachTMDBPresenter();
        applyLayoutManager(mLayoutAsGrid);
        mRecyclerViewMovies.setHasFixedSize(true);

        mMovieAdapter = new MoviesAdapter(this, this);
        mRecyclerViewMovies.setAdapter(mMovieAdapter);

        if (savedInstanceState == null)
            performRefresh();
        else{
            if (mViewAsFavourites)
                reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieFavouritesList());
            else
                reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList());
        }
    }

    private void applyLayoutManager(boolean asGrid){
        if (asGrid){
            boolean isLandscapeOrientation = getResources().getBoolean(R.bool.islandscapeorient);
            GridLayoutManager layoutManager = new GridLayoutManager(this,isLandscapeOrientation ? 3:2);
            mRecyclerViewMovies.setLayoutManager(layoutManager);
//            MenuItem item =  mMenuOptions.findItem(R.id.menulayout);
//            if (item != null)
//                item.setIcon(asGrid ? R.drawable.ic_view_stream_white_24dp : R.drawable.ic_view_module_white_24dp ); // reversed
        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewMovies.setLayoutManager(layoutManager);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    //region Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mMenuOptions = menu;
        displayUpdate_ApplyFilterBy(mFilterMoviesByPopularity);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
//            case R.id.menulayout:
//                return true;
            case R.id.menufavorites:
                loadFavouriteMovies();
                return true;
            case R.id.menulang:
                showLanguageDialog();
                return true;
            case R.id.menufilter:
                mViewAsFavourites = false;
                //mFilterMoviesByPopularity = !mFilterMoviesByPopularity;
                //item.setTitle(mFilterMoviesByPopularity ? R.string.popular : R.string.toprated);
                displayUpdate_ApplyFilterBy(!mFilterMoviesByPopularity);
                performRefresh();
                return true;
            case R.id.menurefresh:
                performRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayUpdate_ApplyLanguageChange(Enums.LanguageLocale lang){
        TMDBManager.getInstance().setLanguage(lang);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            config.locale = new Locale.Builder().setLanguageTag(TMDBManager.getInstance().getLanguage().toString()).build();
            getResources().updateConfiguration(config, resources.getDisplayMetrics());
        }

        getSupportActionBar().setTitle(getString(R.string.app_name));

        if (mMenuOptions != null) {
            MenuItem item = mMenuOptions.findItem(R.id.menulang);
            item.setTitle(String.format("%s (%s)", getString(R.string.language), HelperUtils.languageLocaleToDisplayString(lang)));
        }
    }

    private void displayUpdate_ApplyFilterBy(boolean byPopularity){
        mFilterMoviesByPopularity = byPopularity;
        if (mMenuOptions != null) {
            MenuItem item = mMenuOptions.findItem(R.id.menufilter);
            if (item != null)
                item.setTitle(byPopularity ? R.string.toprated : R.string.popular); // reversed
        }
    }

    //endregion

    private void performRefresh(){
        if (mViewAsFavourites){
            loadFavouriteMovies();
        }
        else {
            snackBarShow(getString(R.string.loading), true);
            if (mFilterMoviesByPopularity)
                mPresenter.getTMDBMoviesByPopularity(TMDBManager.getInstance().getLanguage(), TMDB_QUERY_PAGECOUNT);
            else
                mPresenter.getTMDBMoviesByTopRated(TMDBManager.getInstance().getLanguage(), TMDB_QUERY_PAGECOUNT);
        }
    }

    private void loadFavouriteMovies(){
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void attachTMDBPresenter(){
        mPresenter = (MVP_TMDBMovies.IPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new TMDBMoviesPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void reloadMovieAdapter(final ArrayList<TMDBMovie> movies){
        if (movies != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMovieAdapter.reloadAdapter(movies, !mLayoutAsGrid);
                }
            });
        }
    }

    //region Implemented methods
    @Override
    public void logMessageToView(final String msg) {
        Log.d(TAG, "logMessageToView: ["+msg+"]");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //public void onTMDBMoviesResponse_OK(final ArrayList<TMDBMovie> movies){
    public void onTMDBMoviesResponse_OK(){
        reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList());
        snackBarDismiass();
    }

    @Override
    public void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        // TODO: 21/02/2018 Display better error message
        snackBarFailShow(getString(R.string.error));
    }

    @Override
    public void onClick(TMDBMovie movie) {
        Log.d(TAG, "onClick: Title:"+ movie.getTitle());
        showDetailsActivity(movie);
    }

    //endregion

    private void showDetailsActivity(TMDBMovie movieSelected){
        //Intent detailIntent = new Intent(this, DetailsActivity.class);
        Intent detailIntent = new Intent(this, Details2Activity.class);
        detailIntent.putExtra(Details2Activity.KEY_MOVIE, movieSelected);
        //detailIntent.putExtra(DetailsActivity.KEY_LANG, mLanguage);
        startActivity(detailIntent);
    }

    private void showLanguageDialog(){
        //final String[] langs={String.valueOf(Enums.LanguageLocale.ENGLISH), String.valueOf(Enums.LanguageLocale.PORTUGUESE.toString())};
        final String[] langs={HelperUtils.languageLocaleToDisplayString(Enums.LanguageLocale.ENGLISH), HelperUtils.languageLocaleToDisplayString(Enums.LanguageLocale.PORTUGUESE)};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.languageselection))
                .setSingleChoiceItems(langs, TMDBManager.getInstance().getLanguage().ordinal(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Enums.LanguageLocale selectedLang = Enums.LanguageLocale.values()[which];
                        if (TMDBManager.getInstance().getLanguage() == selectedLang){
                            Log.d(TAG, "onClick: Already in "+selectedLang.toString());
                        }
                        else{
                            displayUpdate_ApplyLanguageChange(selectedLang);
                            performRefresh();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    @SuppressLint("ResourceAsColor")
    private void snackBarShow(String message, boolean asIndefinite){
        Log.d(TAG, "snackBarShow: ");
        mSnackbar = Snackbar.make(mLayoutMain,message, asIndefinite?Snackbar.LENGTH_INDEFINITE:Snackbar.LENGTH_SHORT);
        //mSnackbar.getView().setBackgroundColor(R.color.colorAccent);
        mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mSnackbar.getView().setBackgroundTintMode(null);
//        }
        mSnackbar.setActionTextColor(Color.WHITE);
        mSnackbar.show();
    }

    @SuppressLint("ResourceAsColor")
    private void snackBarFailShow(String message){
        Log.d(TAG, "snackBarFailShow: ");
        mSnackbar = Snackbar.make(mLayoutMain,message, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        mSnackbar.setActionTextColor(Color.WHITE);
        mSnackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRefresh();
            }
        });
        mSnackbar.show();
    }

    private void snackBarDismiass(){
        if (mSnackbar.isShown())
            mSnackbar.dismiss();
    }

    //region Cursor Loader

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(
                this,
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                BaseColumns._ID
                );
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount()>0){

            ArrayList<TMDBMovie> movieList = new ArrayList<>();

            // column descriptors
            int movie_id = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
            int movie_vote_average = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            int movie_title = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
            int movie_poster_path = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
            int movie_background_path = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);
            int movie_overview = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
            int movie_release_date = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
            int movie_original_lang = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANG);

            for (int i=0 ; i < data.getCount() ; i++){
                data.moveToPosition(i);
                TMDBMovie movie = new TMDBMovie();
                movie.setId(data.getInt(movie_id));
                movie.setVoteAverage(data.getDouble(movie_vote_average));
                movie.setTitle(data.getString(movie_title));
                movie.setPosterPath(data.getString(movie_poster_path));
                movie.setBackdropPath(data.getString(movie_background_path));
                movie.setOverview(data.getString(movie_overview));
                movie.setReleaseDate(data.getString(movie_release_date));
                movie.setOriginalLanguage(data.getString(movie_original_lang));
                movieList.add(movie);
            }
            mViewAsFavourites = true;
            ((TMDBMoviesPresenterImpl)mPresenter).setMovieFavouritesList(movieList);
            reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieFavouritesList());
        }
        else{
            mViewAsFavourites = false;
            Toast.makeText(this, R.string.nofavesavail, Toast.LENGTH_SHORT).show();
            // load default movie view
            performRefresh();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //
    }

    //endregion
}
