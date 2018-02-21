package com.bf.popularmovies.utility;

/*
 * Simple utility class to parse JSON
 *
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.model.IMDBMovies;
import com.google.gson.Gson;

public class JSONUtils {
    private static final String TAG = JSONUtils.class.getSimpleName();

    public static IMDBMovies parseTMDBJson(String json) {
        if (json == null)
            return null;

        Gson gson = new Gson();
        IMDBMovies movies = gson.fromJson(json,IMDBMovies.class);
        return movies;
    }
}
