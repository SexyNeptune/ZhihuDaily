package com.example.zhihudailypurify.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SDCardCacheUtils {

    private Context mContext;

    public SDCardCacheUtils(Context context){
        mContext = context;
    }

    public void saveToSDCard(String url , Bitmap bitmap){
        File file = getFile(url);
        //我们首先得到他的父目录
       File parentFile = file.getParentFile();
       //查看是否存在，如果不存在就创建
       if (!parentFile.exists()){
           parentFile.mkdirs(); //创建文件夹
       }
       try {
           //将图片保存到本地
           /**
            * @param format   The format of the compressed image   图片的保存格式
            * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
            *                 small size, 100 meaning compress for max quality. Some
            *                 formats, like PNG which is lossless, will ignore the
            *                 quality setting
            *                 图片的保存的质量    100最好
            * @param stream   The outputstream to write the compressed data.
            */
           bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
        Log.e("MyBitmapUtils","缓存到本地目录中...");
    }

   public Bitmap getFromSDCard(String url){
       File file = getFile(url);
       //如果存在，就通过bitmap工厂，返回的bitmap，然后返回bitmap
        if (file.exists()){
           try {
               Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
               return bitmap;
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
        }
       return null;
   }

    @NonNull
    private File getFile(String url) {
        String fileName = MD5Encoder.encode(url);
        String directory = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + mContext.getPackageName() + "/icon";
        return new File(directory, fileName);
    }


}
