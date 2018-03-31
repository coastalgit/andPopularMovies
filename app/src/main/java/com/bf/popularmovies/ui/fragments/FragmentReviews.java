package com.bf.popularmovies.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.bf.popularmovies.presenter.MVP_TMDBReviews;
import com.bf.popularmovies.presenter.TMDBReviewsPresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;

/*
 * @author frielb 
 * Created on 22/03/2018
 */

@SuppressWarnings("ALL")
public class FragmentReviews extends Fragment implements MVP_TMDBReviews.IView {

    private static final String TAG = FragmentReviews.class.getSimpleName();
    private final static String KEY_MOVIE = "key_movie";

    private MVP_TMDBReviews.IPresenter mPresenter;
    private TMDBMovie mMovie;
    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.listview_reviews)
    ListView mReviewsListView;
    @BindView(R.id.tv_reviews_none)
    TextView mTvNoReviewsLabel;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this, rootView);

        readBundle(getArguments());
        attachTMDBPresenter();

        mReviewsAdapter = new ReviewsAdapter(getActivity());
        mReviewsListView.setAdapter(mReviewsAdapter);
        mReviewsListView.setDivider(null);
        mReviewsListView.setDividerHeight(0);

        // we want to enable nested scrolling of reviews within the (entire scrolling) fragment itself
        boolean isLandscapeOrientation = getResources().getBoolean(R.bool.islandscapeorient);
        if (isLandscapeOrientation){
            mReviewsListView.setOnTouchListener(new ListView.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    view.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }

        buildView();

        return rootView;
    }

//    @Override
//    public Object onRetainCustomNonConfigurationInstance() {
//        return mPresenter;
//    }

    private void buildView(){
        if (mMovie != null){
            if (((TMDBReviewsPresenterImpl) mPresenter).getReviewList() != null){
                reloadReviewsAdapter();
            }
            else{
                mPresenter.getTMDBReviews(TMDBManager.getInstance().getLanguage(), mMovie.getId());
            }
        }
    }

    private void attachTMDBPresenter(){
        // retained presenter instance
        if (mPresenter == null)
            mPresenter = new TMDBReviewsPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroyView() {
        if (mPresenter == null)
            mPresenter.detachView();

        super.onDestroyView();
    }

    private void reloadReviewsAdapter(){
        if (((TMDBReviewsPresenterImpl)mPresenter).getReviewList() != null) {
            if (((TMDBReviewsPresenterImpl)mPresenter).getReviewList().size() > 0) {
                //mTvNoReviewsLabel.setVisibility(View.GONE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mReviewsAdapter != null)
                            mReviewsAdapter.reloadAdapter(((TMDBReviewsPresenterImpl) mPresenter).getReviewList());
                    }
                });
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), FONT_TITILLIUM_REGULAR);
                        mTvNoReviewsLabel.setText(getActivity().getString(R.string.availablenot));
                        mTvNoReviewsLabel.setVisibility(View.VISIBLE);
                        //TODO: 26/03/2018 Additional option to refresh in default (en) lang? Also need a tidy error message.
                    }
                });
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
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
        reloadReviewsAdapter();
    }

    @Override
    public void onTMDBReviewsResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        logMessageToView("Reviews err: "+errorMsg);
    }
}

