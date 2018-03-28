package com.bf.popularmovies.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.ReviewsAdapter;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBReview;
import com.bf.popularmovies.presenter.MVP_TMDBReviews;
import com.bf.popularmovies.presenter.TMDBReviewsPresenterImpl;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * @author frielb 
 * Created on 22/03/2018
 */

public class FragmentReviews extends Fragment implements MVP_TMDBReviews.IView {

    private static final String TAG = FragmentReviews.class.getSimpleName();
    public final static String KEY_MOVIE = "key_movie";
    public final static String KEY_REVIEWLIST = "key_reviewlist";

    private MVP_TMDBReviews.IPresenter mPresenter;
    private TMDBMovie mMovie;
    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.listview_reviews)
    ListView mReviewsListView;
    private ArrayList<TMDBReview> mReviewListLocal;

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
            if (bundle.containsKey(KEY_REVIEWLIST)){
                mReviewListLocal = bundle.getSerializable(KEY_REVIEWLIST);
            }
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
        mReviewsListView.setAdapter(mReviewsAdapter);
        mReviewsListView.setDivider(null);
        mReviewsListView.setDividerHeight(0);

        buildView();

        return rootView;
    }

//    @Override
//    public Object onRetainCustomNonConfigurationInstance() {
//        return mPresenter;
//    }
//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_REVIEWLIST,mReviewListLocal);
//        outState.putString(keyFrag2,mFragData2);
//        outState.putString(keyFrag3,mFragData3);
//
    }

    private void buildView(){
        if (mMovie != null){
            //if (((TMDBReviewsPresenterImpl) mPresenter).getReviewList() != null){
            if (mReviewListLocal != null){
                //load existing in case of device rotation
                reloadReviewsAdapter();
            }
            else{
                mPresenter.getTMDBReviews(TMDBManager.getInstance().getLanguage(), mMovie.getId());
            }

        }
    }

    private void attachTMDBPresenter(){
        // I cannot save an instance state of presenter object within a fragment
        //if (mPresenter == null)
            mPresenter = new TMDBReviewsPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        if (mPresenter == null)
            mPresenter.detachView();

        super.onDestroyView();
    }

    private void reloadReviewsAdapter(){
//        if (((TMDBReviewsPresenterImpl)mPresenter).getReviewList() != null) {
//            if (((TMDBReviewsPresenterImpl)mPresenter).getReviewList().size() > 0) {
        if (mReviewListLocal != null) {
            if (mReviewListLocal.size() > 0) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        for (TMDBReview review : ((TMDBReviewsPresenterImpl) mPresenter).getReviewList()) {
//                            Log.d(TAG, "Review: " + review.getAuthor());
//                        }
                        //mReviewsAdapter.reloadAdapter(((TMDBReviewsPresenterImpl) mPresenter).getReviewList());
                        mReviewsAdapter.reloadAdapter(mReviewListLocal);
                    }
                });
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getActivity().getString(R.string.availablenot) + " (" + getActivity().getString(R.string.reviews)+ ")";
                        // TODO: 26/03/2018 Handle if not default lang? Tidy message (in appropriate language).
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
        // Keep a local instance for device rotation
        this.mReviewListLocal = ((TMDBReviewsPresenterImpl) mPresenter).getReviewList();
        reloadReviewsAdapter();
    }

    @Override
    public void onTMDBReviewsResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        logMessageToView("Reviews err: "+errorMsg);
    }
}

