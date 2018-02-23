package com.bf.popularmovies.ui;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.utility.TMDBUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.net.URL;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_MOVIEPOSTER;
import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String KEY_MOVIE = "key_movie";
    public final static String KEY_LANG = "key_lang";

    TMDBMovie mMovie;
    Enums.LanguageLocale mLang;

    @BindView(R.id.tv_detail_movietitle) TextView mMovieTitle;
    @BindView(R.id.tv_detail_sub_title) TextView mMovieTitleSub;
    @BindView(R.id.tv_detail_bodytext) TextView mMovieBodyText;
    @BindView(R.id.tv_detail_footer_rating) TextView mMovieRating;
    @BindView(R.id.tv_detail_bodycaption) TextView mMovieBodyCaption;

    @BindView(R.id.iv_detail_moviebackdropimage) ImageView mMovieImageBackdrop;
    @BindView(R.id.iv_detail_movieposterimage) ImageView mMovieImagePoster;

    //@BindView(R.id.layout_poster) FrameLayout mLayoutPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        ButterKnife.bind(this);

        mLang = (Enums.LanguageLocale) getIntent().getSerializableExtra(KEY_LANG);
        if (mLang == Enums.LanguageLocale.PORTUGUESE) {
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                config.locale = new Locale.Builder().setLanguageTag(mLang.toString()).build();
                getResources().updateConfiguration(config, resources.getDisplayMetrics());
            }
        }

        mMovie = (TMDBMovie) getIntent().getSerializableExtra(KEY_MOVIE);
        if (mMovie != null) {
            Log.d(TAG, "onCreate: MOVIE:"+mMovie.getTitle());
            buildView();
        }
    }

    private void buildView(){

        mMovieTitle.setText(mMovie.getTitle());
        Typeface font = Typeface.createFromAsset(this.getAssets(), FONT_MOVIEPOSTER);
        mMovieTitle.setTypeface(font);

        font = Typeface.createFromAsset(this.getAssets(), FONT_TITILLIUM_REGULAR);
        mMovieTitleSub.setText(mMovie.getTitle());
        mMovieTitleSub.setTypeface(font);

        mMovieBodyCaption.setText(getString(R.string.released) + " " + mMovie.getReleaseDate());
        mMovieBodyCaption.setTypeface(font);

        mMovieBodyText.setText(mMovie.getOverview());
        mMovieBodyText.setTypeface(font);

        //mMovieRating.setText(String.format("%s %d", getString(R.string.rating), mMovie.getVoteAverage()));
        mMovieRating.setText(getString(R.string.rating) + " " + String.valueOf(mMovie.getVoteAverage()));
        mMovieRating.setTypeface(font);


        URL image_poster = TMDBUtils.buildAPIUrl_Image(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), mMovie.getPosterPath(), "w185");
        URL image_backdrop = TMDBUtils.buildAPIUrl_Image(TMDBManager.getInstance().getTMDBSysConfig().getImages().getBaseUrl(), mMovie.getBackdropPath(), "w300");

        Glide.with(this)
                .load(image_backdrop)
                .apply(fitCenterTransform())
                .into(mMovieImageBackdrop);
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        mLayoutPoster.setBackground(resource);
//                    }
//                });



        Glide.with(this)
                .load(image_poster)
                .apply(fitCenterTransform())
                .into(mMovieImagePoster);

        mMovieImagePoster.bringToFront();


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup.LayoutParams imageparams = mMovieImageBackdrop.getLayoutParams();
//                ViewGroup.LayoutParams overlayparams = mLayoutPosterOverlay.getLayoutParams();
//                Log.d(TAG, "buildView: IMAGE H:"+String.valueOf(imageparams.height));
//                Log.d(TAG, "buildView: OVERLAY H:"+String.valueOf(overlayparams.height));
//                overlayparams.height = imageparams.height;
//                mLayoutPosterOverlay.setLayoutParams(overlayparams);
//            }
//        });

    }
}
