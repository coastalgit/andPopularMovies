package com.bf.popularmovies.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bf.popularmovies.R;
import com.bf.popularmovies.model.TMDBReview;

import java.util.ArrayList;

/*
 * @author frielb 
 * Created on 26/03/2018
 */

public class ReviewsAdapter extends BaseAdapter {

    private ArrayList<TMDBReview> mReviewList;
    private final Context mContext;

    public ReviewsAdapter(Context context) {
        this.mContext = context;
    }

    public void reloadAdapter(ArrayList<TMDBReview> reviews){
        this.mReviewList = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mReviewList == null)
            return 0;

        return mReviewList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int layoutId = R.layout.review_list_item;

        view = LayoutInflater.from(mContext).inflate(layoutId,null);

        TextView tvAuthor = view.findViewById(R.id.tv_review_author);
        TextView tvBody = view.findViewById(R.id.tv_review_bodytext);

        if ((mReviewList != null) && (mReviewList.size() > 0)) {
            TMDBReview review = mReviewList.get(i);
            tvAuthor.setText(TextUtils.isEmpty(review.getAuthor())? mContext.getString(R.string.anon) : review.getAuthor());
            tvBody.setText(TextUtils.isEmpty(review.getContent())? mContext.getString(R.string.anon) : review.getContent());

        }
        else{
            tvAuthor.setText("");
            tvBody.setText(mContext.getString(R.string.availablenot));
        }

        return view;
    }
}
