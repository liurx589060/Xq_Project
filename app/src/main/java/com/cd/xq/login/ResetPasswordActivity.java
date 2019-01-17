package com.cd.xq.login;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cd.xq.AppConstant;
import com.cd.xq.R;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.DialogFactory;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2019/1/12.
 */

public class ResetPasswordActivity extends BaseActivity {
    @BindView(R.id.reset_password_edit_ps1)
    EditText resetPasswordEditPs1;
    @BindView(R.id.reset_password_edit_ps2)
    EditText resetPasswordEditPs2;
    @BindView(R.id.reset_password_btn_commit)
    Button resetPasswordBtnCommit;
    @BindView(R.id.reset_password_btn_close)
    Button resetPasswordBtnClose;
    @BindView(R.id.reset_password_edit_userName)
    EditText resetPasswordEditUserName;

    private XqRequestApi mApi;
    private Dialog mLoadingDialog;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_reset_password);
        ButterKnife.bind(this);

        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        mLoadingDialog = DialogFactory.createLoadingDialog(this);
    }

    private void commitResetPassword() {
        String password_1 = resetPasswordEditPs1.getText().toString();
        String password_2 = resetPasswordEditPs2.getText().toString();
        String userName = resetPasswordEditUserName.getText().toString();
        boolean isReturn = false;
        if (TextUtils.isEmpty(userName)) {
            Tools.toast(getApplicationContext(), "用户名不能为空", true);
            isReturn = true;
        }

        if (TextUtils.isEmpty(password_1) || TextUtils.isEmpty(password_2)) {
            Tools.toast(getApplicationContext(), "密码不能为空", true);
            isReturn = true;
        }

        if (false == password_1.equals(password_2)) {
            Tools.toast(getApplicationContext(), "两次密码输入不同", true);
            isReturn = true;
        }

        if (isReturn) {
            return;
        }

        mLoadingDialog.show();
        String password = Tools.MD5(AppConstant.MD5_PREFIX + password_1);
        mApi.changePassword(userName,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<String>>() {
                    @Override
                    public void accept(NetResult<String> stringNetResult) throws Exception {
                        mLoadingDialog.dismiss();
                        if(stringNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(),"修改密码失败--" + stringNetResult.getMsg(),false);
                            return;
                        }

                        Tools.toast(getApplicationContext(),"密码修改成功",false);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mLoadingDialog.dismiss();
                        Tools.toast(getApplicationContext(),"修改密码失败",false);
                        Log.e("commitResetPassword--" + throwable.toString());
                    }
                });
    }

    @OnClick({R.id.reset_password_btn_commit, R.id.reset_password_btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reset_password_btn_commit:
                commitResetPassword();
                break;
            case R.id.reset_password_btn_close:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingDialog.dismiss();
    }
}
