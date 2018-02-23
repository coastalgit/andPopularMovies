package com.bf.popularmovies.task;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBMovieResults;

@SuppressWarnings("unused")
public interface ITMDBMoviesResponseHandler {
        void onTMDBMoviesResponse_OK(TMDBMovieResults tmdbMovies);
        void onTMDBMoviesResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
}
