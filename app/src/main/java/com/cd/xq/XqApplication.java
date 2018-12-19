package com.cd.xq;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.ArraySet;

import com.cd.xq.module.util.tools.Log;
import com.hc.lib.msc.MscManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by Administrator on 2018/10/28.
 */

public class XqApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //极光IM
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        HashSet<String> arraySet = new HashSet<>();
        arraySet.add(AppConstant.JPUSH_TAG_CHAT);
        JPushInterface.setTags(this, arraySet, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d("JPush--setTags=" + i + "--" + s);
            }
        });

        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "baaced6d7c", true);

        TXLiveBase.setAppID("1252463788");
        TXLiveBase.setConsoleEnabled(true);

        MscManager.initMsc(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
