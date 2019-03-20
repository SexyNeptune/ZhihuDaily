package db;

import java.util.List;

public class News {
    private int date;
    private List<String> storyTitles;
    private List<String> tStoryTitles;
    private List<String> storyImgUri;

    public int getDate() {
        return date;
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
