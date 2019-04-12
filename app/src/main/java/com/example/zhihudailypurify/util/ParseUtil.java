package com.example.zhihudailypurify.util;

import com.example.zhihudailypurify.bean.ExtraData;
import com.example.zhihudailypurify.bean.News;
import com.example.zhihudailypurify.bean.NewsContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {

    public static News handleNewsResponse(String response){
        News news = new News();
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

    public static News handleHistoryNewsResponse(String response){
        News news = new News();
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

    public static ExtraData handleExtraNewsResponse (String response){
        ExtraData extraData = new ExtraData();
        try {
            JSONObject object = new JSONObject(response);
            int popularity = object.getInt("popularity");
            int comments = object.getInt("comments");
            extraData.setComments(comments);
            extraData.setPopularity(popularity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return extraData;
    }

}
