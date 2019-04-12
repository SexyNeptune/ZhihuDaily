package com.example.zhihudailypurify.model;

public interface PermissionInterface {
    void requestPermissionsSuccess(int callBackCode);
    void requestPermissionsFail(int callBackCode);
}