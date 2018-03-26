package com.bf.popularmovies.ui.fragments;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.ReviewsAdapter;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBReview;
import com.bf.popularmovies.presenter.MVP_TMDBReviews;
import com.bf.popularmovies.presenter.TMDBReviewsPresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;

/*
 * @author frielb 
 * Created on 22/03/2018
 */

public class FragmentReviews extends Fragment implements MVP_TMDBReviews.IView {

    private static final String TAG = FragmentReviews.class.getSimpleName();
    public final static String KEY_MOVIE = "key_movie";

    private MVP_TMDBReviews.IPresenter mPresenter;
    private TMDBMovie mMovie;
    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.listview_reviews)
    ListView mReviewsList;

    /**
     * Method to allow instantiation of fragment with argument(s)
     * @param movie
     * @return fragment instance
     */
    public static FragmentReviews newInstance(TMDBMovie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);

        FragmentReviews fragment = new FragmentReviews();
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

        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this, rootView);

        //mTvLabel1.setText(getString(R.string.frag1));
        //mMovie = ((Details2Activity)getActivity()).getMovie();
        readBundle(getArguments());
        attachTMDBPresenter();

        //mReviewsAdapter = new ReviewsAdapter(getActivity(),((TMDBReviewsPresenterImpl) mPresenter).getReviewList());
        mReviewsAdapter = new ReviewsAdapter(getActivity());
        mReviewsList.setAdapter(mReviewsAdapter);
        buildView();

        return rootView;
    }

    private void buildView(){
        if (mMovie != null){
            mPresenter.getTMDBReviews(TMDBManager.getInstance().getLanguage(), mMovie.getId());
        }
    }

    private void attachTMDBPresenter(){
        mPresenter = (MVP_TMDBReviews.IPresenter) getActivity().getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new TMDBReviewsPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void reloadReviewsAdapter(){
        if (((TMDBReviewsPresenterImpl)mPresenter).getReviewList() != null) {
            if (((TMDBReviewsPresenterImpl)mPresenter).getReviewList().size() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Reviews OK", Toast.LENGTH_SHORT).show();
                        //mMovieAdapter.reloadAdapter(movies, !mLayoutAsGrid);
                        //mMovieAdapter.reloadAdapter(((TMDBMoviesPresenterImpl)mPresenter).getMovieList(), !mLayoutAsGrid);
                        for (TMDBReview review : ((TMDBReviewsPresenterImpl) mPresenter).getReviewList()) {
                            Log.d(TAG, "Review: " + review.getAuthor());
                        }
                        mReviewsAdapter.reloadAdapter(((TMDBReviewsPresenterImpl) mPresenter).getReviewList());
                    }
                });
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 26/03/2018 Handle if not default lang? Tidy message (in appropriate language).
                        Toast.makeText(getActivity(), "No reviews", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void logMessageToView(final String msg) {
        Log.d(TAG, "logMessageToView: ["+msg+"]");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTMDBReviewsResponse_OK() {
        //Toast.makeText(getActivity(), "Reviews OK", Toast.LENGTH_SHORT).show();
        reloadReviewsAdapter();
    }

    @Override
    public void onTMDBReviewsResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        Toast.makeText(getActivity(), "Reviews err: "+errorMsg, Toast.LENGTH_SHORT).show();
    }
}

