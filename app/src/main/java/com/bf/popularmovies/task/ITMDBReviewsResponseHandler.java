package com.bf.popularmovies.task;

/*
 * @author frielb 
 * Created on 21/03/2018
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBReviewResults;
import com.bf.popularmovies.model.TMDBVideoResults;

@SuppressWarnings("unused")
public interface ITMDBReviewsResponseHandler {
        void onTMDBReviewsResponse_OK(TMDBReviewResults tmdbReviews);
        void onTMDBReviewsResponse_Error(Enums.TMDBErrorCode code, String errorMsg);
}
