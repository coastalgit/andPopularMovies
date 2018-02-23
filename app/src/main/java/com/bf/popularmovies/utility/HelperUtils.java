package com.bf.popularmovies.utility;

/*
 * Simple utility class to parse JSON
 *
 * @author frielb 
 * Created on 19/02/2018
 */

import com.bf.popularmovies.common.Enums;

public class HelperUtils {

    //private static String languageLocaleToDisplayString(Context context, Enums.LanguageLocale langlocale){
    public static String languageLocaleToDisplayString(Enums.LanguageLocale langlocale){
        switch (langlocale){
            case PORTUGUESE:
                return "Portuguese";
            default:
                return "English";
        }
    }
}
