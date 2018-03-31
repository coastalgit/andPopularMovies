package com.bf.popularmovies.adapter;

/*
 * @author frielb
 * Created on 21/02/2018
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.model.TMDBVideo;
import com.bf.popularmovies.utility.TMDBUtils;
import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

@SuppressWarnings("deprecation")
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private static final String TAG = VideosAdapter.class.getSimpleName();

    private final Context mContext;
    private final VideosAdapterOnClickHandler mClickHandler;
    private ArrayList<TMDBVideo> mVideoList;

    public interface VideosAdapterOnClickHandler {
        void onClickVideo(TMDBVideo video);
    }

    public VideosAdapter(@NonNull Context context, VideosAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public void reloadAdapter(ArrayList<TMDBVideo> videos){
        this.mVideoList = videos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.video_list_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapterViewHolder holder, int position) {
        TMDBVideo video = mVideoList.get(position);
        Log.d(TAG, "onBindViewHolder: vid id:["+video.getId()+"]");
        //holder.videoTitle.setText(video.getName());
        URL image_path = TMDBUtils.buildAPIUrl_VideoThumbnail(video.getKey());

        Glide.with(mContext)
                .load(image_path)
                .apply(fitCenterTransform())
                .into(holder.videoThumbnail);
    }

    @Override
    public int getItemCount() {
        if (mVideoList == null)
            return 0;
        return mVideoList.size();
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView videoThumbnail;

        public VideosAdapterViewHolder(View itemView) {
            super(itemView);

            videoThumbnail = itemView.findViewById(R.id.iv_video_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            TMDBVideo video = mVideoList.get(pos);
            mClickHandler.onClickVideo(video);
        }
    }
}

