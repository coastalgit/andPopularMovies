package com.bf.popularmovies.ui.fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.VideosAdapter;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.model.TMDBVideo;
import com.bf.popularmovies.presenter.MVP_TMDBVideos;
import com.bf.popularmovies.presenter.TMDBVideosPresenterImpl;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import butterknife.BindView;
import butterknife.ButterKnife;


/*
 * @author frielb 
 * Created on 22/03/2018
 */

@SuppressWarnings({"JavaDoc", "WeakerAccess"})
public class FragmentVideos extends Fragment implements MVP_TMDBVideos.IView, VideosAdapter.VideosAdapterOnClickHandler {

    private static final String TAG = FragmentReviews.class.getSimpleName();
    private final static String KEY_MOVIE = "key_movie";

    private MVP_TMDBVideos.IPresenter mPresenter;
    private TMDBMovie mMovie;
    private VideosAdapter mVideoAdapter;

    @BindView(R.id.tv_video_caption1)
    TextView mCaption1;
    @BindView(R.id.recyclerview_videos)
    RecyclerView mRecyclerViewVideos;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, rootView);

        readBundle(getArguments());
        attachTMDBPresenter();
        applyLayoutManager();

        mRecyclerViewVideos.setHasFixedSize(true);
        mVideoAdapter = new VideosAdapter(getActivity(),this);
        mRecyclerViewVideos.setAdapter(mVideoAdapter);

        buildView();

        return rootView;
    }

    private void applyLayoutManager(){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewVideos.setLayoutManager(layoutManager);
    }

    private void buildView(){
        if (mMovie != null){
            if (((TMDBVideosPresenterImpl) mPresenter).getVideoList() != null){
                //load existing in case of device rotation
                reloadVideosAdapter();
            }
            else{
                mPresenter.getTMDBVideos(mMovie.getId());
            }

        }
    }

    private void attachTMDBPresenter(){
        // retained presenter instance
        if (mPresenter == null)
            mPresenter = new TMDBVideosPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroyView() {
        if (mPresenter == null)
            mPresenter.detachView();

        super.onDestroyView();
    }

    @SuppressWarnings("ConstantConditions")
    private void reloadVideosAdapter(){
        if (((TMDBVideosPresenterImpl)mPresenter).getVideoList() != null) {
            final int vidCount = ((TMDBVideosPresenterImpl)mPresenter).getVideoList().size();
            if (vidCount > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCaption(String.valueOf(vidCount) + " " + getActivity().getString(vidCount==1 ? R.string.video : R.string.videos));
//                        for (TMDBVideo video : ((TMDBVideosPresenterImpl) mPresenter).getVideoList()) {
//                            Log.d(TAG, "Video: " + video.getName());
//                        }
                        mVideoAdapter.reloadAdapter(((TMDBVideosPresenterImpl) mPresenter).getVideoList());
                    }
                });
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getActivity().getString(R.string.availablenot) + " (" + getActivity().getString(R.string.videos)+ ")";
                        updateCaption(msg);
                        // TODO: 26/03/2018 Additional option to refresh in default (en) lang? Also need a tidy error message.
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void updateCaption(String txt){
        mCaption1.setText(txt);
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
    public void onTMDBVideosResponse_OK() {
        reloadVideosAdapter();
    }

    @Override
    public void onTMDBVideosResponse_Error(Enums.TMDBErrorCode code, String errorMsg) {
        if (code == Enums.TMDBErrorCode.CONNECTION_ERROR){
            @SuppressWarnings("ConstantConditions") String msg = getActivity().getString(R.string.availablenot) + " (" + getActivity().getString(R.string.connection)+ ")";
            updateCaption(msg);
        }
        else
            logMessageToView("Videos err: "+errorMsg);
    }

    @Override
    public void onClickVideo(TMDBVideo video) {
        playVideo(video);

    }

    @SuppressWarnings("ConstantConditions")
    private void playVideo(TMDBVideo video){
        Log.d(TAG, "playVideo: ["+video.getKey()+"]");
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), getActivity().getString(R.string.api_key_yt), video.getKey());
        startActivity(intent);
    }
}

