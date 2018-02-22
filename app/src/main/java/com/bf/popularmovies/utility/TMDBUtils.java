package com.bf.popularmovies.utility;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import android.net.Uri;
import android.util.Log;

import com.bf.popularmovies.common.Enums;

import java.net.MalformedURLException;
import java.net.URL;

public class TMDBUtils {
    private static final String TAG = TMDBUtils.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String PARAM_APIKEY = "api_key";

    /*
        http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc?&api_key={key}
        http://api.themoviedb.org/3/movie/popular?api_key={key}&language=en-UK&page=2
        http://api.themoviedb.org/3/movie/top_rated?api_key={key}&language=pt-PT&page=1
     */
    private static final String URL_PATH_MOVIE = "movie";
    private static final String URL_PATH_CONFIG = "configuration";

    public static URL buildAPIUrl_SysConfig(String apiKey) {

        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(URL_PATH_CONFIG)
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .build();

        try {
            URL tmdb_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_SysConfig: ["+tmdb_url+"]");
            return tmdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

    public static URL buildAPIUrl_Movies(String apiKey, Enums.TMDBQueryBy queryBy, Enums.LanguageLocale langLocale, int pageCount) {

        final String PARAM_PAGE = "page";
        final String PARAM_LANGUAGE = "language";

        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(URL_PATH_MOVIE)
                .appendPath(queryBy.toString())
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .appendQueryParameter(PARAM_LANGUAGE, langLocale.toString())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(pageCount))
                .build();

        try {
            URL tmdb_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_Movies: ["+tmdb_url+"]");
            return tmdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

    public static URL buildAPIUrl_PosterImage(String baseUrl, String posterPath, String posterSize) {

        // strip the leading slash or it will be encoded
        posterPath = posterPath.replace("/","");

        Uri tmdb_uri = Uri.parse(baseUrl).buildUpon()
                .appendPath(posterSize)
                .appendPath(posterPath)
                .build();
        try {
            URL image_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_PosterImage: ["+image_url+"]");
            return image_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

}
