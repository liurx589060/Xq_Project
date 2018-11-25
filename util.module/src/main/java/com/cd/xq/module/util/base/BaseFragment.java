package com.cd.xq.module.util.base;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Administrator on 2018/11/11.
 */

public class BaseFragment extends Fragment {
    protected View mRootView;

    public int getStatusColor() {
        return Color.parseColor("#ffa07a");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    public void onLogin() {}
}
