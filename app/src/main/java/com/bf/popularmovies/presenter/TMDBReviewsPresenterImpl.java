package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBMovieResults;
import com.bf.popularmovies.model.TMDBSysConfig;
import com.bf.popularmovies.model.TMDBVideo;
import com.bf.popularmovies.model.TMDBVideoResults;
import com.bf.popularmovies.task.ITMDBGenresResponseHandler;
import com.bf.popularmovies.task.ITMDBMoviesResponseHandler;
import com.bf.popularmovies.task.ITMDBSysConfigResponseHandler;
import com.bf.popularmovies.task.ITMDBVideosResponseHandler;
import com.bf.popularmovies.task.UpdateTMDBGenresTask;
import com.bf.popularmovies.task.UpdateTMDBMoviesTask;
import com.bf.popularmovies.task.UpdateTMDBSysConfigTask;
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
                        mView.logMessageToView(errorMsg);
                }
            });
        }
        else{
            if (mView!=null)
                mView.logMessageToView("Invalid movies url");
        }
    }

    public ArrayList<TMDBVideo> getVideoList() {
        return mVideoList;
    }

}
