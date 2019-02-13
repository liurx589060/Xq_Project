package com.cd.xq.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cd.xq.AppConstant;
import com.cd.xq.R;
import com.cd.xq.frame.MainActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.BaseResp;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.DialogFactory;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.utils.AppTools;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/11/3.
 */

public class LoginActivity extends BaseActivity {
    public static final int FROM_MY = 1; //从我的来
    public static final int FROM_WELCOME = 2; //从欢迎页面

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
    @BindView(R.id.login_text_privacy)
    TextView loginTextPrivacy;
    @BindView(R.id.login_text_fpassword)
    TextView loginTextFpassword;

    private Dialog mLoadingDialog;
    private RequestApi mApi;
    private int mFrom = FROM_WELCOME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_login);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            mFrom = getIntent().getExtras().getInt("from", FROM_WELCOME);
        }

        mLoadingDialog = DialogFactory.createLoadingDialog(this);
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);

        if(mFrom == FROM_WELCOME) {
            loginBtnClose.setVisibility(View.INVISIBLE);
        }
        init();
    }

    private void init() {
        loginTextPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ProtocolActivity.class);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.login_btn_close, R.id.login_edit_userName, R.id.login_edit_password, R.id.login_btn_login, R.id.login_btn_register, R.id.login_text_fpassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn_close:
                setResult(RESULT_OK);
                this.finish();
                break;
            case R.id.login_edit_userName:
                break;
            case R.id.login_edit_password:
                break;
            case R.id.login_btn_login:
                if(checkEdit()) {
                    toLogin();
                }
                break;
            case R.id.login_btn_register:
                if(checkEdit()) {
                    toRegist();
                }
                break;
            case R.id.login_text_fpassword:
