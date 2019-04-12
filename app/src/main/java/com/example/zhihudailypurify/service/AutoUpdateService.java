package com.example.zhihudailypurify.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.zhihudailypurify.R;

import java.io.IOException;

import com.example.zhihudailypurify.util.HttpUtil;
import com.example.zhihudailypurify.bean.News;
import com.example.zhihudailypurify.util.ParseUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";
    private News news = null;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNews();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int alarmTime = 60*1000; //一分钟更新+发通知
        long triggerAtTime = SystemClock.elapsedRealtime() + alarmTime;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(News news) {
        String channelId = "news";
        String channelName = "今日热闻";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId,channelName,importance);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this,channelId)
                .setContentTitle("最新新闻")
                .setContentText(news.getStoryTitles().get(0))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();
        manager.notify(1,notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }


    public void updateNews(){
        String latestUrl="https://news-at.zhihu.com/api/4/news/latest";
        HttpUtil.sendOkHttpRequest(latestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                news = ParseUtil.handleNewsResponse(responseText);
                Log.e(TAG,news.toString());
                if(news != null){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("news" ,responseText);
                    editor.apply();
                    showNotification(news);
                }
            }
        });
    }
}
