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

//    private Context context;

//    public DetailSectionsPagerAdapter(FragmentManager fm, Context context) {
//        super(fm);
//        this.context = context;
//    }

    public DetailSectionsPagerAdapter(FragmentManager fm, TMDBMovie movie) {
        super(fm);
        this.mMovie = movie;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return Details2Activity.PlaceholderFragment.newInstance(position + 1);
        Fragment frag = null;
        switch (position){
            case 0:
                //frag = new FragmentOverview();
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
