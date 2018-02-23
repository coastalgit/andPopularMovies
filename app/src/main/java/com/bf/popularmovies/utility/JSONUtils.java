package com.bf.popularmovies.utility;

/*
 * Simple utility class to parse JSON
 *
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.model.TMDBGenre;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.model.TMDBMovieResults;
import com.bf.popularmovies.model.TMDBSysConfig;
import com.google.gson.Gson;

public class JSONUtils {
    private static final String TAG = JSONUtils.class.getSimpleName();

    public static TMDBMovieResults parseTMDBMoviesJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        TMDBMovieResults movies = gson.fromJson(json,TMDBMovieResults.class);
        return movies;
    }

    public static TMDBSysConfig parseTMDBSysConfigJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        TMDBSysConfig sysCfg = gson.fromJson(json,TMDBSysConfig.class);
        return sysCfg;
    }

    public static TMDBGenres parseTMDBGenresJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        TMDBGenres genres = gson.fromJson(json,TMDBGenres.class);
        return genres;
    }

}
