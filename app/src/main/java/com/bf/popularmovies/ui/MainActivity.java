package com.bf.popularmovies.ui;

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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MVP_TMDBMovies.IView, MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MVP_TMDBMovies.IPresenter mPresenter;
    private MoviesAdapter mMovieAdapter;
    private boolean mLayoutAsGrid = true;
    private boolean mFilterMoviesByPopularity = true;
    private Enums.LanguageLocale mLanguage = Enums.LanguageLocale.ENGLISH;

    @BindView(R.id.buttonTestAPI) Button mBtnTestAPI;
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
    }

    private void applyLayoutManager(boolean asGrid){
        if (asGrid){
            GridLayoutManager layoutManager = new GridLayoutManager(this,2);
            mRecyclerViewMovies.setLayoutManager(layoutManager);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menulayout:
                mLayoutAsGrid = !mLayoutAsGrid;
                item.setIcon(mLayoutAsGrid ? R.drawable.ic_view_module_white_24dp : R.drawable.ic_view_stream_white_24dp);
                menuApplyLayout();
                return true;
            case R.id.menulang:
                menuDisplayLanguageSelection();
                return true;
            case R.id.menufilter:
                mFilterMoviesByPopularity = !mFilterMoviesByPopularity;
                item.setTitle(mFilterMoviesByPopularity ? R.string.popular : R.string.toprated);
                menuApplyFilterBy();
                return true;
            case R.id.menurefresh:
                menuPerformRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void menuApplyLayout(){
        //mRecyclerViewMovies.
        mMovieAdapter.setAsBackDropImage(!mLayoutAsGrid);
        applyLayoutManager(mLayoutAsGrid);
        mMovieAdapter.reloadAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList(), !mLayoutAsGrid);
    }

    private void menuDisplayLanguageSelection(){
        Toast.makeText(this, "Lang?", Toast.LENGTH_SHORT).show();
    }

    private void menuApplyFilterBy(){
        if (mFilterMoviesByPopularity)
            mPresenter.getTMDBMoviesByPopularity(mLanguage, 1);
        else
            mPresenter.getTMDBMoviesByTopRated(mLanguage, 1);
    }

    private void menuPerformRefresh(){
        menuApplyLayout();
        menuApplyFilterBy();
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
        menuPerformRefresh();
    }

    private void refreshView(){
        //mmo
    }

    //region Butterknife listeners
    @OnClick(R.id.buttonTestAPI)
    public void mBtnTestAPI_OnClick(Button btn) {
        testAPI();
    }
    //endregion

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
    public void onTMDBMoviesResponse_OK(final ArrayList<TMDBMovie> movies){
        // TODO: 21/02/2018 Refresh RecyclerView / adapter
        if (movies != null) {
//            for (TMDBMovie movie : movies) {
//                Log.d(TAG, "Title: " + movie.getTitle());
//                // TODO: 21/02/2018 Helper function for most appropriate image size from
//            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMovieAdapter.reloadAdapter(movies, !mLayoutAsGrid);
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
    }


    //endregion
}
