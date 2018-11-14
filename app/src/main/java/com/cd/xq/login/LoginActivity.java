package com.cd.xq.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.frame.MainActivity;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.util.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/11/3.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_img_icon)
    ImageView loginImgIcon;
    @BindView(R.id.login_btn_close)
    Button loginBtnClose;
    @BindView(R.id.login_edit_userName)
    EditText loginEditUserName;
    @BindView(R.id.login_edit_password)
    EditText loginEditPassword;
    @BindView(R.id.login_btn_login)
    Button loginBtnLogin;
    @BindView(R.id.login_btn_register)
    Button loginBtnRegister;
    @BindView(R.id.login_btn_forget_password)
    Button loginBtnForgetPassword;
    @BindView(R.id.login_img_wx)
    ImageView loginImgWx;
    @BindView(R.id.login_text_privacy)
    TextView loginTextPrivacy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login_btn_close, R.id.login_edit_userName, R.id.login_edit_password, R.id.login_btn_login, R.id.login_btn_register, R.id.login_btn_forget_password, R.id.login_img_wx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn_close:
                break;
            case R.id.login_edit_userName:
                break;
            case R.id.login_edit_password:
                break;
            case R.id.login_btn_login:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.login_btn_register:
                Intent intent1 = new Intent(this,RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.login_btn_forget_password:
                break;
            case R.id.login_img_wx:
                Intent intent2 = new Intent(this, ChartRoomActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
