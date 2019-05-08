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
import com.cd.xq.module.util.base.DefaultWebActivity;
import com.cd.xq.module.util.beans.BaseResp;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetParse;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
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
 * 注册
 * Created by Administrator on 2019/3/17.
 */

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.notify_btn_back)
    Button notifyBtnBack;
    @BindView(R.id.edit_userName)
    EditText editUserName;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.btn_verify_phone)
    Button btnVerifyPhone;
    @BindView(R.id.text_protocol)
    TextView textProtocol;
    @BindView(R.id.text_tip)
    TextView textTip;

    private RequestApi mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);

        init();
    }

    private void init() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editPassword.getText().toString()) && !TextUtils.isEmpty(editUserName.getText().toString())) {
                    btnVerifyPhone.setEnabled(true);
                } else {
                    btnVerifyPhone.setEnabled(false);
                }
            }
        };
        editPassword.addTextChangedListener(watcher);
        editUserName.addTextChangedListener(watcher);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.notify_btn_back, R.id.btn_verify_phone, R.id.text_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.notify_btn_back:
                finish();
                break;
            case R.id.btn_verify_phone:
                requestCheckUserExist();
                break;
            case R.id.text_protocol:
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
                DefaultWebActivity.startWeb(this, url, "用户协议");
                break;
        }
    }


    private void requestCheckUserExist() {
        getLoadingDialog().show();
        textTip.setText("");
        mApi.checkUserExist(editUserName.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<BaseResp>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<BaseResp>() {
                    @Override
                    public void accept(BaseResp baseResp) throws Exception {
                        getLoadingDialog().dismiss();
                        if (baseResp.getStatus() == XqErrorCode.ERROR_USER_NOT_EXIST) {
                            DataManager.getInstance().getRegisterUserInfo().setUser_name(editUserName.getText().toString());
                            DataManager.getInstance().getRegisterUserInfo().setPassword(editPassword.getText().toString());
                            VerifyCodeActivity.startVerify(RegisterActivity.this,
                                    VerifyCodeActivity.REQUEST_REGISTER,
                                    null);
                            finish();
                        }else {
                            Tools.toast(getApplicationContext(), "用户已存在", false);
                            textTip.setText("用户已存在");
                            return;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        NetParse.parseError(RegisterActivity.this, throwable);
                        getLoadingDialog().dismiss();
                    }
                });
    }
}
