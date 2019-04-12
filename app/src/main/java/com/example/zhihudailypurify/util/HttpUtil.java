package com.example.zhihudailypurify.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.zhihudailypurify.bean.ExtraData;
import com.example.zhihudailypurify.bean.News;
import com.example.zhihudailypurify.bean.NewsContent;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static void sendOkHttpRequest(String address , okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }


    public static Bitmap getImageBitmap(String url) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
//            connection.setRequestProperty("User-agent",System.getProperty("http.agent"));
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            InputStream is = connection.getInputStream();
//            InputStream is = imgUrl.openStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
             } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
            }finally {
            if(connection != null){
                connection.disconnect();
                }
            }
        return bitmap;
    }

//    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(8000);
//                    connection.setReadTimeout(8000);
//                    InputStream in = connection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    if (listener != null) {
//                        // 回调onFinish()方法
//                        listener.onFinish(response.toString());
//                    }
//                } catch (Exception e) {
//                    if (listener != null) {
//                        // 回调onError()方法
//                        listener.onError(e);
//                    }
//                } finally {
//                    if (connection != null) {
//                        connection.disconnect();
//                    }
//                }
//            }
//        }).start();
//    }
}
