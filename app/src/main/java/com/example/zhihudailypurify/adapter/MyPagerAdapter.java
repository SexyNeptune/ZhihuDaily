package com.example.zhihudailypurify.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.zhihudailypurify.activity.NewsContentActivity;

import java.util.ArrayList;
import java.util.List;

import com.example.zhihudailypurify.util.HttpUtil;
import com.example.zhihudailypurify.bean.TopStory;
import com.example.zhihudailypurify.util.ImageUtils;

public class MyPagerAdapter extends PagerAdapter {
    private ArrayList<TopStory> storyList ;
    private Context mContext;

    public MyPagerAdapter(List<TopStory> topStoryList, Context context) {
        storyList = (ArrayList<TopStory>)topStoryList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return storyList.size() ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(container.getContext());
//        final Handler handler = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message message) {
//                switch (message.what) {
//                    case 0 :
//                        imageView.setImageBitmap((Bitmap) message.obj);
//                        break;
//                }
//                return false;
//            }
//        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap bitmap = HttpUtil.getImageBitmap(storyList.get(position).getImgUri());
//                Message msg = Message.obtain();
//                msg.what = 0;
//                msg.obj= bitmap;
//                handler.sendMessage(msg);
//            }
//        }).start();
        new ImageUtils(mContext).display(imageView,storyList.get(position).getImgUri());
        container.addView(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //传递列表数据 -- 序列化对象
                Intent intent = new Intent(mContext, NewsContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("topstorylist",storyList);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
