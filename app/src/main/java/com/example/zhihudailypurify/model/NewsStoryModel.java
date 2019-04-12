package com.example.zhihudailypurify.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.zhihudailypurify.util.HttpUtil;
import com.example.zhihudailypurify.bean.News;
import com.example.zhihudailypurify.bean.Story;
import com.example.zhihudailypurify.bean.TopStory;
import com.example.zhihudailypurify.util.ParseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class NewsStoryModel implements NewsModel{

    private Context context;

    private NewsStoryCallbackListener newsStoryCallbackListener;
    /**
     * 装viewPager数据的集合
     */
    private List<TopStory> topStoryList = new ArrayList<>();

    /**
     * 装recyclerView数据的集合
     */
    private List<Story> storyList = new ArrayList<>();

    public NewsStoryModel(Context context, NewsStoryCallbackListener newsStoryCallbackListener){
        this.context = context;
        this.newsStoryCallbackListener = newsStoryCallbackListener;
    }

    @Override
    public void request() {
        requestLatestNews();
    }

    /**
     * 本地没有保存时联网申请数据
     */
    private void requestLatestNews(){
        String latestUrl="https://news-at.zhihu.com/api/4/news/latest";
        HttpUtil.sendOkHttpRequest(latestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               newsStoryCallbackListener.onFailure();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                News news = ParseUtil.handleNewsResponse(responseText);
                refreshData(news);
                Log.e("NewsStoryModel","申请到的今日热闻数据：         "+ news.toString());
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("news" ,responseText);
                editor.apply();
            }
        });
    }

    /**
     * 网络请求历史新闻
     * @param date 历史日期
     */
    public void requestHistoryNews(int date){
        String latestUrl="https://news-at.zhihu.com/api/4/news/before/" + String.valueOf(date);
        HttpUtil.sendOkHttpRequest(latestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                newsStoryCallbackListener.onFailure();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                final News news = ParseUtil.handleHistoryNewsResponse(responseText);
                Log.e("NewsStoryModel","申请到的历史新闻数据：         "+ news.toString());
                refreshData(news);
            }
        });
    }

    /**
     * 刷新数据
     * @param news 解析json的对象
     */
    public void refreshData(News news) {
        storyList.clear();
        topStoryList.clear();
        addTopStoryData(news);
        addStoryData(news);
        //返回数据
        newsStoryCallbackListener.onResponse(storyList,topStoryList);
    }

    /**
     * 将数据添加到topStoryList中
     * @param news 解析json数据返回的对象，包括date，tStory，Story
     */
    private void addTopStoryData(News news) {
        if(news.gettStoryTitles()!=null){
            for(int i =0; i<news.gettStoryTitles().size();i++){
                //添加到viewPager数据集合中
                TopStory topStory = new TopStory();
                topStory.setImgUri(news.gettStoryImgUri().get(i));
                topStory.setTitle(news.gettStoryTitles().get(i));
                topStory.setId(news.gettIds().get(i));
                topStoryList.add(topStory);
            }
        }
    }

    /**
     * 将数据添加到storyList中
     * @param news 解析json数据返回的对象，包括date，tStory，Story
     */
    private void addStoryData(News news) {
        int historyDate = news.getDate();
        storyList.add(new Story(String.valueOf(historyDate),null,0));
        for(int i =0 ; i<news.getStoryImgUri().size();i++){
            //添加到RecyclerView的数据集合中
            Story story = new Story();
            story.setImgUrl(news.getStoryImgUri().get(i));
            story.setTitle(news.getStoryTitles().get(i));
            story.setId(news.getIds().get(i));
            story.setType(1);
            storyList.add(story);
        }
    }
}
