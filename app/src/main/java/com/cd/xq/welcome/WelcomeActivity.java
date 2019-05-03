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
import com.cd.xq.module.util.beans.BAppSettings;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetParse;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.SharedPreferenceUtil;
import com.cd.xq.module.util.tools.Tools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    private RequestApi mApi;
    private boolean mIsGetAppSettings;
    private boolean mIsTimeDown;

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
                    mIsTimeDown = true;
                    jumpToMainActivity();
                }else {
                    mTvTime.setText(String.valueOf(COUNT - mTimeCount) + "  跳过");
                    mTimeCount++;
                    mHandler.postDelayed(this,1000);
                }
            }
        };
        mHandler.post(mTimeRunnable);

        //获取服务器系统配置
        requestGetAppSettings();
    }

    public static Retrofit newRetrofit() {
        String BASEURL = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/Sample_Mjmz/";
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5,TimeUnit.SECONDS)
                .build();

        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        Retrofit retrofit=new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASEURL)
                .build();
        return retrofit;
    }

    private void requestGetAppSettings() {
        mApi = newRetrofit().create(RequestApi.class);
        mApi.getAppSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BAppSettings>>() {
                    @Override
                    public void accept(NetResult<BAppSettings> bAppSettingsNetResult) throws Exception {
                        if(bAppSettingsNetResult.getData() == null) {
                            Log.e("requestGetAppSettings--get the appsettings error");
                            DataManager.getInstance().setAppSettings(new BAppSettings());
                        }else {
                            DataManager.getInstance().setAppSettings(bAppSettingsNetResult.getData());
                        }
                        mIsGetAppSettings = true;
                        jumpToMainActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        DataManager.getInstance().setAppSettings(new BAppSettings());
                        Log.e("requestGetAppSettings--get the appsettings error");
                        mIsGetAppSettings = true;
                        jumpToMainActivity();
                    }
                });
    }

    private void jumpToMainActivity() {
        if(mIsGetAppSettings && mIsTimeDown) {
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
    }

    @OnClick(R.id.welcome_relayout_time)
    public void onViewClicked() {
        mHandler.removeCallbacks(mTimeRunnable);
        mIsTimeDown = true;
        jumpToMainActivity();
    }
}
