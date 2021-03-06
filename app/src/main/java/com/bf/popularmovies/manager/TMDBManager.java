package com.bf.popularmovies.manager;

/*
 * @author frielb 
 * Created on 21/02/2018
 *
 * Singleton class to hold all persistent TMDB information
 */

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.model.TMDBSysConfig;

public class TMDBManager {

    //private static final String TAG = TMDBManager.class.getSimpleName();

    // TODO: Later. I/O Allow for persistent info to be written to device storage
    //private Context mContext;
    //private String mUrlSysConfig = "";

    // TODO: Perform a date comparison to allow for SysConfig updates
    //private Date mDateOfLastSysConfigUpdate;

    private TMDBSysConfig mSysConfig;
    private TMDBGenres mGenres;
    private Enums.LanguageLocale mLanguage = Enums.LanguageLocale.ENGLISH; //default

    private static TMDBManager instance;

    public static TMDBManager getInstance()
    {
        if (instance == null)
        {
            synchronized (TMDBManager.class)
            {
                if (instance == null)
                {
                    instance = new TMDBManager();
                }
            }
        }
        return instance;
    }

    public boolean hasRecentSysConfig(){
        if (mSysConfig == null)
            return false;

        //if (mDateOfLastSysConfigUpdate == null)
        //    return false;
        //Date now = new Date();

        // TODO: validate some key values in SysConfig

        return true;
    }

    public  TMDBSysConfig getTMDBSysConfig() {
        //if (this.mSysConfig == null)
        //    this.mSysConfig = new mSysConfig();
        return mSysConfig;
    }

    public void setTMDBSysConfig(TMDBSysConfig sysConfig) {
        this.mSysConfig = sysConfig;
    }

    public void setTMDBGenres(TMDBGenres mGenres) {
        this.mGenres = mGenres;
    }

    public TMDBGenres getTMDBGenres() {
        return mGenres;
    }

    public Enums.LanguageLocale getLanguage() {
        return mLanguage;
    }

    public void setLanguage(Enums.LanguageLocale mLanguage) {
        this.mLanguage = mLanguage;
    }

}
