package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import db.NewsContent;
import okhttp3.HttpUrl;
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
        List<Integer> ids = new ArrayList<>();
        List<Integer> tids = new ArrayList<>();
        try{
            JSONObject object = new JSONObject(response);
            int date = object.getInt("date");
            JSONArray stories = object.getJSONArray("stories");
            for (int i=0;i<stories.length(); i++){
                JSONObject story = stories.getJSONObject(i);
                String title = story.getString("title");
                String imgUri = story.getJSONArray("images").getString(0);
                int id = story.getInt("id");
                titles.add(title);
                imgUris.add(imgUri);
                ids.add(id);
            }
            JSONArray tStories = object.getJSONArray("top_stories");
            for (int i=0;i<tStories.length(); i++){
                JSONObject tStory = tStories.getJSONObject(i);
                String title = tStory.getString("title");
                String imgUri = tStory.getString("image");
                int id = tStory.getInt("id");
                ttitles.add(title);
                tImgUris.add(imgUri);
                tids.add(id);
            }
            news.setDate(date);
            news.setStoryImgUri(imgUris);
            news.settStoryImgUri(tImgUris);
            news.setStoryTitles(titles);
            news.settStoryTitles(ttitles);
            news.setIds(ids);
            news.settIds(tids);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return news;
    }

    public static db.News handleHistoryNewsResponse(String response){
        db.News news = new db.News();
        List<String> titles = new ArrayList<>();
        List<String> imgUris = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        try{
            JSONObject object = new JSONObject(response);
            int date = object.getInt("date");
            JSONArray stories = object.getJSONArray("stories");
            for (int i=0;i<stories.length(); i++){
                JSONObject story = stories.getJSONObject(i);
                String title = story.getString("title");
                String imgUri = story.getJSONArray("images").getString(0);
                int id = story.getInt("id");
                titles.add(title);
                imgUris.add(imgUri);
                ids.add(id);
            }
            news.setDate(date);
            news.setStoryImgUri(imgUris);
            news.setStoryTitles(titles);
            news.setIds(ids);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return news;
    }

    public static NewsContent handleNewsContentResponse(String response) {
        NewsContent content = new NewsContent();
        try{
            JSONObject object = new JSONObject(response);
            String body = object.getString("body");
            String title = object.getString("title");
            String image = object.getString("image");
            int id = object.getInt("id");
            String imageSource = object.getString("image_source");
            content.setBody(body);
            content.setTitle(title);
            content.setImage(image);
            content.setId(id);
            content.setImageResouce(imageSource);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  content;
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
