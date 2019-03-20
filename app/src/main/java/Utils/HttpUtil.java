package Utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.zhihudailypurify.MainActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gson.News;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {



    public static void sendOkHttpRequest(String address , okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static db.News handleNewsResponse(String response){
        db.News news = new db.News();
        List<String> titles = new ArrayList<>();
        List<String> ttitles = new ArrayList<>();
        List<String> imgUris = new ArrayList<>();
        List<String> tImgUris= new ArrayList<>();
        try{
            JSONObject object = new JSONObject(response);
            int date = object.getInt("date");
            JSONArray stories = object.getJSONArray("stories");
            for (int i=0;i<stories.length(); i++){
                JSONObject story = stories.getJSONObject(i);
                String title = story.getString("title");
                String imgUri = story.getJSONArray("images").getString(0);
                titles.add(title);
                imgUris.add(imgUri);
            }
            JSONArray tStories = object.getJSONArray("top_stories");
            for (int i=0;i<tStories.length(); i++){
                JSONObject tStory = tStories.getJSONObject(i);
                String title = tStory.getString("title");
                String imgUri = tStory.getString("image");
                ttitles.add(title);
                tImgUris.add(imgUri);
            }
            news.setDate(date);
            news.setStoryImgUri(imgUris);
            news.settStoryImgUri(tImgUris);
            news.setStoryTitles(titles);
            news.settStoryTitles(ttitles);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return news;
    }

    public static Bitmap getImageBitmap(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish()方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        // 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
