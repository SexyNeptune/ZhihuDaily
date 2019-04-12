package com.example.zhihudailypurify.model;

import com.example.zhihudailypurify.bean.News;
import com.example.zhihudailypurify.bean.Story;
import com.example.zhihudailypurify.bean.TopStory;

import java.util.List;

public interface NewsStoryCallbackListener {
    void onResponse(List<Story>  stories, List<TopStory>topStories);
    void onFailure();
}
