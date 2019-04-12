package com.example.zhihudailypurify.bean;

import java.io.Serializable;

public class Story implements Serializable {

    public Story(String title, String imgUrl, int type) {
        this.title = title;
        ImgUrl = imgUrl;
        this.type = type;
    }

    private String title;
    private String ImgUrl;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Story() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;
}
