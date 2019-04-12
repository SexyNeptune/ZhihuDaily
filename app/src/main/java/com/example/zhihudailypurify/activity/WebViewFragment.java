package com.example.zhihudailypurify.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhihudailypurify.R;
import com.example.zhihudailypurify.bean.NewsContent;
import com.example.zhihudailypurify.db.MyDatabaseHelper;
import com.example.zhihudailypurify.util.BitmapUtils;
import com.example.zhihudailypurify.util.HttpUtil;
import com.example.zhihudailypurify.util.ParseUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WebViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mUrl;
    private String mId;

    private WebView mWvBody;
    private ImageView imageView;
    private TextView imageSource;
    private TextView title;
    private Activity mActivity;
    private BitmapUtils mBitmapUtils;

    protected boolean isVisible;
    // 标志位，标志Fragment已经初始化完成。
    public boolean isPrepared = false;



    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String url, String id) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        args.putString(ARG_PARAM2, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInVisible();
        }
    }

    protected void onInVisible() {
    }

    protected void onVisible() {
        //加载数据
        initData();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_PARAM1);
            mId = getArguments().getString(ARG_PARAM2);
        }
        mActivity = getActivity();
    }

    protected void initData() {
        if(!isPrepared || !isVisible) {
            return;
        }
        mBitmapUtils = new BitmapUtils(getContext());
        MyDatabaseHelper helper = new MyDatabaseHelper(mActivity,"NewsContent.db",null,1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newsContent where newsId = ?",new String[]{mId});
        if (cursor.moveToFirst()){
            String responseText = cursor.getString(cursor.getColumnIndex("response"));
            handleResponse(responseText);
        }else{
            requestBody();
        }
        cursor.close();
    }

    private void requestBody(){
        String contentUrl  = mUrl +  mId;
        HttpUtil.sendOkHttpRequest(contentUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity,"获取新闻内容失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                handleResponse(responseText);
            }
        });
    }

    private void handleResponse(String responseText) {
        final NewsContent content = ParseUtil.handleNewsContentResponse(responseText);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String head = "<head><style>* {font-size:15px}{color:#212121;}img{max-width: 100%; width:auto; height: auto;}</style></head>";
                String resultStr = "<html>" + head + "<body>" + content.getBody() + "</body></html>";
                mWvBody.loadDataWithBaseURL(null,resultStr,"text/html","utf-8",null);
                imageSource.setText(content.getImageResouce());
                title.setText(content.getTitle());
                showImage(content);
            }
        });
    }

    private void showImage(final NewsContent content) {
        mBitmapUtils.setImage(imageView,content.getImage());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        findView(view);
        initView();
        isPrepared = true;
        initData();
        return view;
    }

    private void initView() {
        WebSettings settings = mWvBody.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    private void findView(View view) {
        mWvBody = view.findViewById(R.id.newscontent_wv_body);
        imageView = view.findViewById(R.id.news_content_image);
        title = view.findViewById(R.id.news_content_title);
        imageSource = view.findViewById(R.id.image_source);
    }

}
