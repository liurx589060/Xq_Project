package com.cd.xq.module.chart;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.cd.xq.module.chart.manager.XqStatusChartUIViewMg;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq_chart.module.R;
import com.hc.lib.msc.MscDefaultSpeech;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/11/14.
 */

public class ChartRoomActivity extends BaseActivity {
    private XqStatusChartUIViewMg mXqStatusChartUIViewMg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_room);
        mXqStatusChartUIViewMg = new XqStatusChartUIViewMg(this, (FrameLayout) findViewById(R.id.chart_activity_frameLayout));
        mXqStatusChartUIViewMg.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mXqStatusChartUIViewMg.onDestroy();

        //重置roomId
        DataManager.getInstance().getChartBChatRoom().setRoom_id(0);
        MscDefaultSpeech.getInstance().destroy();
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

    @Override
    public void finish() {
        //检查是否是黑名单
        EventBusParam param = new EventBusParam();
        param.setEventBusCode(EventBusParam.EVENT_BUS_CHECK_BLACKUSER);
        EventBus.getDefault().post(param);

        super.finish();
    }
}
