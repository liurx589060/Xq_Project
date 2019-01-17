package com.cd.xq.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.network.NetWorkMg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/1/14.
 */

public class ProtocolActivity extends BaseActivity {
    @BindView(R.id.protocol_btn_back)
    Button protocolBtnBack;
    @BindView(R.id.protocol_webView)
    WebView protocolWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        ButterKnife.bind(this);

        WebSettings webSettings = protocolWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
        protocolWebView.loadUrl(url);
    }

    @OnClick(R.id.protocol_btn_back)
    public void onViewClicked() {
        finish();
    }
}
