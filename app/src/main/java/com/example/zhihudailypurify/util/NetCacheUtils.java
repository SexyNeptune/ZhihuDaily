package com.example.zhihudailypurify.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class NetCacheUtils {

    private SDCardCacheUtils mSDCardCacheUtils;

    private MemoryCacheUtils mMemoryCacheUtils;


    public NetCacheUtils(SDCardCacheUtils sdcardCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mSDCardCacheUtils = sdcardCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    public void getFromNet(ImageView image,String url){
        new MyAsyncTask().execute(image,url);
    }

    /**
     * 异步下载
     * <p/>
     * 第一个泛型 ： 参数类型  对应doInBackground（）
     * 第二个泛型 ： 更新进度   对应onProgressUpdate（）
     * 第三个泛型 ： 返回结果result   对应onPostExecute
     */
    class MyAsyncTask extends AsyncTask<Object, Void, Bitmap> {

        private ImageView mImageView;

        private String mUrl;

        /**
         * 后台下载  子线程
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object... params) {

            //拿到传入的image
            mImageView = (ImageView) params[0];

            //得到图片的地址
            mUrl = (String) params[1];

            Bitmap bitmap = HttpUtil.getImageBitmap(mUrl);

            return bitmap;
        }


        /**
         * 进度更新   UI线程
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 回调结果，耗时方法结束后，主线程
         *
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.e("MyBitmapUtils", "我是从网络缓存中读取的图片啊");
                mImageView.setImageBitmap(bitmap);
                /**
                 * 当从网络上下载好之后保存到sdcard中
                 */
                mSDCardCacheUtils.saveToSDCard(mUrl, bitmap);
                /**
                 *  写入到内存中
                 */
                mMemoryCacheUtils.setToMemory(mUrl, bitmap);

//                Bitmap b = mMemoryCacheUtils.getFromMemory(mUrl);
//                if (b != null){
//                    mImageView.setImageBitmap(b);
//                }else{
//                    mImageView.setImageBitmap(bitmap);
//                }
            }
        }
    }

}
