package com.cd.xq.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cd.xq.module.util.base.DefaultWebActivity;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Administrator on 2019/4/26.
 */

public class RegisterInfoDialogActivity extends DefaultWebActivity {

    public static void startWeb(Activity activity, String webUrl, String title) {
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
    }

    @Override
    protected void initWebView(WebView webView, boolean isCache) {
        super.initWebView(webView, isCache);
        WebSettings webSetting = webView.getSettings();
        webSetting.setUseWideViewPort(true);  //不放大
    }
}
