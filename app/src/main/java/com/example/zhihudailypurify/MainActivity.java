package com.example.zhihudailypurify;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.HttpCallbackListener;
import Utils.HttpUtil;
import db.Story;
import db.Tstory;
import gson.News;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements HttpCallbackListener{

    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private RecyclerView recyclerview;
    private NavigationView mNavigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * 装recyclerView的集合
     */
    private List<Story> storyList ;

    private List<Tstory> tstoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initView();
        initData();
        setListener();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Log.e(TAG,pref.getString("story_title","hhh")+"OK");
    }

    private void initData(){
//        HttpUtil.sendHttpRequest("https://news-at.zhihu.com/api/4/news/latest",this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String response =sharedPreferences.getString("news",null);
        if(response!=null){//有主页面缓存时直接加载
            db.News news =HttpUtil.handleNewsResponse(response);
            Log.e(TAG,news.toString());
            showViews(news);
        }else{
            requestNews();
        }
    }

    private void showViews(db.News news) {
        storyList= new ArrayList<>();
        storyList.add(new Story(String.valueOf(news.getDate()),null,0));
        for(int i =0 ; i<news.getStoryImgUri().size();i++){
            Story story = new Story();
            story.setImgUrl(news.getStoryImgUri().get(i));
            story.setTitle(news.getStoryTitles().get(i));
            story.setType(1);
            storyList.add(story);
        }
        Log.e(TAG,storyList.get(0).getTitle());
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(storyList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(adapter);
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        tstoryList = new ArrayList<>();
        for(int i =0; i<news.gettStoryTitles().size();i++){
            Tstory tstory = new Tstory();
            tstory.setImgUri(news.gettStoryImgUri().get(i));
            tstory.setTitle(news.gettStoryTitles().get(i));
            tstoryList.add(tstory);
        }
        viewPager.setAdapter(new MyPagerAdapter(tstoryList));
    }


    /**
     * 本地没有保存时联网申请数据
     */
    private void requestNews(){
        String latestUrl="https://news-at.zhihu.com/api/4/news/latest";
        HttpUtil.sendOkHttpRequest(latestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                final db.News news = HttpUtil.handleNewsResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(news != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("news",responseText);
                            editor.apply();
                            showViews(news);
                        }else{
                            Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#8F33CC"));       //紫色棒棒哒
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);//妈呀这句漏了搞得半天没看出个究竟来。。。将标题栏显现出来
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            //标题栏中设置HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //侧滑栏默认选中首页
        mNavigationView.setCheckedItem(R.id.nav_home);

    }

    private void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(mNavigationView);
                return true;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void findViews() {
        viewPager = findViewById( R.id.viewPager );
        recyclerview = findViewById( R.id.recyclerview );
        mNavigationView = findViewById( R.id.nav_view );
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.message:
                Toast.makeText(this,"You clicked 消息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.day_and_night:
                Toast.makeText(this, "You clicked 日间模式", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(this,"You clicked settings",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    @Override
    public void onFinish(String response) {
        Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
    }
}
