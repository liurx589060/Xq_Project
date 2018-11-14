package com.cd.xq.frame;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseFragment;

/**
 * Created by Administrator on 2018/11/11.
 */

public class HomeFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_home,null);
        return mRootView;
    }

//    @Override
//    public int getStatusColor() {
//        return Color.parseColor("#32b7b9");
//    }
}
