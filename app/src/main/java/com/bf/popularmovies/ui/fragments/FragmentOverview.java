package com.bf.popularmovies.ui.fragments;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.utility.TMDBUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;


/*
 * @author frielb 
 * Created on 22/03/2018
 */

@SuppressWarnings({"JavaDoc", "WeakerAccess"})
public class FragmentOverview extends Fragment {

    private final static String KEY_MOVIE = "key_movie";

    private TMDBMovie mMovie;

    @BindView(R.id.tv_detail_bodycaption)
    TextView mMovieBodyCaption;
    @BindView(R.id.tv_detail_bodycaption2)
    TextView mMovieBodyCaption2;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, rootView);

        readBundle(getArguments());
        buildView();

        return rootView;
    }

    @SuppressWarnings("ConstantConditions")
    private void buildView(){
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), FONT_TITILLIUM_REGULAR);

        String captionStr = getString(R.string.released) + ": (" + getString(R.string.availablenot) +")";
        String genreCaption = getString(R.string.genre) + ": " + getString(R.string.availablenot);
        String bodyText = getString(R.string.availablenot);

        if (mMovie != null){
            captionStr = getString(R.string.released) + ": " + mMovie.getReleaseDate();
            if (mMovie.getOriginalLanguage().length() > 0)
                captionStr = captionStr + " (" + mMovie.getOriginalLanguage().toUpperCase() + ")";

            genreCaption = getString(R.string.genre) + ": " + getString(R.string.unknown);
            if (mMovie.getGenreIds() != null) {
                ArrayList<String> genresList = TMDBUtils.buildGenreStringListById(TMDBManager.getInstance().getTMDBGenres(), mMovie.getGenreIds());
                if (genresList != null && genresList.size() > 0)
                    genreCaption = getString(R.string.genre) + ": " + TextUtils.join(", ", genresList);
            }
            bodyText = mMovie.getOverview();
        }

        mMovieBodyCaption.setText(captionStr);
        mMovieBodyCaption.setTypeface(font);
        mMovieBodyCaption2.setText(genreCaption);
        mMovieBodyCaption2.setTypeface(font);
        mMovieBodyText.setText(bodyText);
        mMovieBodyText.setTypeface(font);

    }

}
