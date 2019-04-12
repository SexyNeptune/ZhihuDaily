package com.example.zhihudailypurify.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhihudailypurify.activity.NewsContentActivity;
import com.example.zhihudailypurify.R;

import java.util.ArrayList;
import java.util.List;

import com.example.zhihudailypurify.bean.Story;
import com.example.zhihudailypurify.util.ImageUtils;

public class MyRecyclerAdapter extends RecyclerView.Adapter {

    //布局类型
    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private ArrayList<Story> storyList;
    private Context mContext;
    private ImageUtils imageUtils;


    public MyRecyclerAdapter(List<Story> storyList, Context context) {
        this.storyList = (ArrayList<Story>) storyList;
        mContext = context;
        imageUtils = new ImageUtils(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_ONE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
                holder = new StoryViewHolder(view);
                break;
            case VIEW_TYPE_TWO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_date, parent, false);
                holder = new DateViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ONE:
                ((StoryViewHolder)holder).setPosition(position);
                final String imgUrl = storyList.get(position).getImgUrl();
//                final Handler handler = new Handler(new Handler.Callback() {
//                    @Override
//                    public boolean handleMessage(Message message) {
//                        switch(message.what){
//                            case 1:
//                                ((StoryViewHolder)holder).imageView.setImageBitmap((Bitmap) message.obj);
//                        }
//                        return false;
//                    }
//                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = HttpUtil.getImageBitmap(imgUrl);
//                        Message message = new Message();
//                        message.what = 1;
//                        message.obj = bitmap;
//                        handler.sendMessage(message);
//                    }
//                }).start();
                imageUtils.display(((StoryViewHolder)holder).imageView,imgUrl);
                ((StoryViewHolder)holder).title.setText(storyList.get(position).getTitle());
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


    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView date;

        public DateViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.recyclerview_date);
        }
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;
        private int position;

        public StoryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.story_title);
            imageView = itemView.findViewById(R.id.story_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //传递列表数据 -- 序列化对象
                    Intent intent = new Intent(mContext, NewsContentActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("storylist",storyList);
                    intent.putExtras(bundle);
                    intent.putExtra("position",position);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
        public void setPosition(int i){
            position = i;
        }
    }

}
