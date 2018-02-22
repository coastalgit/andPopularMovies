package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBMovie;

import java.util.ArrayList;

public interface MVP_TMDBMovies {

    interface IView{
        void logMessageToView(String msg);
        //void onTMDBMoviesResponse_OK(ArrayList<TMDBMovie> movies);
        void onTMDBMoviesResponse_OK();
        void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
    }

    interface IPresenter{
        void attachView(IView view);
        void detachView();
        //void doDestroyPresenter();

        void getTMDBMoviesByPopularity(Enums.LanguageLocale langLocale, int pageCount);
        void getTMDBMoviesByTopRated(Enums.LanguageLocale langLocale, int pageCount);
    }
}
