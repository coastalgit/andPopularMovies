package com.bf.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.buttonTestAPI) Button mBtnTestAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    private void testAPI(){

    }

    //region Butterknife listeners
    @OnClick(R.id.buttonTestAPI)
    public void mBtnTestAPI_OnClick(Button btn) {
        testAPI();
    }
    //endregion
}
