package com.cd.xq.my;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.cd.xq.R;
import com.cd.xq.beans.BCheckUpdate;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.DefaultWebActivity;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.network.XqRequestApi;
import com.cd.xq.utils.AppTools;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 关于
 * Created by Administrator on 2019/3/17.
 */

public class MyAboutActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.relayout_update)
    RelativeLayout relayoutUpdate;
    @BindView(R.id.relayout_protocol)
    RelativeLayout relayoutProtocol;
    @BindView(R.id.text_versionName)
    TextView textVersionName;

    private XqRequestApi mApi;
    private DownloadBuilder mBuilder;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_about);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        mHandler = new Handler();

        init();
    }

    private void init() {
        textVersionName.setText("V" + Tools.getVersionName(this));
    }

    /**
     * 检查更新
     */
    private void requestCheckUpdate() {
        mApi.checkUpdate(Tools.getVersionCode(this))
                .compose(this.<NetResult<BCheckUpdate>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BCheckUpdate>>() {
                    @Override
                    public void accept(NetResult<BCheckUpdate> bCheckUpdateNetResult) throws Exception {
                        BCheckUpdate bean = bCheckUpdateNetResult.getData();
                        if (bean == null) {
                            Tools.toast(getApplicationContext(),"已是最新版本",false);
                            return;
                        }
                        UIData uiData = UIData.create();
                        uiData.setTitle("检测到新版本");
                        String content = bean.getMessage().replace("\\n", "\n");
                        uiData.setContent(content);
                        uiData.setDownloadUrl(bean.getDown_url());
                        mBuilder = AllenVersionChecker
                                .getInstance()
                                .downloadOnly(uiData);
                        mBuilder.setNewestVersionCode(bean.getVersion_code());
                        if (bean.getIs_force() == 1) {
                            //强制更新
                            mBuilder.setForceUpdateListener(new ForceUpdateListener() {
                                @Override
                                public void onShouldForceUpdate() {
                                }
                            });
                        }
                        mBuilder.executeMission(MyAboutActivity.this);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("requestCheckUpdate--" + throwable.toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestCheckUpdate();
                            }
                        }, 5000);
                    }
                });
    }

    @OnClick({R.id.btn_back, R.id.relayout_update, R.id.relayout_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.relayout_update:
                //检测是否更新
                requestCheckUpdate();
                break;
            case R.id.relayout_protocol:
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
                DefaultWebActivity.startWeb(this, url, "用户协议");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AllenVersionChecker.getInstance().cancelAllMission(this);
    }
}
