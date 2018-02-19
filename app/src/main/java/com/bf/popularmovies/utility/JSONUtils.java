package com.bf.popularmovies.utility;

/*
 * Simple utility class to parse JSON
 *
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.model.Movies;
import com.google.gson.Gson;

public class JSONUtils {
    private static final String TAG = JSONUtils.class.getSimpleName();

    public static Movies parseTMDBJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        Movies movies = gson.fromJson(json,Movies.class);
        return movies;
    }
}
