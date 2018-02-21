package com.bf.popularmovies.utility;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.bf.popularmovies.common.Enums;

import java.net.MalformedURLException;
import java.net.URL;

public class IMDBUtils {

    /*
        http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc?&api_key={key}
        http://api.themoviedb.org/3/movie/popular?api_key={key}&language=en-UK&page=2
        http://api.themoviedb.org/3/movie/top_rated?api_key={key}&language=pt-PT&page=1
     */

    private static final String TAG = IMDBUtils.class.getSimpleName();

    public static URL buildAPIUrl(String apiKey, Enums.IMDBQueryBy queryBy, Enums.LanguageLocale langLocale, int pageCount) {

        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String PARAM_APIKEY = "api_key";
        final String PARAM_PAGE = "page";
        final String PARAM_LANGUAGE = "language";

        Uri imdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(queryBy.toString())
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .appendQueryParameter(PARAM_LANGUAGE, langLocale.toString())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(pageCount))
                .build();

        try {
            URL imdb_url = new URL(imdb_uri.toString());
            Log.d(TAG, "buildAPIUrl: ["+imdb_url+"]");
            return imdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

}
