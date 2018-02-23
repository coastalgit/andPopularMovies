package com.bf.popularmovies.task;

/*
 * @author frielb 
 * Created on 23/02/2018
 */

import android.support.annotation.NonNull;

import com.bf.popularmovies.common.Enums;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.utility.JSONUtils;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("ConstantConditions")
public class UpdateTMDBGenresTask {

    private final URL mUrl;
    private final ITMDBGenresResponseHandler mListener;

    public UpdateTMDBGenresTask(URL urlTMDB, ITMDBGenresResponseHandler listener) {
        this.mUrl = urlTMDB;
        this.mListener = listener;
    }

    public void doUpdate(){
        //
        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(mUrl)
                .build();

        client.newCall(req)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        mListener.onTMDBGenresResponse_Error(Enums.TMDBErrorCode.CONNECTION_ERROR, "HTTP error");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            //noinspection Handled in JSONUtils
                            String resp = response.body().string();
                            TMDBGenres genres = JSONUtils.parseTMDBGenresJson(resp);
                            if (genres != null) {
                                mListener.onTMDBGenresResponse_OK(genres);
                            } else {
                                mListener.onTMDBGenresResponse_Error(Enums.TMDBErrorCode.INVALID_DATA, "Cannot parse response");
                            }
                        }
                        else{
                            mListener.onTMDBGenresResponse_Error(Enums.TMDBErrorCode.INVALID_RESPONSE, "Invalid server response");
                        }
                    }
                });
    }

}
