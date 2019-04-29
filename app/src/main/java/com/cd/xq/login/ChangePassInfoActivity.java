package com.cd.xq.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.network.NetParse;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2019/4/29.
 */

public class ChangePassInfoActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.edit_userName)
    EditText editUserName;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.text_tip)
    TextView textTip;

    private RequestApi mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_info);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);

        init();
    }

    private void requestUserInfo() {
        textTip.setText("");
        mApi.getUserInfoByUserName(editUserName.getText().toString())
                .compose(this.<UserResp>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        if (userResp.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), "用户不存在",false);
                            textTip.setText("用户不存在");
                            Log.e("requestUserInfo--" + userResp.getMsg());
                            return;
                        }

                        VerifyCodeActivity.startVerify(ChangePassInfoActivity.this,
                                VerifyCodeActivity.REQUEST_CHANGE_PASS,
                                userResp.getData().getPhone(),
                                userResp.getData().getUser_name());
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        NetParse.parseError(ChangePassInfoActivity.this,throwable);
                    }
                });
    }

    private void init() {
        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(editUserName.getText().toString())) {
                    btnNext.setEnabled(false);
                } else {
                    btnNext.setEnabled(true);
                }
            }
        });
    }

    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                requestUserInfo();
                break;
        }
    }
}
