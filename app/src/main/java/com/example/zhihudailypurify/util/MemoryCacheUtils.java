package com.example.zhihudailypurify.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * 内存缓存
 */
public class MemoryCacheUtils {
    private LruCache<String, Bitmap> mLruCache;

    public MemoryCacheUtils(){
        //申请内存空间
        long maxMemory = Runtime.getRuntime().freeMemory();
        int cacheSize = (int) (maxMemory/4);
        //实例化LruCache
        mLruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    /**
     * 读内存
     * @param url
     * @return
     */
    public Bitmap getFromMemory(String url){
        return mLruCache.get(url);
    }

    /**
     * 写内存
     * @param url
     * @param bitmap
     */
    public void setToMemory(String url,Bitmap bitmap){
        if (getFromMemory(url) == null){
            mLruCache.put(url,bitmap);
        }
        Log.e("MyBitmapUtils","缓存到内存中。。。。。");
    }
}
