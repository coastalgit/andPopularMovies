package com.bf.popularmovies.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.ui.controls.FixedViewPager;
import com.bf.popularmovies.utility.TMDBUtils;
import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bf.popularmovies.common.Constants.FONT_MOVIEPOSTER;
import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

public class Details2Activity extends AppCompatActivity {

    private static final String TAG = Details2Activity.class.getSimpleName();
    public final static String KEY_MOVIE = "key_movie";

    private final static int TABINDEX_OVERVIEW = 0;
    private final static int TABINDEX_VIDEOS = 1;
    private final static int TABINDEX_REVIEWS = 2;

    private TMDBMovie mMovie;
    public TMDBMovie getMovie() {
        return mMovie;
    }

    private DetailSectionsPagerAdapter mDetailsSectionsPagerAdapter;
    private ViewPager mViewPager;

    @BindView(R.id.tv_detail_movietitle)
    TextView mMovieTitle;
    @BindView(R.id.tv_detail_sub_title)
    TextView mMovieTitleSub;
//    @BindView(R.id.tv_detail_bodytext)
//    TextView mMovieBodyText;
    @BindView(R.id.tv_detail_footer_rating)
    TextView mMovieRating;
    @BindView(R.id.tv_detail_bodycaption)
    TextView mMovieBodyCaption;
    @BindView(R.id.tv_detail_bodycaption2)
    TextView mMovieBodyCaption2;

    @BindView(R.id.iv_detail_moviebackdropimage)
    ImageView mMovieImageBackdrop;
    @BindView(R.id.iv_detail_movieposterimage)
    ImageView mMovieImagePoster;

    @BindView(R.id.layout_cardposter)
    CardView mCardViewPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        ButterKnife.bind(this);

        mMovie = (TMDBMovie) getIntent().getSerializableExtra(KEY_MOVIE);
        if (mMovie != null) {
            Log.d(TAG, "onCreate: MOVIE:"+mMovie.getTitle());
            buildView();
        }

        if (savedInstanceState == null) {
            buildTabViewPager();
        }

    }

    private void buildTabViewPager(){
        //mDetailsSectionsPagerAdapter = new DetailSectionsPagerAdapter(getSupportFragmentManager(), Details2Activity.this);
        mDetailsSectionsPagerAdapter = new DetailSectionsPagerAdapter(getSupportFragmentManager(), mMovie);

        mViewPager = (FixedViewPager) findViewById(R.id.viewpager_sections);
        mViewPager.setAdapter(mDetailsSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsSections);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                animatePosterVisibility();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void animatePosterVisibility(){
        if (mMovieImagePoster != null){
            switch (mViewPager.getCurrentItem()){
                case TABINDEX_OVERVIEW:
                    mCardViewPoster.animate()
                            //.translationX(mCardViewPoster.getHeight())
                            .alpha(1.0f)
                            .setDuration(300);
                    break;
                case TABINDEX_VIDEOS:
                case TABINDEX_REVIEWS:
                    mCardViewPoster.animate()
                            //.translationX(mCardViewPoster.getWidth())
                            .alpha(0.0f)
                            .setDuration(300);
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void buildView(){

        mMovieTitle.setText(mMovie.getOriginalTitle());
        Typeface font = Typeface.createFromAsset(this.getAssets(), FONT_MOVIEPOSTER);
        mMovieTitle.setTypeface(font);

        font = Typeface.createFromAsset(this.getAssets(), FONT_TITILLIUM_REGULAR);
        mMovieTitleSub.setText(mMovie.getTitle());
        mMovieTitleSub.setTypeface(font);

        String captionStr = getString(R.string.released) + " " + mMovie.getReleaseDate();
        if (mMovie.getOriginalLanguage().length() > 0)
            captionStr = captionStr + " ("+mMovie.getOriginalLanguage().toUpperCase() +")";
        mMovieBodyCaption.setText(captionStr);
        mMovieBodyCaption.setTypeface(font);

//        mMovieBodyText.setText(mMovie.getOverview());
//        mMovieBodyText.setTypeface(font);

        //mMovieRating.setText(String.format("%s %d", getString(R.string.rating), mMovie.getVoteAverage()));
        mMovieRating.setText(getString(R.string.rating) + " " + String.valueOf(mMovie.getVoteAverage()));
        mMovieRating.setTypeface(font);

        String genreCaption = getString(R.string.genre) + ": " + getString(R.string.unknown);
        ArrayList<String> genresList = TMDBUtils.buildGenreStringListById(TMDBManager.getInstance().getTMDBGenres(),mMovie.getGenreIds());
        if (genresList != null && genresList.size()>0)
            genreCaption = getString(R.string.genre) + ": " + TextUtils.join(", ",genresList);
        mMovieBodyCaption2.setText(genreCaption);
        mMovieBodyCaption2.setTypeface(font);


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

    }


}
