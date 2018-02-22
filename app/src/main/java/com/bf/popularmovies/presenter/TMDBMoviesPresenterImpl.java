package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import android.util.Log;

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBMovieResults;
import com.bf.popularmovies.task.ITMDBMoviesResponseHandler;
import com.bf.popularmovies.task.ITMDBSysConfigResponseHandler;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBSysConfig;
import com.bf.popularmovies.task.UpdateTMDBMoviesTask;
import com.bf.popularmovies.task.UpdateTMDBSysConfigTask;
import com.bf.popularmovies.utility.TMDBUtils;

import java.net.URL;
import java.util.ArrayList;

public class TMDBMoviesPresenterImpl implements MVP_TMDBMovies.IPresenter{

    private static final String TAG = TMDBMoviesPresenterImpl.class.getSimpleName();

    private MVP_TMDBMovies.IView mView;
    private String mApiKey = null;

    private ArrayList<TMDBMovie> mMovieList;

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
            if (urlSysConfig != null) {
                UpdateTMDBSysConfigTask updateTask = new UpdateTMDBSysConfigTask(urlSysConfig, new ITMDBSysConfigResponseHandler() {
                    @Override
                    public void onTMDBSysConfigResponse_OK(TMDBSysConfig tmdbSysConfig) {
                        TMDBManager.getInstance().setTMDBSysConfig(tmdbSysConfig);
                        getTMDBMovies(queryBy, lang, pages);
                    }

                    @Override
                    public void onTMDBSysConfigResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                        if (mView != null)
                            mView.logMessageToView(errorMsg);
                    }
                });
                updateTask.doUpdate();
            }
            else{
                if (mView!=null)
                    mView.logMessageToView("Invalid config url");
            }
        }
        else{
            URL urlMovies = TMDBUtils.buildAPIUrl_Movies(this.mApiKey, queryBy, lang, pages);
            //if (mView!=null)
            //    mView.logMessageToView("URL:"+"["+tmdbUrl.toString()+"]");

            if (urlMovies != null) {
                UpdateTMDBMoviesTask updateTask = new UpdateTMDBMoviesTask(urlMovies, new ITMDBMoviesResponseHandler() {
                    @Override
                    public void onTMDBMoviesResponse_OK(TMDBMovieResults tmdbMovies) {
                        mMovieList = new ArrayList<TMDBMovie>(tmdbMovies.getResults());
                        //for (TMDBMovie movie: tmdbMovies.getResults()) {
//                        for (TMDBMovie movie: mMovieList) {
//                            Log.d(TAG, "Title: "+movie.getTitle());
//                            // TODO: 21/02/2018 Helper function for most appropriate image size from
//                            //URL poster_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl());
//                            URL poster_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getPosterPath(),"w185");
//                            //Log.d(TAG, "Poster: "+poster_path.toString());
//                        }
                        if (mView != null)
                            //mView.onTMDBMoviesResponse_OK(mMovieList);
                            mView.onTMDBMoviesResponse_OK();
                    }

                    @Override
                    public void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                        if (mView != null)
                            mView.logMessageToView(errorMsg);
                    }
                });
                updateTask.doUpdate();
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

}
