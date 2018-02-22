package com.bf.popularmovies.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.utility.TMDBUtils;
import com.bumptech.glide.Glide;

import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

import java.net.URL;
import java.util.ArrayList;

/*
 * @author frielb 
 * Created on 21/02/2018
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private ArrayList<TMDBMovie> mMovieList;
    private final String FONT_MOVIEPOSTER = "SFMoviePoster.ttf";

    public boolean getAsBackDropImage() {
        return mWithBackDropImage;
    }

    public void setAsBackDropImage(boolean mAsBackDropImage) {
        this.mWithBackDropImage = mAsBackDropImage;
    }

    private boolean mWithBackDropImage = false;

    public interface MoviesAdapterOnClickHandler {
        void onClick(TMDBMovie movie);
    }

    public MoviesAdapter(@NonNull Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        //mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    public void reloadAdapter(ArrayList<TMDBMovie> movies, boolean withBackdropImage){
        this.mMovieList = movies;
        this.mWithBackDropImage = withBackdropImage;
        notifyDataSetChanged();
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_list_item_notitle;
        if (mWithBackDropImage)
            layoutId = R.layout.movie_list_item_withtitle;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        TMDBMovie movie = mMovieList.get(position);
        holder.movieTitle.setText(movie.getTitle());

        Log.d(TAG, "onBindViewHolder: IS BACKDROP:"+ (mWithBackDropImage ?"YES":"NO"));
        //URL poster_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getPosterPath(), "w185");
        //URL backdrop_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getBackdropPath(), "w300");

        URL image_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getPosterPath(), "w185");
        if (mWithBackDropImage)
            image_path = TMDBUtils.buildAPIUrl_PosterImage(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getBackdropPath(), "w300");

        Glide.with(mContext)
                .load(image_path)
                .apply(fitCenterTransform())
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null)
            return 0;
        return mMovieList.size();
    }



    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView moviePoster;
        final TextView movieTitle;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);

            moviePoster = (ImageView) itemView.findViewById(R.id.iv_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.tv_movietitle);

            Typeface font = Typeface.createFromAsset(mContext.getAssets(), FONT_MOVIEPOSTER);
            movieTitle.setTypeface(font);

            movieTitle.setVisibility(mWithBackDropImage ?View.VISIBLE:View.INVISIBLE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // TODO: 21/02/2018
            int pos = getAdapterPosition();
            TMDBMovie movie = mMovieList.get(pos);
            mClickHandler.onClick(movie);
        }
    }
}

