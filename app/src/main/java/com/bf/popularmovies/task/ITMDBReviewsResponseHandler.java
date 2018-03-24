package com.bf.popularmovies.task;

/*
 * @author frielb 
 * Created on 21/03/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBMovieResults;
import com.bf.popularmovies.model.TMDBVideoResults;

@SuppressWarnings("unused")
public interface ITMDBVideosResponseHandler {
        void onTMDBVideosResponse_OK(TMDBVideoResults tmdbVideos);
        void onTMDBVideosResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
}
