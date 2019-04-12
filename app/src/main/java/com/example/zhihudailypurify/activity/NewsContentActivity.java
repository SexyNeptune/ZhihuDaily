package com.example.zhihudailypurify.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhihudailypurify.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.zhihudailypurify.adapter.FragmentAdapter;
import com.example.zhihudailypurify.bean.Story;
import com.example.zhihudailypurify.bean.TopStory;
import com.example.zhihudailypurify.util.HttpUtil;
import com.example.zhihudailypurify.util.ParseUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsContentActivity extends AppCompatActivity{
    private static final String TAG = "NewsContentActivity";
    private String extra  = "https://news-at.zhihu.com/api/4/story-extra/";
    private String url = "https://news-at.zhihu.com/api/4/news/";
    private ArrayList<Story> storyArrayList;
    private ArrayList<TopStory> topStoryArrayList;
    private int id ;
    private int position;
    private Toolbar toolbar;
    private ViewPager mVpNewsContent;
    private CheckBox cb_popularity;
    private CheckBox cb_collect;
    private TextView tv_popularity;
    private TextView tv_comments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newscontent);
        findViews();
        intiView();
        getData();
        initData();
        setListener();
    }

    private void getData() {
        //如果是点击Banner的话就传入TopStory集合
        topStoryArrayList = (ArrayList<TopStory>) getIntent().getSerializableExtra("topstorylist");
        //如果谁点击RecyclerView的话就传入Story集合
        storyArrayList = (ArrayList<Story>) getIntent().getSerializableExtra("storylist");
        position = getIntent().getIntExtra("position" , 0);
        if (topStoryArrayList != null){
            id = topStoryArrayList.get(position).getId();
        }else{
            id = storyArrayList.get(position).getId();
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.content_toolbar);
        cb_popularity = findViewById(R.id.cb_popularity);
        cb_collect = findViewById(R.id.cb_collection);
        tv_comments = findViewById(R.id.tv_comments);
        tv_popularity = findViewById(R.id.tv_popularity);
        mVpNewsContent = findViewById(R.id.news_content_viewpager);
    }

    private void intiView() {

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            //标题栏中设置HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void initData() {
        requestExtra();
        //装fragment的集合
        List<Fragment> fragments = new ArrayList<>();
        if (topStoryArrayList != null){
            for (int i = 0; i<topStoryArrayList.size(); i++){
                Fragment fragment = WebViewFragment.newInstance(url,String.valueOf(topStoryArrayList.get(i).getId()));
                fragments.add(fragment);
            }
        }else{
            for (int i = 0; i<storyArrayList.size(); i++){
                if (storyArrayList.get(i).getType()==0){
                    //减去日期栏的数据
                    if (i< position)
                    position --;
                    continue;
                }
                Fragment fragment = WebViewFragment.newInstance(url,String.valueOf(storyArrayList.get(i).getId()));
                fragments.add(fragment);
            }
        }
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),fragments);
        mVpNewsContent.setAdapter(adapter);
        mVpNewsContent.setCurrentItem(position);
    }


    private void requestExtra() {
        String extraUrl = extra + id;
        HttpUtil.sendOkHttpRequest(extraUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                final com.example.zhihudailypurify.bean.ExtraData extraData = ParseUtil.handleExtraNewsResponse(responseText);
                Log.e(TAG,extraData.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String comments = String.valueOf(extraData.getComments());
                        String popularity = String.valueOf(extraData.getPopularity());
                        tv_comments.setText(comments);
                        tv_popularity.setText(popularity);
                    }
                });
            }
        });
    }

    private void setListener() {
        cb_popularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cb_popularity.isChecked()){
                    Toast.makeText(NewsContentActivity.this,"取消成功", Toast.LENGTH_SHORT).show();
                    String text = (String) tv_popularity.getText();
                    int num = Integer.parseInt(text);
                    num--;
                    String numText = String.valueOf(num);
                    tv_popularity.setText(numText);
                }else{
                    Toast.makeText(NewsContentActivity.this,"点赞成功", Toast.LENGTH_SHORT).show();
                    String text = (String) tv_popularity.getText();
                    int num = Integer.parseInt(text);
                    num++;
                    String numText = String.valueOf(num);
                    tv_popularity.setText(numText);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }
}
