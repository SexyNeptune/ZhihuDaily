package com.example.zhihudailypurify;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import Utils.HttpUtil;
import db.NewsContent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsContentActivity extends AppCompatActivity{
    private static final String TAG = "NewsContentActivity";
    private WebView webView;
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView imageSource;
    private TextView title;
    private CheckBox cb_popularity;
    private CheckBox cb_collect;
    boolean i = false;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what){
                case 1:
                    imageView.setImageBitmap((Bitmap) message.obj);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newscontent);
        findViews();
        intiView();
        requestBody();
        setListener();
    }

    private void setListener() {
        cb_popularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cb_popularity.isChecked()){
                    Toast.makeText(NewsContentActivity.this,"取消成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NewsContentActivity.this,"点赞成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cb_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cb_collect.isChecked()){
                    Toast.makeText(NewsContentActivity.this,"取消成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NewsContentActivity.this,"收藏成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void intiView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            //标题栏中设置HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void findViews() {
        webView = findViewById(R.id.tv_body);
        toolbar = findViewById(R.id.content_toolbar);
        imageView = findViewById(R.id.news_content_image);
        title = findViewById(R.id.news_content_title);
        imageSource = findViewById(R.id.image_source);
        cb_popularity = findViewById(R.id.cb_popularity);
        cb_collect = findViewById(R.id.cb_collection);
    }


    private void requestBody(){
        String url = getIntent().getStringExtra("url");
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NewsContentActivity.this,"获取新闻内容失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                final NewsContent content = HttpUtil.handleNewsContentResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadDataWithBaseURL(null,content.getBody(),"text/html","utf-8",null);
                        imageSource.setText(content.getImageResouce());
                        title.setText(content.getTitle());
                        showImage(content);
                    }
                });
            }
        });
    }

    private void showImage(final NewsContent content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = HttpUtil.getImageBitmap(content.getImage());
                Message message = new Message();
                message.what = 1;
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }
}
