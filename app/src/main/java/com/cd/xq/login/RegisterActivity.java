package com.cd.xq.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

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

    @OnClick({R.id.notify_btn_back, R.id.btn_verify_phone,R.id.text_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.notify_btn_back:
                finish();
                break;
            case R.id.btn_verify_phone:
                dialogConfirmVerifyPhone();
                break;
            case R.id.text_protocol:
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
                DefaultWebActivity.startWeb(this,url,"用户协议");
                break;
        }
    }

    /**
     * 确认验证对话框
     */
    private void dialogConfirmVerifyPhone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("验证的手机号将与注册的账号绑定，将作为本人的联系方式以及以后用于密码找回")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendSMSCode(RegisterActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    /**
     * 验证手机号
     *
     * @param context
     */
    private void sendSMSCode(Context context) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                    DataManager.getInstance().getRegisterUserInfo().setPhone(phone);
                    DataManager.getInstance().getRegisterUserInfo().setUser_name(editUserName.getText().toString());
                    DataManager.getInstance().getRegisterUserInfo().setPassword(editPassword.getText().toString());

                    Intent intent = new Intent(RegisterActivity.this, RegisterInfoActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // TODO 处理错误的结果
                    Tools.toast(getApplicationContext(), "验证失败--" + result, false);
                    Log.e("sendSMSCode---" + result);
                }
            }
        });
        page.show(context);
    }
}
