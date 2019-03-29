package com.example.zhihudailypurify;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Service.AutoUpdateService;
import Service.DownloadService;
import Utils.HttpUtil;
import db.MyDatabaseHelper;
import db.News;
import db.Story;
import db.Tstory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private RecyclerView recyclerview;
    private NavigationView mNavigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_point_group;
    private TextView textView;
    private MyRecyclerAdapter adapter;
    private ScrollView scrollView;
    private MyDatabaseHelper dbHelper;

    /**
     * 轮播图中点的记录位置
     */
    private int prePosition = 0;

    /**
     * 装recyclerView数据的集合
     */
    private List<Story> storyList ;

    /**
     * 装viewPager数据的集合
     */
    private List<Tstory> tstoryList;
    /**
     * 记录历史日期
     */
    private int historyDate;

    private DownloadService.DownloadBinder  downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadService.DownloadBinder) iBinder;
            downloadBinder.startDownload(storyList);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
        findViews();
        initView();
        initData();
        setListener();
        Log.e(TAG, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
    }

    private void startService() {
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void findViews() {
        viewPager = findViewById( R.id.viewPager );
        recyclerview = findViewById( R.id.recyclerview );
        mNavigationView = findViewById( R.id.nav_view );
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        ll_point_group = findViewById(R.id.ll_point_group);
        textView = findViewById(R.id.tv_title);
        scrollView = findViewById(R.id.scroll_view);
    }

    private void initData(){
//        HttpUtil.sendHttpRequest("https://news-at.zhihu.com/api/4/news/latest",this);
        dbHelper = new MyDatabaseHelper(this,"NewsContent.db", null,1);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String response =sharedPreferences.getString("news" ,null);
        if(response!=null){//有主页面缓存时直接加载
            db.News news =HttpUtil.handleNewsResponse(response);
            Log.e(TAG,"已存入json数据：      "+ news.toString());
            refreshData(news);
        }else{
            requestLatestNews();
        }
        //给轮播图添加点
        for (int i =0; i<tstoryList.size(); i++){
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16,16);
            params.leftMargin = 20;
            if (i==0){
                point.setEnabled(true);
            }else{
                point.setEnabled(false);
            }
            point.setLayoutParams(params);
            ll_point_group.addView(point);
        }
    }

    private void refreshData(db.News news) {
        storyList= new ArrayList<>();
        tstoryList = new ArrayList<>();
        for(int i =0; i<news.gettStoryTitles().size();i++){
            //添加到viewPager数据集合中
            Tstory tstory = new Tstory();
            tstory.setImgUri(news.gettStoryImgUri().get(i));
            tstory.setTitle(news.gettStoryTitles().get(i));
            tstory.setId(news.gettIds().get(i));
            tstoryList.add(tstory);
        }
        addData(news);
        showView();
    }

    private void addData(News news) {
        historyDate = news.getDate();
        storyList.add(new Story(String.valueOf(historyDate),null,0));
        for(int i =0 ; i<news.getStoryImgUri().size();i++){
            //添加到RecyclerView的数据集合中
            Story story = new Story();
            story.setImgUrl(news.getStoryImgUri().get(i));
            story.setTitle(news.getStoryTitles().get(i));
            story.setId(news.getIds().get(i));
            story.setType(1);
            storyList.add(story);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int item = (viewPager.getCurrentItem()+1)%tstoryList.size();
            viewPager.setCurrentItem(item);
            handler.sendEmptyMessageDelayed(0,3000);
            return false;
        }
    });

    private void showView() {
        adapter = new MyRecyclerAdapter(storyList,MainActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(adapter);
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        viewPager.setCurrentItem(0);
        textView.setText(tstoryList.get(0).getTitle());
        viewPager.setAdapter(new MyPagerAdapter(tstoryList, MainActivity.this));
        //第一发自动循环消息开始
        handler.sendEmptyMessageDelayed(0,4000);
    }

    /**
     * 本地没有保存时联网申请数据
     */
    private void requestLatestNews(){
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
                Log.e(TAG,"申请到的今日热闻数据：         "+ news.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(news != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("news" ,responseText);
                            editor.apply();
                            refreshData(news);
                        }
                    }
                });
            }
        });
    }

    private void requestHistoryNews(int date){
        String latestUrl="https://news-at.zhihu.com/api/4/news/before/" + String.valueOf(date);
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
                final db.News news = HttpUtil.handleHistoryNewsResponse(responseText);
                Log.e(TAG,"申请到的历史新闻数据：         "+ news.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(news != null){
                            addData(news);
                            adapter.notifyDataSetChanged();
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

    //swipeRefreshLayout的监听
    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        //刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLatestNews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //侧滑栏监听
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(mNavigationView);
                return true;
            }
        });
        //下载按钮监听
        View headerView = mNavigationView.getHeaderView(0);
        TextView tv_download = headerView.findViewById(R.id.nav_download);
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent1 = new Intent(MainActivity.this, DownloadService.class);
//                bindService(intent1,connection,BIND_AUTO_CREATE); //绑定服务
                for(int i = 0; i<storyList.size(); i++){
                    requestNewsContent(storyList.get(i));
                    Log.e(TAG,"第"+ i + "条新闻缓存==============");
                }
            }
        });

        //viewPager的监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                //选中的位置设置成对应的文本
                textView.setText(tstoryList.get(position).getTitle());
                //取消上个viewPager点的高亮
                ll_point_group.getChildAt(prePosition).setEnabled(false);
                //当前的设置成高亮
                ll_point_group.getChildAt(position).setEnabled(true);
                //预记录上一个位置
                prePosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING://1 dragging（拖动）
                        handler.removeCallbacksAndMessages(null); //记得是传null而不是0啊
//                        Log.e(TAG, "拖动state:"+state+"---------->---------->现在的页码索引:"+viewPager.getCurrentItem());
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING://2 settling(安放、定居、解决)
//                        Log.e(TAG, "安放state:"+state+"---------->---------->现在的页码索引:"+viewPager.getCurrentItem());
                        break;
                    case ViewPager.SCROLL_STATE_IDLE://0 idle(空闲，挂空挡)
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(0,3000);
//                        Log.e(TAG, "挂空挡state:"+state+"---------->---------->现在的页码索引:"+viewPager.getCurrentItem());
                        break;
                }
            }
        });

//        //RecyclerView的监听，一旦设置了setNestedScrollingEnable(false)后recyclerview就不处理事件了，故已失效
//        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
////                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//
////                }
//                if (recyclerView.canScrollVertically(1)){
//                    Toast.makeText(MainActivity.this,"获取更多往期新闻中", Toast.LENGTH_SHORT).show();
//                    requestHistoryNews(historyDate--);
//                }
//            }
//        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        if(scrollView.getChildAt(0).getMeasuredHeight() == scrollView.getScrollY()+ scrollView.getHeight()){
                            Toast.makeText(MainActivity.this,"获取更多往期新闻中", Toast.LENGTH_SHORT).show();
                            requestHistoryNews(historyDate--);
                        }
                }
                return false;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
    }

    private void requestNewsContent(final Story story) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://news-at.zhihu.com/api/4/news/" + story.getId()).build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseText = response.body().string();
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this,"NewsContent.db",null,1);
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
        }).start();
    }
}
