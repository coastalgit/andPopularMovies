package com.bf.popularmovies.ui.activity;

/*
 * @author frielb 
 * Created on 22/03/2018
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.ui.fragments.FragmentOverview;
import com.bf.popularmovies.ui.fragments.FragmentReviews;
import com.bf.popularmovies.ui.fragments.FragmentVideos;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DetailSectionsPagerAdapter extends FragmentPagerAdapter {

    private TMDBMovie mMovie;

    public DetailSectionsPagerAdapter(FragmentManager fm, TMDBMovie movie) {
        super(fm);
        this.mMovie = movie;
    }

    @Override
    public Fragment getItem(int position) {
        // IMPORTANT NOTE: the fragments themselves persist within the SupportFragmentManager,
        // hence this method is called only once (for each fragment instance created).

        Fragment frag = null;
        switch (position){
            case 0:
                frag = FragmentOverview.newInstance(mMovie);
                break;
            case 1:
                frag = FragmentVideos.newInstance(mMovie);
                break;
            case 2:
                frag = FragmentReviews.newInstance(mMovie);
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
