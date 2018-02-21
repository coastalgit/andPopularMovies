package com.bf.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bf.popularmovies.R;
import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.presenter.ImdbMoviesPresenterImpl;
import com.bf.popularmovies.presenter.MVP_ImdbMovies;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MVP_ImdbMovies.IView {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MVP_ImdbMovies.IPresenter mPresenter;

    @BindView(R.id.buttonTestAPI) Button mBtnTestAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        attachIMDBPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    private void attachIMDBPresenter(){
        mPresenter = (MVP_ImdbMovies.IPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null)
            mPresenter = new ImdbMoviesPresenterImpl(getString(R.string.api_key), this);

        mPresenter.attachView(this);
    }

    private void testAPI(){
        mPresenter.getImdbMoviesByPopularity(Enums.LanguageLocale.ENGLISH, 1);
    }

    //region Butterknife listeners
    @OnClick(R.id.buttonTestAPI)
    public void mBtnTestAPI_OnClick(Button btn) {
        testAPI();
    }
    //endregion

    //region IView methods
    @Override
    public void logMessageToView(String msg) {
        Log.d(TAG, "logMessageToView: ["+msg+"]");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIMDBResponse_OK() {

    }

    @Override
    public void onIMDBResponse_Error(String errorMsg) {

    }

    @Override
    public void onErrorConnection(String errorMsg) {

    }
    //endregion
}
