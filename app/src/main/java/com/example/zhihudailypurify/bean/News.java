package com.example.zhihudailypurify.bean;

import java.util.List;

/**
 * 主界面新闻
 */
public class News {
    private int date;
    private List<String> storyTitles;
    private List<String> tStoryTitles;
    private List<String> storyImgUri;
    private List<Integer> ids;
    private List<Integer> tIds;
    public int getDate() {
        return date;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> gettIds() {
        return tIds;
    }

    public void settIds(List<Integer> tIds) {
        this.tIds = tIds;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public List<String> getStoryTitles() {
        return storyTitles;
    }

    public void setStoryTitles(List<String> storyTitles) {
        this.storyTitles = storyTitles;
    }

    @Override
    public String toString() {
        return "News{" +
                "date=" + date +
                ", storyTitles=" + storyTitles +
                ", tStoryTitles=" + tStoryTitles +
                ", storyImgUri=" + storyImgUri +
                ", ids=" + ids +
                ", tIds=" + tIds +
                ", tStoryImgUri=" + tStoryImgUri +
                '}';
    }

    public List<String> gettStoryTitles() {
        return tStoryTitles;
    }

    public void settStoryTitles(List<String> tStoryTitles) {
        this.tStoryTitles = tStoryTitles;
    }

    public List<String> getStoryImgUri() {
        return storyImgUri;
    }

    public void setStoryImgUri(List<String> storyImgUri) {
        this.storyImgUri = storyImgUri;
    }

    public List<String> gettStoryImgUri() {
        return tStoryImgUri;
    }

    public void settStoryImgUri(List<String> iStoryImgUri) {
        this.tStoryImgUri = iStoryImgUri;
    }

    private List<String> tStoryImgUri;

}
