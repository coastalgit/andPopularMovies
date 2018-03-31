package com.bf.popularmovies.utility;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import android.net.Uri;
import android.util.Log;

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBGenre;
import com.bf.popularmovies.model.TMDBGenres;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TMDBUtils {
    private static final String TAG = TMDBUtils.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String PARAM_APIKEY = "api_key";

    private static final String BASE_URL_THUMBNAILS = "https://img.youtube.com/vi/";
    private static final String PARAM_THUMBNAIL_DEFAULTSIZE = "0.jpg";

    /*
        http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc?&api_key={key}
        http://api.themoviedb.org/3/movie/popular?api_key={key}&language=en-UK&page=2
        http://api.themoviedb.org/3/movie/top_rated?api_key={key}&language=pt-PT&page=1
        http://api.themoviedb.org/3/genre/movie/list?api_key={key}
        https://api.themoviedb.org/3/movie/390043/videos?api_key={key}
        https://api.themoviedb.org/3/movie/390043/reviews?api_key={key}&language=en-US&page=1
     */

    private static final String PARAM_MOVIE = "movie";
    private static final String PARAM_CONFIG = "configuration";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_GENRE = "genre";
    private static final String PARAM_LIST = "list";
    private static final String PARAM_VIDEOS = "videos";
    private static final String PARAM_REVIEWS = "reviews";


    public static URL buildAPIUrl_SysConfig(String apiKey) {

        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_CONFIG)
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

        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_MOVIE)
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

    public static URL buildAPIUrl_Image(String baseUrl, String posterPath, String posterSize) {

        // strip the leading slash or it will be encoded
        posterPath = posterPath.replace("/","");

        Uri tmdb_uri = Uri.parse(baseUrl).buildUpon()
                .appendPath(posterSize)
                .appendPath(posterPath)
                .build();
        try {
            URL image_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_Image: ["+image_url+"]");
            return image_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

    public static URL buildAPIUrl_Genres(String apiKey) {

        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_GENRE)
                .appendPath(PARAM_MOVIE)
                .appendPath(PARAM_LIST)
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .build();

        try {
            URL tmdb_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_Genres: ["+tmdb_url+"]");
            return tmdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

    public static ArrayList<String> buildGenreStringListById(TMDBGenres genresAvailable, List<Integer> genreIds){
        if (genresAvailable == null || genreIds == null)
            return null;

        ArrayList<String> genreStrings = new ArrayList<>();

        for(int i=0; i <genreIds.size(); i++){
            int gid =  genreIds.get(i);
            for (TMDBGenre genre: genresAvailable.getGenres()) {
                if (genre.getId().equals(gid)){
                    genreStrings.add(genre.getName());
                    break;
                }
            }
        }

        Log.d(TAG, "buildGenreStringListById: Genre count["+String.valueOf(genreStrings.size())+"]");
        return genreStrings;
    }

    //public static URL buildAPIUrl_Videos(String apiKey, Enums.LanguageLocale langLocale, int movieId) {
    public static URL buildAPIUrl_Videos(String apiKey, int movieId) {

        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_MOVIE)
                .appendPath(String.valueOf(movieId))
                .appendPath(PARAM_VIDEOS)
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .build();

        try {
            URL tmdb_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_Videos: ["+tmdb_url+"]");
            return tmdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

    public static URL buildAPIUrl_Reviews(String apiKey, Enums.LanguageLocale langLocale, int movieId) {


        Uri tmdb_uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_MOVIE)
                .appendPath(String.valueOf(movieId))
                .appendPath(PARAM_REVIEWS)
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .appendQueryParameter(PARAM_LANGUAGE, langLocale.toString())
                .build();

        try {
            URL tmdb_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_Reviews: ["+tmdb_url+"]");
            return tmdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

    public static URL buildAPIUrl_VideoThumbnail(String videoKey) {


        Uri tmdb_uri = Uri.parse(BASE_URL_THUMBNAILS).buildUpon()
                .appendPath(videoKey)
                .appendPath(PARAM_THUMBNAIL_DEFAULTSIZE)
                .build();

        try {
            URL tmdb_url = new URL(tmdb_uri.toString());
            Log.d(TAG, "buildAPIUrl_VideoThumbnail: ["+tmdb_url+"]");
            return tmdb_url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exc:["+e+"]");
        }

        return null;
    }

}
