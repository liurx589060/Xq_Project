package com.cd.xq.module.chart;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.module.chart.manager.XqStatusChartUIViewMg;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq_chart.module.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/11/14.
 */

public class ChartRoomActivity extends BaseActivity {
    private XqStatusChartUIViewMg mXqStatusChartUIViewMg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXqStatusChartUIViewMg = new XqStatusChartUIViewMg(this);
        mXqStatusChartUIViewMg.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mXqStatusChartUIViewMg.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mXqStatusChartUIViewMg.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mXqStatusChartUIViewMg.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mXqStatusChartUIViewMg.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            mXqStatusChartUIViewMg.showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
