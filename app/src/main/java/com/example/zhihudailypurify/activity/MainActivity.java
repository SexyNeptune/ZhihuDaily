package com.example.zhihudailypurify.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.zhihudailypurify.model.DownloadListener;
import com.example.zhihudailypurify.model.DownloadTask;
import com.example.zhihudailypurify.model.PermissionHelper;
import com.example.zhihudailypurify.model.PermissionInterface;
import com.example.zhihudailypurify.service.AutoUpdateService;

import com.example.zhihudailypurify.R;
import com.example.zhihudailypurify.adapter.MyRecyclerAdapter;
import com.example.zhihudailypurify.bean.News;
import com.example.zhihudailypurify.bean.Story;
import com.example.zhihudailypurify.bean.TopStory;
import com.example.zhihudailypurify.model.NewsStoryCallbackListener;
import com.example.zhihudailypurify.model.NewsStoryModel;
import com.example.zhihudailypurify.ui.TopViewPager;
import com.example.zhihudailypurify.util.ParseUtil;

public class MainActivity extends AppCompatActivity implements PermissionInterface {

    private static final String TAG = "MainActivity";
    private RecyclerView myRvNews;
    private NavigationView myNvgView;
    private DrawerLayout myDrawerLayout;
    private Toolbar myToolbar;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private MyRecyclerAdapter adapter;
    private ScrollView myScrollView;
    private TextView mTvdownload;
    private TopViewPager myViewPager;
    private PermissionHelper mPermissionHelper;

    /**
     * 装viewPager数据的集合
     */
    private List<TopStory> topStoryList = new ArrayList<>();

    /**
     * 装recyclerView数据的集合
     */
    private List<Story> storyList = new ArrayList<>();

    /**
     * 记录历史日期
     */
    private int historyDate;

    /**
     *网络请求数据及处理类
     */
    private NewsStoryModel newsStoryModel;

    /**
     * 下拉的时候是否正在加载中
     */
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
        findViews();
        initView();
        //想要initData就必须申请权限
        mPermissionHelper = new PermissionHelper(this,this);
        mPermissionHelper.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
        setListener();
        Log.e(TAG, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
    }

    private void startService() {
        //启动自动更新并通知最新一条新闻的服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void findViews() {
        myRvNews = findViewById( R.id.main_rv_news);
        myNvgView = findViewById( R.id.main_nav_view);
        myDrawerLayout = findViewById(R.id.main_drawer_layout);
        myToolbar = findViewById(R.id.main_toolbar);
        mySwipeRefreshLayout = findViewById(R.id.main_swipe_refresh);
        myScrollView = findViewById(R.id.main_scroll_view);
        myViewPager = findViewById(R.id.main_myViewPager);
    }

    private void initView() {
        mySwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#8F33CC"));       //紫色棒棒哒
        myToolbar.setTitle("首页");
        setSupportActionBar(myToolbar);//妈呀这句漏了搞得半天没看出个究竟来。。。将标题栏显现出来
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            //标题栏中设置HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //RecyclerView禁止滑动
        myRvNews.setHasFixedSize(true);
        myRvNews.setNestedScrollingEnabled(false);
        //侧滑栏默认选中首页
        myNvgView.setCheckedItem(R.id.nav_item_home);
    }

    private void initData(){
//        HttpUtil.sendHttpRequest("https://news-at.zhihu.com/api/4/news/latest",this);
        newsStoryModel = new NewsStoryModel(MainActivity.this, new MyNewsStoryCallbackListener());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String response =sharedPreferences.getString("news" ,null);
        if(response!=null){//有主页面缓存时直接加载
            News news = ParseUtil.handleNewsResponse(response);
            Log.e(TAG,"已存入json数据：      "+ news.toString());
            newsStoryModel.refreshData(news);
        }else{
            newsStoryModel.request();
        }
    }

    /**
     * 获取到数据之后适配到viewPager和RecyclerView中
     */
    private void setViews() {
        adapter = new MyRecyclerAdapter(storyList,MainActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRvNews.setLayoutManager(layoutManager);
        myRvNews.setAdapter(adapter);
        myViewPager.addData(topStoryList);
    }

    @Override
    public void requestPermissionsSuccess(int callBackCode) {
        if (callBackCode ==1){
            initData();
        }
    }

    @Override
    public void requestPermissionsFail(int callBackCode) {

    }

    /**
     * 网络请求的回调
     */
    class MyNewsStoryCallbackListener implements NewsStoryCallbackListener{
        @Override
        public void onResponse(final List<Story> stories, final List<TopStory> topStories) {
            storyList.addAll(stories);
            topStoryList.addAll(topStories);
            isLoading = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (topStories != null){
                        setViews();
                        historyDate = Integer.parseInt(stories.get(0).getTitle());
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
        @Override
        public void onFailure() {
            isLoading = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"请求网络数据失败",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {

        //刷新监听
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //清空集合
                storyList.clear();
                topStoryList.clear();
                newsStoryModel.request();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });

        //侧滑栏监听
        myNvgView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                myDrawerLayout.closeDrawer(myNvgView);
                return true;
            }
        });

        //下载按钮监听
        View headerView = myNvgView.getHeaderView(0);
        mTvdownload = headerView.findViewById(R.id.nav_tv_download);
        mTvdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvdownload.setText("下载中...");
                    DownloadTask downloadTask = new DownloadTask(new DownloadListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                            mTvdownload.setText("下载");
                        }
                    },MainActivity.this);
                    downloadTask.execute(storyList);
            }
        });

        myScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        if(myScrollView.getChildAt(0).getMeasuredHeight() == myScrollView.getScrollY()+ myScrollView.getHeight()){
                            if (!isLoading){
                                Toast.makeText(MainActivity.this,"加载更多往期新闻中" + historyDate, Toast.LENGTH_SHORT).show();
                                newsStoryModel.requestHistoryNews(historyDate--);
                                isLoading = true;
                            }
                        }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                myDrawerLayout.openDrawer(GravityCompat.START);
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

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.requestPermissionsResult(requestCode,permissions,grantResults);
    }
}
