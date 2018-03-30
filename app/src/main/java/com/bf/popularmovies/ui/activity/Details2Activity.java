package com.bf.popularmovies.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.adapter.DetailSectionsPagerAdapter;
import com.bf.popularmovies.db.MovieDbTransactionManager;
import com.bf.popularmovies.db.MovieDbTransactionManager.onDbTransactionHandler;
import com.bf.popularmovies.manager.TMDBManager;
import com.bf.popularmovies.model.TMDBMovie;
import com.bf.popularmovies.ui.controls.FixedViewPager;
import com.bf.popularmovies.utility.TMDBUtils;
import com.bumptech.glide.Glide;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bf.popularmovies.common.Constants.FONT_MOVIEPOSTER;
import static com.bf.popularmovies.common.Constants.FONT_TITILLIUM_REGULAR;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

@SuppressWarnings({"deprecation", "unused", "ConstantConditions"})
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
    private MovieDbTransactionManager mDbTransactionManager;

    private ViewPager mViewPager;

    @BindView(R.id.tv_detail_movietitle)
    TextView mMovieTitle;
    @BindView(R.id.tv_detail_sub_title)
    TextView mMovieTitleSub;
    @BindView(R.id.tv_detail_footer_rating)
    TextView mMovieRating;

    @BindView(R.id.iv_detail_moviebackdropimage)
    ImageView mMovieImageBackdrop;
    @BindView(R.id.iv_detail_movieposterimage)
    ImageView mMovieImagePoster;

    @BindView(R.id.layout_cardposter)
    CardView mCardViewPoster;

    @BindView(R.id.imageButtonFaves)
    ImageButton mBtnFaves;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        ButterKnife.bind(this);

        mDbTransactionManager = new MovieDbTransactionManager(this, new onDbTransactionHandler() {
            @Override
            public void onFaveMovieAdded_Ok() {
                Toast.makeText(Details2Activity.this, R.string.addedtofaves, Toast.LENGTH_SHORT).show();
                toggleBtnFaves(true);
            }

            @Override
            public void onFaveMovieAdded_Fail(String errorMsg) {
                Toast.makeText(Details2Activity.this, errorMsg, Toast.LENGTH_SHORT).show();
                toggleBtnFaves(false);
            }

            @Override
            public void onFaveMovieRemoved() {
                Toast.makeText(Details2Activity.this, R.string.removedfromfaves, Toast.LENGTH_SHORT).show();
                toggleBtnFaves(false);
            }
        });

        mMovie = (TMDBMovie) getIntent().getSerializableExtra(KEY_MOVIE);
        if (mMovie != null) {
            Log.d(TAG, "onCreate: MOVIE:"+mMovie.getTitle());
            buildView();
            toggleBtnFaves(mDbTransactionManager.isMovieAFavourite(mMovie.getId()));
        }

        buildTabViewPager();
    }

// Not required, as we can re-use initial intent
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState");
//        super.onSaveInstanceState(outState);
//        outState.putSerializable(KEY_MOVIE,mMovie);
//    }

    private void buildTabViewPager(){
        //mDetailsSectionsPagerAdapter = new DetailSectionsPagerAdapter(getSupportFragmentManager(), Details2Activity.this);
        DetailSectionsPagerAdapter mDetailsSectionsPagerAdapter = new DetailSectionsPagerAdapter(getSupportFragmentManager(), mMovie);

        mViewPager = (FixedViewPager) findViewById(R.id.viewpager_sections);
        mViewPager.setAdapter(mDetailsSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabsSections);

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
        if (mCardViewPoster != null){
            switch (mViewPager.getCurrentItem()){
                case TABINDEX_OVERVIEW:
                    mCardViewPoster.animate()
                            //.translationX(mCardViewPoster.getHeight())
                            .alpha(1.0f)
                            .setDuration(400);
                    break;
                case TABINDEX_VIDEOS:
                case TABINDEX_REVIEWS:
                    mCardViewPoster.animate()
                            //.translationX(mCardViewPoster.getWidth())
                            .alpha(0.0f)
                            .setDuration(400);
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


    }

    private void handleFavesClick(){
        if (mDbTransactionManager.isMovieAFavourite(mMovie.getId())){
            mDbTransactionManager.doRemoveMovieFromFavorites(mMovie.getId());
        }
        else{
            mDbTransactionManager.doAddMovieToFavorites(mMovie);
        }
    }
    private void toggleBtnFaves(boolean enabled){
        mBtnFaves.setImageResource(enabled ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
    }

    @OnClick(R.id.imageButtonFaves)
    public void btnFaves_onClick(ImageButton btn){
        handleFavesClick();
    }

}
