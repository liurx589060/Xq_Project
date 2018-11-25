package com.cd.xq.module.chart.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;

/**
 * Created by Administrator on 2018/5/26.
 */

public abstract class AbsChartView {
    protected Activity mActivity;
    protected String mAddress;
    protected View mRootView;

    public abstract View getView();
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onDestroy();
    public abstract void onConfigurationChanged(Configuration newConfig);

    public void init(Activity activity,String address){
        mActivity = activity;
        mAddress = address;
    }

    public void setVisible(boolean isVisible){
        if(mRootView != null) {
            mRootView.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
        }
    }
    public void start(){}
    public void stop(){}
}
