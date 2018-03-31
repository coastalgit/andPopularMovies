package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBMovieResults;
import com.bf.popularmovies.task.ITMDBGenresResponseHandler;
import com.bf.popularmovies.task.ITMDBMoviesResponseHandler;
import com.bf.popularmovies.task.ITMDBSysConfigResponseHandler;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBSysConfig;
import com.bf.popularmovies.task.UpdateTMDBGenresTask;
import com.bf.popularmovies.task.UpdateTMDBMoviesTask;
import com.bf.popularmovies.task.UpdateTMDBSysConfigTask;
import com.bf.popularmovies.utility.TMDBUtils;

import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("Convert2Diamond")
public class TMDBMoviesPresenterImpl implements MVP_TMDBMovies.IPresenter{

    //private static final String TAG = TMDBMoviesPresenterImpl.class.getSimpleName();

    private MVP_TMDBMovies.IView mView;
    private String mApiKey = null;

    private ArrayList<TMDBMovie> mMovieList;
    // Also storing Favourites here for persistence
    private ArrayList<TMDBMovie> mMovieFavouritesList;
    private int mCurrentPositionIndex = -1;
    private boolean mViewAsFavourites = false;

    public TMDBMoviesPresenterImpl(String apiKey, MVP_TMDBMovies.IView viewMovies) {
        this.mApiKey = apiKey;
        this.attachView(viewMovies);
    }


    @Override
    public void attachView(MVP_TMDBMovies.IView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getTMDBMoviesByPopularity(Enums.LanguageLocale langLocale, int pageCount) {
        getTMDBMovies(Enums.TMDBQueryBy.POPULAR, langLocale, pageCount);
    }

    @Override
    public void getTMDBMoviesByTopRated(Enums.LanguageLocale langLocale, int pageCount) {
        getTMDBMovies(Enums.TMDBQueryBy.TOPRATED, langLocale, pageCount);
    }

    private void getTMDBMovies(final Enums.TMDBQueryBy queryBy, final Enums.LanguageLocale lang, final int pages){
        if (!TMDBManager.getInstance().hasRecentSysConfig()) {
            final URL urlSysConfig = TMDBUtils.buildAPIUrl_SysConfig(mApiKey);

            //region SYSTEM CONFIG
            if (urlSysConfig != null) {
                UpdateTMDBSysConfigTask updateTaskSysConfig = new UpdateTMDBSysConfigTask(urlSysConfig, new ITMDBSysConfigResponseHandler() {
                    @Override
                    public void onTMDBSysConfigResponse_OK(TMDBSysConfig tmdbSysConfig) {
                        TMDBManager.getInstance().setTMDBSysConfig(tmdbSysConfig);

                        //region GENRES
                        // Observation: Even without genres, we should let app proceed to load movies
                        if (TMDBManager.getInstance().getTMDBGenres() == null){
                            final URL urlGenres = TMDBUtils.buildAPIUrl_Genres(mApiKey);
                            if (urlGenres != null){
                                UpdateTMDBGenresTask updateTaskGenres = new UpdateTMDBGenresTask(urlGenres, new ITMDBGenresResponseHandler() {
                                    @Override
                                    public void onTMDBGenresResponse_OK(TMDBGenres tmdbGenres) {
                                        TMDBManager.getInstance().setTMDBGenres(tmdbGenres);
                                        getTMDBMovies(queryBy, lang, pages);
                                    }

                                    @Override
                                    public void onTMDBGenresResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                                        if (mView != null)
                                            mView.logMessageToView(errorMsg);
                                        getTMDBMovies(queryBy, lang, pages);
                                    }
                                });
                                updateTaskGenres.doUpdate();
                            }
                            else{
                                // Observation: Even without genres, we should let app proceed
                                getTMDBMovies(queryBy, lang, pages);
                            }
                        }
                        else{
                            //we already have genres
                            getTMDBMovies(queryBy, lang, pages);
                        }
                        //endregion GENRES
                    }

                    @Override
                    public void onTMDBSysConfigResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                        if (mView != null)
                            mView.logMessageToView(errorMsg);
                    }
                });
                updateTaskSysConfig.doUpdate();
            }
            else{
                if (mView!=null)
                    mView.logMessageToView("Invalid config url");
            }
            //endregion SYSTEM CONFIG
        }
        else{
            URL urlMovies = TMDBUtils.buildAPIUrl_Movies(this.mApiKey, queryBy, lang, pages);

            if (urlMovies != null) {
                UpdateTMDBMoviesTask updateTaskMovies = new UpdateTMDBMoviesTask(urlMovies, new ITMDBMoviesResponseHandler() {
                    @Override
                    public void onTMDBMoviesResponse_OK(TMDBMovieResults tmdbMovies) {
                        mMovieList = new ArrayList<TMDBMovie>(tmdbMovies.getResults());
                        if (mView != null)
                            mView.onTMDBMoviesResponse_OK();
                    }

                    @Override
                    public void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                        if (mView != null)
                            mView.logMessageToView(errorMsg);
                    }
                });
                updateTaskMovies.doUpdate();
            }
            else{
                if (mView!=null)
                    mView.logMessageToView("Invalid movies url");
            }
        }

    }

    public ArrayList<TMDBMovie> getMovieList() {
        return mMovieList;
    }

    public ArrayList<TMDBMovie> getMovieFavouritesList() {
        return mMovieFavouritesList;
    }

    public void setMovieFavouritesList(ArrayList<TMDBMovie> mMovieFavouritesList) {
        this.mMovieFavouritesList = mMovieFavouritesList;
    }

    public boolean getViewAsFavourites() {
        return mViewAsFavourites;
    }

    public void setViewAsFavourites(boolean mViewAsFavourites) {
        this.mViewAsFavourites = mViewAsFavourites;
    }

    public int getCurrentPositionIndex() {
        return mCurrentPositionIndex;
    }

    public void setCurrentPositionIndex(int mCurrentPositionIndex) {
        this.mCurrentPositionIndex = mCurrentPositionIndex;
    }

}
