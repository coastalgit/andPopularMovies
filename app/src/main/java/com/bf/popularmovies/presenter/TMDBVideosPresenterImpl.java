package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 21/03/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBVideo;
import com.bf.popularmovies.model.TMDBVideoResults;
import com.bf.popularmovies.task.ITMDBVideosResponseHandler;
import com.bf.popularmovies.task.UpdateTMDBVideosTask;
import com.bf.popularmovies.utility.TMDBUtils;

import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("Convert2Diamond")
public class TMDBVideosPresenterImpl implements MVP_TMDBVideos.IPresenter{

    //private static final String TAG = TMDBVideosPresenterImpl.class.getSimpleName();

    private MVP_TMDBVideos.IView mView;
    private String mApiKey = null;

    private ArrayList<TMDBVideo> mVideoList;

    public TMDBVideosPresenterImpl(String apiKey, MVP_TMDBVideos.IView viewVideos) {
        this.mApiKey = apiKey;
        this.attachView(viewVideos);
    }

    @Override
    public void attachView(MVP_TMDBVideos.IView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getTMDBVideos(int movieId) {
        final URL urlVideos = TMDBUtils.buildAPIUrl_Videos(mApiKey, movieId);
        if (urlVideos != null) {
            UpdateTMDBVideosTask updateTaskVideos = new UpdateTMDBVideosTask(urlVideos, new ITMDBVideosResponseHandler() {
                @Override
                public void onTMDBVideosResponse_OK(TMDBVideoResults tmdbVideos) {
                    mVideoList = new ArrayList<TMDBVideo>(tmdbVideos.getResults());
                    if (mView != null)
                        mView.onTMDBVideosResponse_OK();
                }

                @Override
                public void onTMDBVideosResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
                    if (mView != null)
                        //mView.logMessageToView(errorMsg);
                        mView.onTMDBVideosResponse_Error(code, errorMsg);
                }
            });
            updateTaskVideos.doUpdate();
        }
        else{
            if (mView!=null)
                mView.logMessageToView("Invalid videos url");
        }
    }

    public ArrayList<TMDBVideo> getVideoList() {
        return mVideoList;
    }

}
