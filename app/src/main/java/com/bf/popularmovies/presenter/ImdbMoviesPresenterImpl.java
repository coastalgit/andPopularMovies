package com.bf.popularmovies.presenter;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

import android.util.Log;

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.utility.IMDBUtils;

import java.net.URL;

public class ImdbMoviesPresenterImpl implements MVP_ImdbMovies.IPresenter {

    private static final String TAG = ImdbMoviesPresenterImpl.class.getSimpleName();

    private MVP_ImdbMovies.IView mView;
    private String mApiKey = "";

    public ImdbMoviesPresenterImpl(String apiKey, MVP_ImdbMovies.IView viewMovies) {
        this.mApiKey = apiKey;
        this.attachView(viewMovies);
    }


    @Override
    public void attachView(MVP_ImdbMovies.IView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getImdbMoviesByPopularity(Enums.LanguageLocale langLocale, int pageCount) {
        getImdbMovies(Enums.IMDBQueryBy.POPULAR, langLocale, pageCount);
    }

    @Override
    public void getImdbMoviesByTopRated(Enums.LanguageLocale langLocale, int pageCount) {
        getImdbMovies(Enums.IMDBQueryBy.TOPRATED, langLocale, pageCount);
    }

    private void getImdbMovies(Enums.IMDBQueryBy queryBy, Enums.LanguageLocale lang, int pages){
        URL imdburl = IMDBUtils.buildAPIUrl(this.mApiKey, queryBy, lang, pages);
        if (mView!=null)
            mView.logMessageToView("URL:"+"["+imdburl.toString()+"]");



    }
}
