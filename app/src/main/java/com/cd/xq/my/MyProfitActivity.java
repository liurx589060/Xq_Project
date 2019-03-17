package com.cd.xq.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.login.LoginActivity;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.DefaultWebActivity;
import com.cd.xq.module.util.network.NetWorkMg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 收益
 * Created by Administrator on 2019/3/16.
 */

public class MyProfitActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.text_enable_profit)
    TextView textEnableProfit;
    @BindView(R.id.text_today_profit)
    TextView textTodayProfit;
    @BindView(R.id.text_total_profit)
    TextView textTotalProfit;
    @BindView(R.id.relayout_exchange)
    RelativeLayout relayoutExchange;
    @BindView(R.id.btn_cash_history)
    Button btnCashHistory;
    @BindView(R.id.btn_cash_protocol)
    Button btnCashProtocol;
    @BindView(R.id.btn_cash)
    Button btnCash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profit);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

    }

    @OnClick({R.id.btn_back, R.id.relayout_exchange, R.id.btn_cash_history, R.id
            .btn_cash_protocol, R.id.btn_cash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.relayout_exchange:
                break;
            case R.id.btn_cash_history:
            {
                Intent intent = new Intent(this,MyCashHistoryActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_cash_protocol:
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
                DefaultWebActivity.startWeb(MyProfitActivity.this,url,"提现规则");
                break;
            case R.id.btn_cash:
                break;
        }
    }
}
