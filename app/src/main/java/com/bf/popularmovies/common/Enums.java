package com.bf.popularmovies.common;

/*
 * @author frielb 
 * Created on 19/02/2018
 */

public class Enums {

    public enum LanguageLocale{
        ENGLISH {public String toString(){return "en-UK";}},
        PORTUGUESE {public String toString(){return "pt-PT";}}
    }

    public enum TMDBQueryBy {
        POPULAR {public String toString(){return "popular";}},
        TOPRATED {public String toString(){return "top_rated";}}
    }

    public enum TMDBErrorCode {
        @SuppressWarnings("unused")UNKNOWN,
        CONNECTION_ERROR,
        INVALID_DATA,
        INVALID_RESPONSE
    }
}
