package com.cd.xq;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;

import cn.jpush.im.android.api.JMessageClient;

import static com.tencent.bugly.Bugly.applicationContext;

/**
 * Created by Administrator on 2018/10/28.
 */

public class XqApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);
        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "4c34f4883b", true);

        TXLiveBase.setAppID("1252463788");
        TXLiveBase.setConsoleEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
