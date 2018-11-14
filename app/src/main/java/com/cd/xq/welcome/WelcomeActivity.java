package com.cd.xq.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/28.
 */

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.welcome_tv_time)
    TextView mTvTime;
    @BindView(R.id.welcome_relayout_time)
    RelativeLayout mRelayoutTime;

    private Handler mHandler;
    private Runnable mTimeRunnable;
    private int mTimeCount = 0;
    private final int COUNT = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        mHandler = new Handler();
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mTimeCount == COUNT) {
                    jumpToMainActivity();
                }else {
                    mTvTime.setText(String.valueOf(COUNT - mTimeCount) + "  跳过");
                    mTimeCount++;
                    mHandler.postDelayed(this,1000);
                }
            }
        };
        mHandler.post(mTimeRunnable);
    }

    private void jumpToMainActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.welcome_relayout_time)
    public void onViewClicked() {
        mHandler.removeCallbacks(mTimeRunnable);
        jumpToMainActivity();
    }
}
