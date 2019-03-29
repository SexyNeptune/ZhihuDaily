package com.example.zhihudailypurify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utils.HttpUtil;
import db.Tstory;

class MyPagerAdapter extends PagerAdapter {
    private List<Tstory> storyList ;
    private Context mContext;

    public MyPagerAdapter(List<Tstory> tstoryList, Context context) {
        storyList = tstoryList;
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
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0 :
                        imageView.setImageBitmap((Bitmap) message.obj);
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = HttpUtil.getImageBitmap(storyList.get(position).getImgUri());
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj= bitmap;
                handler.sendMessage(msg);
            }
        }).start();
        container.addView(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "你点击了第" + position +"条Banner", Toast.LENGTH_SHORT).show();
                int id = storyList.get(position).getId();
                //get到了，Context启动Activity的话要添加一个FLAG
                Intent intent = new Intent(mContext,NewsContentActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //好像又不用了。。。
                intent.putExtra("id",id);
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
