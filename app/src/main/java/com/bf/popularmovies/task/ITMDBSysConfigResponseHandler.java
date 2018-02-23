package com.bf.popularmovies.task;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBSysConfig;

@SuppressWarnings("unused")
public interface ITMDBSysConfigResponseHandler {
        void onTMDBSysConfigResponse_OK(TMDBSysConfig tmdbSysConfig);
        void onTMDBSysConfigResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
}
