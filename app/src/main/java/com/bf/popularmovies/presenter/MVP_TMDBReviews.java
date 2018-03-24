package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;

public interface MVP_TMDBVideos {

    @SuppressWarnings("unused")
    interface IView{
        void logMessageToView(String msg);
        void onTMDBVideosResponse_OK();
        void onTMDBVideosResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
    }

    @SuppressWarnings("SameParameterValue")
    interface IPresenter{
        void attachView(IView view);
        void detachView();
        //void doDestroyPresenter();

        void getTMDBVideos(int movieId);
    }
}
