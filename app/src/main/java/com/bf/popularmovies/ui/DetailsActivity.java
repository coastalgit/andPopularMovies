package com.bf.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.model.TMDBMovie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String KEY_MOVIE = "key_movie";

    TMDBMovie mMovie;

    @BindView(R.id.tvdetail_movietitle) TextView mMovieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ButterKnife.bind(this);

        mMovie = (TMDBMovie)getIntent().getSerializableExtra(KEY_MOVIE);
        if (mMovie != null) {
            Log.d(TAG, "onCreate: MOVIE:"+mMovie.getTitle());
            mMovieTitle.setText(mMovie.getTitle());
        }
    }
}
