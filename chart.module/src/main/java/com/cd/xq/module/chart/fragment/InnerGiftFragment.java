package com.cd.xq.module.chart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cd.xq.module.chart.manager.PresentGiftViewMg;
import com.cd.xq.module.util.base.BaseFragment;

/**
 * Created by Administrator on 2019/3/25.
 */

public abstract class InnerGiftFragment extends BaseFragment {
    protected View mRootView;
    protected PresentGiftViewMg mGiftViewMg;
    public abstract String getTitle();
    public void setGiftViewMg(PresentGiftViewMg viewMg) {
        this.mGiftViewMg = viewMg;
    }
}
