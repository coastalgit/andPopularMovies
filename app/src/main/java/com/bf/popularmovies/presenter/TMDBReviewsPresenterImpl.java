package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 21/03/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBReview;
import com.bf.popularmovies.model.TMDBReviewResults;
import com.bf.popularmovies.task.ITMDBReviewsResponseHandler;
import com.bf.popularmovies.task.UpdateTMDBReviewsTask;
import com.bf.popularmovies.utility.TMDBUtils;

import java.net.URL;
import java.util.ArrayList;

public class TMDBReviewsPresenterImpl implements MVP_TMDBReviews.IPresenter{

    //private static final String TAG = TMDBReviewsPresenterImpl.class.getSimpleName();

    private MVP_TMDBReviews.IView mView;
    private String mApiKey = null;

    private ArrayList<TMDBReview> mReviewList;

    public TMDBReviewsPresenterImpl(String apiKey, MVP_TMDBReviews.IView viewReviews) {
        this.mApiKey = apiKey;
        this.attachView(viewReviews);
    }

    @Override
    public void attachView(MVP_TMDBReviews.IView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getTMDBReviews(Enums.LanguageLocale langLocale, int movieId) {
        final URL urlReviews = TMDBUtils.buildAPIUrl_Reviews(mApiKey, langLocale, movieId);
        if (urlReviews != null) {
            UpdateTMDBReviewsTask updateTaskReviews = new UpdateTMDBReviewsTask(urlReviews, new ITMDBReviewsResponseHandler() {
                @SuppressWarnings("Convert2Diamond")
                @Override
                public void onTMDBReviewsResponse_OK(TMDBReviewResults tmdbReviews) {
                    mReviewList = new ArrayList<TMDBReview>(tmdbReviews.getResults());
                    if (mView != null)
                        mView.onTMDBReviewsResponse_OK();
                }

                @Override
                public void onTMDBReviewsResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                    if (mView != null)
                        mView.logMessageToView(errorMsg);
                }
            });
            updateTaskReviews.doUpdate();
        }
        else{
            if (mView!=null)
                mView.logMessageToView("Invalid reviews url");
        }
    }

    public ArrayList<TMDBReview> getReviewList() {
        return mReviewList;
    }

}
