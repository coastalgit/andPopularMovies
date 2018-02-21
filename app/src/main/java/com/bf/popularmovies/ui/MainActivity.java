package com.bf.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.MoviesAdapter;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.presenter.TMDBMoviesPresenterImpl;
import com.bf.popularmovies.presenter.MVP_TMDBMovies;
import com.bf.popularmovies.utility.TMDBUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MVP_TMDBMovies.IView, MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MVP_TMDBMovies.IPresenter mPresenter;
    private MoviesAdapter mMovieAdapter;

    @BindView(R.id.buttonTestAPI) Button mBtnTestAPI;
    @BindView(R.id.recyclerview_movies) RecyclerView mRecyclerViewMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        attachTMDBPresenter();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mMovieAdapter = new MoviesAdapter(this, this);
        mRecyclerViewMovies.setAdapter(mMovieAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    private void attachTMDBPresenter(){
        mPresenter = (MVP_TMDBMovies.IPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new TMDBMoviesPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void testAPI(){
        mPresenter.getTMDBMoviesByPopularity(Enums.LanguageLocale.ENGLISH, 1);
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
            for (TMDBMovie movie : movies) {
                Log.d(TAG, "Title: " + movie.getTitle());
                // TODO: 21/02/2018 Helper function for most appropriate image size from
                //URL poster_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl());
                //URL poster_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getPosterPath(), "w185");
                //Log.d(TAG, "Poster: "+poster_path.toString());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMovieAdapter.reloadAdapter(movies);
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
