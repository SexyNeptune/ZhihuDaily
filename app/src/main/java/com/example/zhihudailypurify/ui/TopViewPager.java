package com.example.zhihudailypurify.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zhihudailypurify.R;
import com.example.zhihudailypurify.activity.MainActivity;
import com.example.zhihudailypurify.adapter.MyPagerAdapter;
import com.example.zhihudailypurify.bean.TopStory;

import java.util.ArrayList;
import java.util.List;

public class TopViewPager extends RelativeLayout {

    /**
     * 轮播图中点的记录位置
     */
    private int prePosition;

    /**
     * 装viewPager数据的集合
     */
    private List<TopStory> myTopStoryList = new ArrayList<>();

    private ViewPager myViewPager;
    private LinearLayout myLlPointGroup;
    private TextView myTvTitle;
    private Context mContext;

    public TopViewPager(Context context) {
        super(context);
        onCreate(context);
    }

    public TopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public TopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    public void onCreate(Context context){
        mContext = context;
        findView();
        setListener();
    }

    //实现轮播图自动循环
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int item = (myViewPager.getCurrentItem()+1)% myTopStoryList.size();
            myViewPager.setCurrentItem(item);
            handler.sendEmptyMessageDelayed(0,3000);
            return false;
        }
    });

    private void setListener() {
        //viewPager的监听
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                //选中的位置设置成对应的文本
                myTvTitle.setText(myTopStoryList.get(position).getTitle());
                //取消上个viewPager点的高亮
                myLlPointGroup.getChildAt(prePosition).setEnabled(false);
                //当前的设置成高亮
                myLlPointGroup.getChildAt(position).setEnabled(true);
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
    }

    public void findView(){
        LayoutInflater.from(mContext).inflate(R.layout.viewpager,this,true);
        myLlPointGroup = findViewById(R.id.ll_point_group);
        myTvTitle = findViewById(R.id.tv_title);
        myViewPager = findViewById(R.id.viewPager);
    }

    public void addData(List<TopStory> topStories){
        handler.removeCallbacksAndMessages(null);
        myLlPointGroup.removeAllViews();
        prePosition = 0;
        myTopStoryList = topStories;
        //给轮播图添加相应个数的点
        for (int i = 0; i< myTopStoryList.size(); i++){
            ImageView point = new ImageView(mContext);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16,16);
            params.leftMargin = 20;
            if (i==0){
                point.setEnabled(true);
            }else{
                point.setEnabled(false);
            }
            point.setLayoutParams(params);
            myLlPointGroup.addView(point);
        }
        //轮播图自动循环开始
        handler.sendEmptyMessageDelayed(0,4000);
        showView();
    }

    private void showView() {
        myTvTitle.setText(myTopStoryList.get(0).getTitle());
        myViewPager.setAdapter(new MyPagerAdapter(myTopStoryList, mContext));
        myViewPager.setCurrentItem(0);
    }

}
