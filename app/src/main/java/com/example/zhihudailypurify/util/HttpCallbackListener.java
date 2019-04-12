package com.example.zhihudailypurify.util;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
