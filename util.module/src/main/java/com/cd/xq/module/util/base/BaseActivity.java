package com.cd.xq.module.util.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.cd.xq.module.util.tools.StatusBarCompat;

/**
 * Created by Administrator on 2018/10/28.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean isTranslucentStatusBar() {
        return false;
    }

    private void toSetSatusBar() {
        if(isTranslucentStatusBar())
        {
            translucentStatusBar(true);
        }else {
            setStatusBarColor(getStatusBarColor());
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toSetSatusBar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        toSetSatusBar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        toSetSatusBar();
    }

    public int getStatusBarColor(){
        return Color.parseColor("#ffa07a");
    }

    public void setStatusBarColor(int color) {
        StatusBarCompat.setStatusBarColor(this,color);
    }

    public void setStatusBarColor(int color,int alpha) {
        StatusBarCompat.setStatusBarColor(this,color,alpha);
    }

    public void translucentStatusBar(boolean hideStatusBarBackground) {
        StatusBarCompat.translucentStatusBar(this,hideStatusBarBackground);
    }
}
