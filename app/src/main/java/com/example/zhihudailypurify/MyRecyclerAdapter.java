package com.example.zhihudailypurify;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import Utils.HttpUtil;
import db.Story;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyRecyclerAdapter extends RecyclerView.Adapter {

//    private List<String> mtitles;
//    private List<String> mimgUrls;

    //布局类型
    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private List<Story> storyList;


    public MyRecyclerAdapter(List<Story> storyList) {
        this.storyList = storyList;
    }

//    public MyRecyclerAdapter(List<String> titles, List<String> imgUrls) {
//        mimgUrls = imgUrls;
//        mtitles = titles;
//    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_ONE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
                holder = new ViewHolder(view);
                break;
            case VIEW_TYPE_TWO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_date, parent, false);
                holder = new DateViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ONE:
                ((ViewHolder)holder).title.setText(storyList.get(position).getTitle());
//                Bitmap bitmap = HttpUtil.getImageBitmap(storyList.get(position).getImgUrl());
                ((ViewHolder)holder).imageView.setBackgroundResource(R.drawable.nav_icon);
                break;
            case VIEW_TYPE_TWO:
                ((DateViewHolder)holder).date.setText(storyList.get(position).getTitle());
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (storyList.get(position).getType() == 1) {
            return VIEW_TYPE_ONE;
        } else if (storyList.get(position).getType() == 0) {
            return VIEW_TYPE_TWO;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView date;

        public DateViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.recyclerview_date);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.story_title);
            imageView = itemView.findViewById(R.id.story_image);
        }

    }
}
