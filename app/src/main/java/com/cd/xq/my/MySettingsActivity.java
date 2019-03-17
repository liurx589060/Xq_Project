package com.cd.xq.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cd.xq.R;
import com.cd.xq.login.LoginActivity;
import com.cd.xq.module.util.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 * Created by Administrator on 2019/3/17.
 */

public class MySettingsActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.relayout_about)
    RelativeLayout relayoutAbout;
    @BindView(R.id.relayout_feedback)
    RelativeLayout relayoutFeedback;
    @BindView(R.id.btn_change_account)
    Button btnChangeAccount;
    @BindView(R.id.btn_exit_account)
    Button btnExitAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

    }

    private void jumpToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("from", LoginActivity.FROM_MY);
        intent.putExtras(bundle);
        startActivityForResult(intent, 200);
    }

    @OnClick({R.id.btn_back, R.id.relayout_about, R.id.relayout_feedback, R.id
            .btn_change_account, R.id.btn_exit_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.relayout_about:
            {
                Intent intent = new Intent(this,MyAboutActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.relayout_feedback:
                break;
            case R.id.btn_change_account:
                jumpToLoginActivity();
                break;
            case R.id.btn_exit_account:
                break;
        }
    }
}
