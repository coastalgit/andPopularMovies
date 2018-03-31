package com.bf.popularmovies.utility;

/*
 * Simple utility class to parse JSON
 *
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.model.TMDBMovieResults;
import com.bf.popularmovies.model.TMDBReviewResults;
import com.bf.popularmovies.model.TMDBSysConfig;
import com.bf.popularmovies.model.TMDBVideoResults;
import com.google.gson.Gson;

public class JSONUtils {
   // private static final String TAG = JSONUtils.class.getSimpleName();

    public static TMDBMovieResults parseTMDBMoviesJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,TMDBMovieResults.class);
    }

    public static TMDBSysConfig parseTMDBSysConfigJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,TMDBSysConfig.class);
    }

    public static TMDBGenres parseTMDBGenresJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,TMDBGenres.class);
    }

    public static TMDBVideoResults parseTMDBVideosJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,TMDBVideoResults.class);
    }

    public static TMDBReviewResults parseTMDBReviewsJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json,TMDBReviewResults.class);
    }

}
