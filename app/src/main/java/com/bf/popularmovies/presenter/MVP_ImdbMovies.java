package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;

public interface MVP_ImdbMovies {

    interface IView{
        void logMessageToView(String msg);
        void onIMDBResponse_OK();
        void onIMDBResponse_Error(String errorMsg);
        void onErrorConnection(String errorMsg);
    }

    interface IPresenter{
        void attachView(IView view);
        void detachView();
        //void doDestroyPresenter();

        void getImdbMoviesByPopularity(Enums.LanguageLocale langLocale, int pageCount);
        void getImdbMoviesByTopRated(Enums.LanguageLocale langLocale, int pageCount);
    }
}
