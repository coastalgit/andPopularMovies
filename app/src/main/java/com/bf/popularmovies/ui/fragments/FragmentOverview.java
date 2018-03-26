package com.bf.popularmovies.ui.fragments;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.model.TMDBMovie;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;


/*
 * @author frielb 
 * Created on 22/03/2018
 */

public class FragmentOverview extends Fragment {

    public final static String KEY_MOVIE = "key_movie";

    private TMDBMovie mMovie;

    @BindView(R.id.tv_overview_bodytext)
    TextView mMovieBodyText;


    /**
     * Method to allow instantiation of fragment with argument(s)
     * @param movie
     * @return fragment instance
     */
    public static FragmentOverview newInstance(TMDBMovie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);

        FragmentOverview fragment = new FragmentOverview();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            mMovie = (TMDBMovie) bundle.getSerializable(KEY_MOVIE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, rootView);

        readBundle(getArguments());
        buildView();

        return rootView;
    }

    private void buildView(){
        if (mMovie != null){
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), FONT_TITILLIUM_REGULAR);

            mMovieBodyText.setText(mMovie.getOverview());
            mMovieBodyText.setTypeface(font);

        }
        else{
            // TODO: 26/03/2018 Display error text
        }
    }

}
