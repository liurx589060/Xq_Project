package com.cd.xq.module.util.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq_util.com.R;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Administrator on 2019/2/18.
 */

public class DefaultWebActivity extends BaseActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private ImageView mImgBack;
    private TextView mTextTitle;
    private String mWebUrl = "";
    private String mTitle = "";
    private boolean mIsCache;


    public static void startWeb(Activity activity,String webUrl,String title) {
        startWeb(activity,webUrl,title,false);
    }

    /**
     * 默认不适用缓存
     * @param activity
     * @param webUrl
     * @param title
     * @param isUseCache
     */
    public static void startWeb(Activity activity,String webUrl,String title,boolean isUseCache) {
        Intent intent = new Intent(activity,DefaultWebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        bundle.putString("url",webUrl);
        bundle.putBoolean("isCache",isUseCache);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //网页中的视频，上屏幕的时候，可能出现闪烁的情况
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//（这个对宿主没什么影响，建议声明）

        setContentView(R.layout.activity_default_web);

        try{
            mTitle = getIntent().getExtras().getString("title");
            mWebUrl = getIntent().getExtras().getString("url");
            mIsCache = getIntent().getExtras().getBoolean("isCache");
        }catch (Exception e) {
            Log.e("DefaultWebActivity connot find the mTitle,mWebUrl!");
        }

        init();
    }

    private void init() {
        mWebView = findViewById(R.id.default_web_webView);
        mImgBack = findViewById(R.id.default_web_btn_back);
        mProgressBar = findViewById(R.id.default_web_progressBar);
        mTextTitle = findViewById(R.id.default_web_text_title);

        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initWebView(mWebView);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                if(i == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(i);

                super.onProgressChanged(webView,i);
            }
        });

        mTextTitle.setText(mTitle);
        mWebView.loadUrl(mWebUrl);
    }

    /**
     * 初始化webView
     * @param webView
     */
    protected void initWebView(WebView webView) {
        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        //webSetting.setUseWideViewPort(true);  //不放大
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE); //不使用缓存
        if(!mIsCache) {
            webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE); //不使用缓存
        }
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
