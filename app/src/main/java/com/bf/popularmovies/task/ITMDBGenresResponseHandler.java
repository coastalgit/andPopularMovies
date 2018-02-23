package com.bf.popularmovies.task;

/*
 * @author frielb 
 * Created on 23/02/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.model.TMDBSysConfig;

public interface ITMDBGenresResponseHandler {
        void onTMDBGenresResponse_OK(TMDBGenres tmdbGenres);
        void onTMDBGenresResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
}
