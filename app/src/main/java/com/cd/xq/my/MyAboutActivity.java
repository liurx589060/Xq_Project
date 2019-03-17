package com.cd.xq.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cd.xq.R;
import com.cd.xq.login.LoginActivity;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.DefaultWebActivity;
import com.cd.xq.module.util.network.NetWorkMg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_about);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

    }

    @OnClick({R.id.btn_back, R.id.relayout_update, R.id.relayout_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.relayout_update:
                break;
            case R.id.relayout_protocol:
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
                DefaultWebActivity.startWeb(this,url,"用户协议");
                break;
        }
    }
}
