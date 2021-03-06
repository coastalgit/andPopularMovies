package com.bf.popularmovies.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.bf.popularmovies.utility.NetworkUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings({"ConstantConditions", "WeakerAccess"})
public class MainActivity extends AppCompatActivity implements MVP_TMDBMovies.IView, MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final Integer TMDB_QUERY_PAGECOUNT = 10;
    private static final int LOADER_ID = 0;
    private static final String EXTRA_ASGRID = "extra_asgrid";

    private MVP_TMDBMovies.IPresenter mPresenter;
    private MoviesAdapter mMovieAdapter;
    private boolean mFilterMoviesByPopularity = true;
    private Menu mMenuOptions;
    private Snackbar mSnackbar;

    // NOTE: abandoned attempt to allow an optional grid/linear layout on the fly (experiencing problems with image caching)
    private boolean mLayoutAsGrid = true;

    NetworkUtils mNetworkUtils;

    @BindView(R.id.layoutMain)
    RelativeLayout mLayoutMain;
    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerViewMovies;

    //region Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNetworkUtils = new NetworkUtils();
        attachTMDBPresenter();

        /* Persist layout style */
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_ASGRID))
                mLayoutAsGrid = savedInstanceState.getBoolean(EXTRA_ASGRID,true);
        }
        applyLayoutManager(mLayoutAsGrid);

        mMovieAdapter = new MoviesAdapter(this, this);
        mRecyclerViewMovies.setAdapter(mMovieAdapter);
        //mRecyclerViewMovies.setHasFixedSize(true);

        if (savedInstanceState == null) {
            performRefresh();
        }
        else{
            reloadAppropriateMovieList();
        }
    }

    private void reloadAppropriateMovieList(){
        if (((TMDBMoviesPresenterImpl)mPresenter).getViewAsFavourites()){
            reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieFavouritesList(),true);
        }
        else {
            reloadMovieAdapter(((TMDBMoviesPresenterImpl) mPresenter).getMovieList(), true);
        }
    }
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(TAG, "onRetainCustomNonConfigurationInstance: ");
        return mPresenter;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(EXTRA_ASGRID, mLayoutAsGrid);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
        /* Check if a solitary favourite has just been removed when navigating back from the Details activity */
        if (((TMDBMoviesPresenterImpl)mPresenter).getViewAsFavourites())
            loadFavouriteMovies();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

    //endregion Lifecycle

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
            case R.id.menulayout:
                applyLayoutManager(!mLayoutAsGrid);
                /* Perform rebuild of adapter here also, to address problem with title text not always being displayed */
                mMovieAdapter = new MoviesAdapter(this, this);
                mRecyclerViewMovies.setAdapter(mMovieAdapter);
                reloadAppropriateMovieList();
                return true;
            case R.id.menufavorites:
                loadFavouriteMovies();
                return true;
            case R.id.menulang:
                showLanguageDialog();
                return true;
            case R.id.menufilter:
                ((TMDBMoviesPresenterImpl)mPresenter).setViewAsFavourites(false);
                //mFilterMoviesByPopularity = !mFilterMoviesByPopularity;
                //item.setTitle(mFilterMoviesByPopularity ? R.string.popular : R.string.toprated);
                displayUpdate_ApplyFilterBy(!mFilterMoviesByPopularity);
                //clearScrollPosition();
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

    //region Misc private methods

    private void applyLayoutManager(boolean asGrid){
        mLayoutAsGrid = asGrid;
        if (asGrid){
            boolean isLandscapeOrientation = getResources().getBoolean(R.bool.islandscapeorient);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,isLandscapeOrientation ? 3:2);
            mRecyclerViewMovies.setLayoutManager(gridLayoutManager);
//            MenuItem item =  mMenuOptions.findItem(R.id.menulayout);
//            if (item != null)
//                item.setIcon(asGrid ? R.drawable.ic_view_stream_white_24dp : R.drawable.ic_view_module_white_24dp ); // reversed
        }
        else{
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewMovies.setLayoutManager(linearLayoutManager);
        }

    }

    private void performRefresh(){
        if (mNetworkUtils.isConnected(this)) {
            if (((TMDBMoviesPresenterImpl) mPresenter).getViewAsFavourites()) {
                loadFavouriteMovies();
            } else {
                snackBarShow(getString(R.string.loading), true);
                if (mFilterMoviesByPopularity)
                    mPresenter.getTMDBMoviesByPopularity(TMDBManager.getInstance().getLanguage(), TMDB_QUERY_PAGECOUNT);
                else
                    mPresenter.getTMDBMoviesByTopRated(TMDBManager.getInstance().getLanguage(), TMDB_QUERY_PAGECOUNT);
            }
        }
        else{
            String errorStr = getString(R.string.availablenot) + " (" + getString(R.string.connection) +")";
            snackBarFailShow(errorStr);
        }
    }

    private void loadFavouriteMovies(){
        Log.d(TAG, "loadFavouriteMovies: ");
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void attachTMDBPresenter(){
        mPresenter = (MVP_TMDBMovies.IPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new TMDBMoviesPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void reloadMovieAdapter(final ArrayList<TMDBMovie> movies, final boolean restorePreviousState){
        if (mNetworkUtils.isConnected(this)) {
            if (movies != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMovieAdapter.reloadAdapter(movies, !mLayoutAsGrid);
                    }
                });
            }
        }
        else{
            String errorStr = getString(R.string.availablenot) + " (" + getString(R.string.connection) +")";
            snackBarFailShow(errorStr);
        }
    }

    private void showDetailsActivity(TMDBMovie movieSelected){
        Intent detailIntent = new Intent(this, Details2Activity.class);
        detailIntent.putExtra(Details2Activity.KEY_MOVIE, movieSelected);
        startActivity(detailIntent);
    }

    //endregion Misc private methods

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
    public void onTMDBMoviesResponse_OK(){
        reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList(), false);
        snackBarDismiass();
    }

    @Override
    public void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        String errorStr = getString(R.string.error);
        if (code == Enums.TMDBErrorCode.CONNECTION_ERROR)
            errorStr = getString(R.string.availablenot) + " (" + getString(R.string.connection) +")";
        snackBarFailShow(errorStr);
    }

    @Override
    public void onClick(TMDBMovie movie) {
        Log.d(TAG, "onClick: Title:"+ movie.getTitle());
        if (mNetworkUtils.isConnected(this)) {
            snackBarDismiass();
            showDetailsActivity(movie);
        }
        else {
            String errorStr = getString(R.string.availablenot) + " (" + getString(R.string.connection) + ")";
            snackBarFailShow(errorStr);
        }
    }

    //endregion

    //region Dialogs and Snackbars
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

    @SuppressWarnings("SameParameterValue")
    @SuppressLint("ResourceAsColor")
    private void snackBarShow(String message, boolean asIndefinite){
        Log.d(TAG, "snackBarShow: ");
        mSnackbar = Snackbar.make(mLayoutMain,message, asIndefinite?Snackbar.LENGTH_INDEFINITE:Snackbar.LENGTH_SHORT);
        mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
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
        if (mSnackbar != null && mSnackbar.isShown())
            mSnackbar.dismiss();
    }

    //endregion Dialogs and Snackbars

    //region Cursor Loader

    @NonNull
    @SuppressWarnings("UnnecessaryLocalVariable")
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
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount()>0){

            ArrayList<TMDBMovie> movieList = new ArrayList<>();

            // column descriptors
            int movie_id = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
            int movie_vote_average = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            int movie_title = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
            int movie_original_title = data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINALTITLE);
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
                movie.setOriginalTitle(data.getString(movie_original_title));
                movie.setPosterPath(data.getString(movie_poster_path));
                movie.setBackdropPath(data.getString(movie_background_path));
                movie.setOverview(data.getString(movie_overview));
                movie.setReleaseDate(data.getString(movie_release_date));
                movie.setOriginalLanguage(data.getString(movie_original_lang));
                movieList.add(movie);
            }
            //clearScrollPosition();
            ((TMDBMoviesPresenterImpl)mPresenter).setViewAsFavourites(true);
            ((TMDBMoviesPresenterImpl)mPresenter).setMovieFavouritesList(movieList);
            reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieFavouritesList(), false);
        }
        else{
            Toast.makeText(this, R.string.nofavesavail, Toast.LENGTH_SHORT).show();
            // load default movie view
            if (mPresenter != null){
                ((TMDBMoviesPresenterImpl)mPresenter).setViewAsFavourites(false);
                if (((TMDBMoviesPresenterImpl)mPresenter).getMovieList() != null &&
                        (((TMDBMoviesPresenterImpl)mPresenter).getMovieList().size() > 0))
                    reloadMovieAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList(), false);
                else
                    performRefresh();
            }
            else
                performRefresh();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //
    }

    //endregion
}
