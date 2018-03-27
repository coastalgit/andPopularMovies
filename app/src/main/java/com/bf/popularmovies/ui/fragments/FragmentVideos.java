package com.bf.popularmovies.ui.fragments;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBVideo;
import com.bf.popularmovies.presenter.MVP_TMDBVideos;
import com.bf.popularmovies.presenter.TMDBVideosPresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;


/*
 * @author frielb 
 * Created on 22/03/2018
 */

public class FragmentVideos extends Fragment implements MVP_TMDBVideos.IView {

    private static final String TAG = FragmentReviews.class.getSimpleName();
    public final static String KEY_MOVIE = "key_movie";

    private MVP_TMDBVideos.IPresenter mPresenter;
    private TMDBMovie mMovie;

    /**
     * Method to allow instantiation of fragment with argument(s)
     * @param movie
     * @return fragment instance
     */
    public static FragmentVideos newInstance(TMDBMovie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MOVIE, movie);

        FragmentVideos fragment = new FragmentVideos();
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

        View rootView = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, rootView);

        readBundle(getArguments());
        attachTMDBPresenter();

        buildView();

        return rootView;
    }

    private void buildView(){
        if (mMovie != null){
            //Typeface font = Typeface.createFromAsset(getActivity().getAssets(), FONT_TITILLIUM_REGULAR);

            mPresenter.getTMDBVideos(mMovie.getId());
        }
    }

    private void attachTMDBPresenter(){
        mPresenter = (MVP_TMDBVideos.IPresenter) getActivity().getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new TMDBVideosPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void reloadVideosAdapter(){
        if (((TMDBVideosPresenterImpl)mPresenter).getVideoList() != null) {
            if (((TMDBVideosPresenterImpl)mPresenter).getVideoList().size() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (TMDBVideo video : ((TMDBVideosPresenterImpl) mPresenter).getVideoList()) {
                            Log.d(TAG, "Video: " + video.getName());
                        }
                        //mReviewsAdapter.reloadAdapter(((TMDBReviewsPresenterImpl) mPresenter).getReviewList());
                    }
                });
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getActivity().getString(R.string.availablenot) + " (" + getActivity().getString(R.string.trailers)+ ")";
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
    public void onTMDBVideosResponse_OK() {
        //logMessageToView("Videos OK");
        reloadVideosAdapter();
    }

    @Override
    public void onTMDBVideosResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        logMessageToView("Videos err: "+errorMsg);
    }
}

