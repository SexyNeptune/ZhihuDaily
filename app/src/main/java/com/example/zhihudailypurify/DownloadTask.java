package com.example.zhihudailypurify;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import db.MyDatabaseHelper;
import db.Story;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<List<Story>,Integer,Integer> {

    private DownloadListener listener;
    private Context mContext;
    private List<Story> storyList = new ArrayList<>();


    public DownloadTask (DownloadListener listener, Context context){
        this.listener = listener;
        mContext =  context;

    }

    @Override
    protected Integer doInBackground(List<Story>... strings) {
        storyList = strings[0];
        for(int i = 0; i<storyList.size(); i++){
            requestNewsContent(storyList.get(i));
        }
        return null;
    }

    private void requestNewsContent(Story story) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://news-at.zhihu.com/api/4/news/" + story.getId()).build();
        try {
            Response response = client.newCall(request).execute();
            String responseText = response.body().string();
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext,"NewsContent.db",null,1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("newsId",story.getId());
            values.put("response",responseText);
            db.insert("newsContent",null,values);
            values.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case 0:
                listener.onSuccess();
                break;
                default:
                    break;
        }
    }
}
