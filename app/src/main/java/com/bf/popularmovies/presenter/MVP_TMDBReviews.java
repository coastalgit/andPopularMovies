package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;

public interface MVP_TMDBReviews {

    @SuppressWarnings("unused")
    interface IView{
        void logMessageToView(String msg);
        void onTMDBReviewsResponse_OK();
        void onTMDBReviewsResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
    }

    @SuppressWarnings("SameParameterValue")
    interface IPresenter{
        void attachView(IView view);
        void detachView();
        //void doDestroyPresenter();

        void getTMDBReviews(Enums.LanguageLocale langLocale, int movieId);
    }
}