//                Intent intent3 = new Intent(this, MainActivity.class);
//                startActivity(intent3);
                sendSMSCode(this,1);
                break;
        }
    }

    private void toRegist() {
        mLoadingDialog.show();
        mApi.checkUserExist(loginEditUserName.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<BaseResp>bindToLifecycle())
                .subscribe(new Consumer<BaseResp>() {
                    @Override
                    public void accept(BaseResp baseResp) throws Exception {
                        mLoadingDialog.dismiss();
                        if(baseResp.getStatus() == XqErrorCode.ERROR_USER_NOT_EXIST) {
                            sendSMSCode(LoginActivity.this,2);
                        }else if(baseResp.getStatus() == XqErrorCode.SUCCESS){
                            Tools.toast(getApplicationContext(),"用户已存在",false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mLoadingDialog.dismiss();
                        Log.e("toRegist--" + throwable.toString());
                    }
                });
    }

    private void toLogin() {
        mLoadingDialog.show();
        final String userName = loginEditUserName.getText().toString();
        final String password = loginEditPassword.getText().toString();
        Tools.checkUserOrBlack(this,userName, new BlackCheckListener() {
            @Override
            public void onResult(boolean isBlack) {
                mLoadingDialog.dismiss();

                if(!isBlack) {
                    login(userName,password );
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void login(final String userName, String password) {
        final String pss = Tools.MD5(AppConstant.MD5_PREFIX + password);
        mLoadingDialog.show();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> observableEmitter) throws Exception {
                JMessageClient.login(userName, pss
                        , new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i != 0) {
                                    observableEmitter.onError(new Throwable("JMessage login fail--" + s + ">>>" + i));
                                } else {
                                    observableEmitter.onNext(i);
                                }
                            }
                        });
            }
        }).observeOn(Schedulers.io())
                .flatMap(new Function<Integer, ObservableSource<UserResp>>() {
                    @Override
                    public ObservableSource<UserResp> apply(Integer integer) throws Exception {
                        try {
                            return mApi.login(userName, pss);
                        } catch (Exception e) {
                            return Observable.error(e);
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        mLoadingDialog.dismiss();
                        if (userResp.getStatus() == XqErrorCode.SUCCESS) {
                            if(DataManager.getInstance().getUserInfo() != null) {
                                AppTools.notifyFriendOnLine(false);
                            }

                            DataManager.getInstance().setJmUserName(userName);
                            DataManager.getInstance().setUserInfo(userResp.getData());
                            mLoadingDialog.dismiss();
                            saveUser(JMessageClient.getMyInfo().getUserName(), pss, JMessageClient.getMyInfo().getAppKey());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            if (mFrom == FROM_WELCOME) {
                                Tools.toast(getApplicationContext(), "登录成功", false);
                            } else if (mFrom == FROM_MY) {
                                //setResult(RESULT_OK);
                                Tools.toast(getApplicationContext(), "切换账号成功", false);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            }

                            //通知获取好友列表
                            EventBusParam param = new EventBusParam();
                            param.setEventBusCode(EventBusParam.EVENT_BUS_UPDATE_FRIENDLIST);
                            EventBus.getDefault().post(param);

                            if(DataManager.getInstance().getUserInfo() != null) {
                                AppTools.notifyFriendOnLine(true);
                            }

                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else if (userResp.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_WRONG) {
                            Tools.toast(getApplicationContext(), "密码错误", true);
                        } else if (userResp.getStatus() == XqErrorCode.ERROR_USER_NOT_EXIST) {
                            Tools.toast(getApplicationContext(), "用户不存在", true);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mLoadingDialog.dismiss();
                        Tools.toast(getApplicationContext(), throwable.toString(), true);
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void regist(final String userName,String password) {
        mLoadingDialog.show();
        final String pss = Tools.MD5(AppConstant.MD5_PREFIX + password);
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> observableEmitter) throws Exception {
                JMessageClient.register(userName, pss
                        , new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0 || i == 898001) {//成功或者已注册过
                                    observableEmitter.onNext(i);
                                } else {
                                    observableEmitter.onError(new Throwable("JMessage regist error"));
                                }
                            }
                        });
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<Integer, ObservableSource<UserResp>>() {
            @Override
            public ObservableSource<UserResp> apply(Integer integer) throws Exception {
                try {
                    return mApi.regist(userName, pss);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<UserResp>bindToLifecycle())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        mLoadingDialog.dismiss();
                        if (userResp.getStatus() == XqErrorCode.SUCCESS) {//注册成功
                            Tools.toast(getApplicationContext(), "注册成功", false);
                            DataManager.getInstance().setUserInfo(userResp.getData());
                            //跳转到填写详情页面
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("from", RegisterActivity.FROM_LOGIN);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (userResp.getStatus() == XqErrorCode.ERROR_USER_REGIST_EXIST) {
                            Tools.toast(getApplicationContext(), "已存在该账号", true);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mLoadingDialog.dismiss();
                        Tools.toast(getApplicationContext(), "注册失败--" + throwable.toString(), true);
                    }
                });
    }

    private void saveUser(String userName, String password, String appKey) {
        UserInfo userInfo = JMessageClient.getMyInfo();
        Log.i("avatar=" + userInfo.getAvatar());
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString("userName", userName)
                .putString("password", password)
                .putString("appKey", appKey)
                .apply();
    }

    private void sendSMSCode(Context context, final int typeForm) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                    if(typeForm == 1) {
                        //忘记密码
                        Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                        startActivity(intent);
                    }else if(typeForm == 2){
                        //注册
                        regist(loginEditUserName.getText().toString(),loginEditPassword.getText().toString());
                    }
                } else{
                    // TODO 处理错误的结果
                    Tools.toast(getApplicationContext(),"验证失败--" + result,false);
                    Log.e("sendSMSCode---" + result);
                }
            }
        });
        page.show(context);
    }

    private boolean checkEdit() {
        if(TextUtils.isEmpty(loginEditUserName.getText().toString()) || TextUtils.isEmpty(loginEditPassword.getText().toString())) {
            Tools.toast(getApplicationContext(),"用户名和密码不能为空",false);
            return false;
        }
        return true;
    }
}
