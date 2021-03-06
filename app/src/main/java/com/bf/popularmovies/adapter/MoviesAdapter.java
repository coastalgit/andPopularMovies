package com.bf.popularmovies.adapter;

/*
 * @author frielb
 * Created on 21/02/2018
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.utility.TMDBUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.bf.popularmovies.common.Constants.FONT_MOVIEPOSTER;

import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private ArrayList<TMDBMovie> mMovieList;
    private boolean mWithBackDropImage = false;

    public interface MoviesAdapterOnClickHandler {
        void onClick(TMDBMovie movie);
    }

    public MoviesAdapter(@NonNull Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public void reloadAdapter(ArrayList<TMDBMovie> movies, boolean withBackdropImage){
        this.mMovieList = movies;
        this.mWithBackDropImage = withBackdropImage;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_list_item_notitle;
        if (mWithBackDropImage)
            layoutId = R.layout.movie_list_item_withtitle;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesAdapterViewHolder holder, int position) {
        TMDBMovie movie = mMovieList.get(position);
        //String title = TextUtils.isEmpty(movie.getTitle()) ? "UNKNOWN" : movie.getTitle();
        holder.movieTitle.setText(movie.getTitle());
        //holder.movieTitle.setText(title);
        //Log.d(TAG, "onBindViewHolder: IS BACKDROP:"+ (mWithBackDropImage ?"YES":"NO"));
        /// TODO: 23/02/2018 Dynamic determination of poster size
        if (TMDBManager.getInstance().getTMDBSysConfig() != null) {
            URL image_path = TMDBUtils.buildAPIUrl_Image(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getPosterPath(), "w185");
            if (mWithBackDropImage)
                image_path = TMDBUtils.buildAPIUrl_Image(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), movie.getBackdropPath(), "w300");

// 06/04/2018 - replaced Glide as it was breaking reinstatement of scroll view position on orientation change!
//            Glide.with(mContext)
//                    .load(image_path)
//                    .apply(fitCenterTransform())
//                    .into(holder.moviePoster);

            assert image_path != null;
            Picasso.with(mContext)
                    .load(image_path.toString())
                    .into(holder.moviePoster,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                        holder.moviePoster.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {
                                    // TODO: 06/04/2018 Display a friendlier image?
                                    holder.moviePoster.setVisibility(View.INVISIBLE);
                                    holder.movieTitle.setVisibility(View.INVISIBLE);
                                }
                            }
                    );

        }
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

            moviePoster = itemView.findViewById(R.id.iv_poster);
            movieTitle = itemView.findViewById(R.id.tv_movietitle);

            Typeface font = Typeface.createFromAsset(mContext.getAssets(), FONT_MOVIEPOSTER);
            movieTitle.setTypeface(font);

            movieTitle.setVisibility(mWithBackDropImage ?View.VISIBLE:View.INVISIBLE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            TMDBMovie movie = mMovieList.get(pos);
            mClickHandler.onClick(movie);
        }
    }
}

