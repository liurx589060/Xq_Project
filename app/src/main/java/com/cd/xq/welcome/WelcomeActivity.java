package com.cd.xq.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.bj.paysdk.utils.TrPay;
import com.cd.xq.AppService;
import com.cd.xq.R;
import com.cd.xq.frame.MainActivity;
import com.cd.xq.login.LoginActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.SharedPreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;

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
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        if(SharedPreferenceUtil.getIsRemote(this)) {
            NetWorkMg.IP_ADDRESS = Constant.CONSTANT_REMOTE_IP;
        }else {
            NetWorkMg.IP_ADDRESS = SharedPreferenceUtil.getSpIpAddress(this);
        }

        //TrPay
        TrPay.getInstance(this).initPaySdk(Constant.TRPAY_KEY,"baidu");

        Intent intent = new Intent(this, AppService.class);
        startService(intent);

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
        if(JMessageClient.getMyInfo() != null) {
            //自动登陆
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        this.finish();
    }

    @OnClick(R.id.welcome_relayout_time)
    public void onViewClicked() {
        mHandler.removeCallbacks(mTimeRunnable);
        jumpToMainActivity();
    }
}
