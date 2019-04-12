package com.example.zhihudailypurify.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class BitmapUtils {

    /**
     * 网络缓存
     */
    public NetCacheUtils mNetCacheUtils;

    /**
     * 本地缓存
     */
    public SDCardCacheUtils mSdCardCacheUtils;

    /**
     * 内存缓存
     */
    public MemoryCacheUtils mMemoryCacheUtils;

    public BitmapUtils(Context context) {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mSdCardCacheUtils = new SDCardCacheUtils(context);
        mNetCacheUtils = new NetCacheUtils(mSdCardCacheUtils,mMemoryCacheUtils);
    }

    public void setImage(ImageView image, String url){

        //从内存中读取
        Bitmap fromMemory = mMemoryCacheUtils.getFromMemory(url);
        if (fromMemory != null){
            image.setImageBitmap(fromMemory);
            Log.e("MyBitmapUtils","读取到内存中有缓存，从内存中读取。。。");
            return;
        }
        //从本地SD卡中读取
        Bitmap fromSdCard = mSdCardCacheUtils.getFromSDCard(url);
        if (fromSdCard != null) {
            Log.e("MyBitmapUtils", "读取到本地中有缓存，从本地中读取。。。");
            image.setImageBitmap(fromSdCard);
            mMemoryCacheUtils.setToMemory(url,fromSdCard);
            return;
        }
        //从网络读取
        mNetCacheUtils.getFromNet(image,url);
    }
}
