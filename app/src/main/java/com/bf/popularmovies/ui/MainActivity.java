package com.bf.popularmovies.ui;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.MoviesAdapter;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.presenter.TMDBMoviesPresenterImpl;
import com.bf.popularmovies.presenter.MVP_TMDBMovies;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MVP_TMDBMovies.IView, MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MVP_TMDBMovies.IPresenter mPresenter;
    private MoviesAdapter mMovieAdapter;
    private boolean mFilterMoviesByPopularity = true;
    private Enums.LanguageLocale mLanguage = Enums.LanguageLocale.ENGLISH;
    private Menu mMenuOptions;

    // NOTE: abandoned attempt to allow an optional grid/linear layout on the fly (experiencing problems with image caching)
    private boolean mLayoutAsGrid = true;

    @BindView(R.id.recyclerview_movies) RecyclerView mRecyclerViewMovies;

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

        performRefresh();
    }

    private void applyLayoutManager(boolean asGrid){
        if (asGrid){
            GridLayoutManager layoutManager = new GridLayoutManager(this,2);
            mRecyclerViewMovies.setLayoutManager(layoutManager);
//            MenuItem item =  mMenuOptions.findItem(R.id.menulayout);
//            if (item != null)
//                item.setIcon(asGrid ? R.drawable.ic_view_stream_white_24dp : R.drawable.ic_view_module_white_24dp ); // reversed
        }
        else{
            LinearLayoutManager layoutManager =
            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewMovies.setLayoutManager(layoutManager);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
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
//                menuApplyLayout();
//                mLayoutAsGrid = !mLayoutAsGrid;
//                //item.setIcon(mLayoutAsGrid ? R.drawable.ic_view_module_white_24dp : R.drawable.ic_view_stream_white_24dp);
//                performRefresh();
//                return true;
            case R.id.menulang:
                showLanguageDialog();
                return true;
            case R.id.menufilter:
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

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        Log.d(TAG, "onPrepareOptionsMenu: ");
//
//        MenuItem item =  menu.findItem(R.id.menulayout);
//        item.setIcon(mLayoutAsGrid ? R.drawable.ic_view_module_white_24dp : R.drawable.ic_view_stream_white_24dp);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

//    private void menuApplyLayout(){
//        //mRecyclerViewMovies.
//        //mMovieAdapter.setAsBackDropImage(!mLayoutAsGrid);
//
//        applyLayoutManager(mLayoutAsGrid);
//        //mMovieAdapter.reloadAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList(), !mLayoutAsGrid);
//        //invalidateOptionsMenu();
//
//    }

    private void displayUpdate_ApplyLanguageChange(Enums.LanguageLocale lang){
        //Toast.makeText(this, "Lang?", Toast.LENGTH_SHORT).show();
        mLanguage = lang;

        if (mMenuOptions != null) {
            MenuItem item = mMenuOptions.findItem(R.id.menulang);
            item.setTitle(String.format("%s (%s)", getString(R.string.language), lang.toString()));
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


    private void performRefresh(){
        //menuApplyLayout();
        //displayUpdate_ApplyFilterBy(mFilterMoviesByPopularity);
        if (mFilterMoviesByPopularity)
            mPresenter.getTMDBMoviesByPopularity(mLanguage, 1);
        else
            mPresenter.getTMDBMoviesByTopRated(mLanguage, 1);

    }
    //endregion

    private void attachTMDBPresenter(){
        mPresenter = (MVP_TMDBMovies.IPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new TMDBMoviesPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void testAPI(){
        //mPresenter.getTMDBMoviesByPopularity(Enums.LanguageLocale.ENGLISH, 1);
        performRefresh();
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
        // TODO: 21/02/2018 Refresh RecyclerView / adapter
        if (((TMDBMoviesPresenterImpl)mPresenter).getMovieList() != null) {
//            for (TMDBMovie movie : movies) {
//                Log.d(TAG, "Title: " + movie.getTitle());
//                // TODO: 21/02/2018 Helper function for most appropriate image size from
//            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mMovieAdapter.reloadAdapter(movies, !mLayoutAsGrid);
                    mMovieAdapter.reloadAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList(), !mLayoutAsGrid);
                }
            });

        }
    }

    @Override
    public void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        // TODO: 21/02/2018 Display error
    }

    @Override
    public void onClick(TMDBMovie movie) {
        Log.d(TAG, "onClick: Title:"+ movie.getTitle());
        showDetailsActivity(movie);
    }

    //endregion

    private void showDetailsActivity(TMDBMovie movieSelected){
        Intent detailIntent = new Intent(this, DetailsActivity.class);
        detailIntent.putExtra(DetailsActivity.KEY_MOVIE, movieSelected);
        detailIntent.putExtra(DetailsActivity.KEY_LANG, mLanguage);
        startActivity(detailIntent);
    }

    private void showLanguageDialog(){
        final String[] langs={String.valueOf(Enums.LanguageLocale.ENGLISH), String.valueOf(Enums.LanguageLocale.PORTUGUESE.toString())};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.languageselection))
                .setSingleChoiceItems(langs, mLanguage.ordinal(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Enums.LanguageLocale selectedLang = Enums.LanguageLocale.values()[which];
                        if (mLanguage == selectedLang){
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
}
