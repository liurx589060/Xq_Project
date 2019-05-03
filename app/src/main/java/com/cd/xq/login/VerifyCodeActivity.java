package com.cd.xq.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.Tools;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 验证手机验证码
 */
public class VerifyCodeActivity extends BaseActivity {
    public final static int REQUEST_REGISTER = 0x10;   //注册
    public final static int REQUEST_CHANGE_PASS = 0x11; //修改密码

    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.text_btn_code)
    TextView textBtnCode;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.btn_verify_code)
    Button btnVerifyCode;
    @BindView(R.id.text_tip)
    TextView textTip;

    private Handler mHandler;
    private final int TOTAL_COUNT = 60;
    private int mCurrentTime = 0;
    private Runnable mTimeRunnable;
    private EventHandler mSMSEventHandler;
    private int mRequestCode;
    private String mUserName;

    /**
     * @param phoneNum 传入指定手机号   不指定则传null
     */
    public static void startVerify(Activity activity, int requestCode, String phoneNum) {
        Intent intent = new Intent(activity, VerifyCodeActivity.class);
        Bundle bundle = new Bundle();
        if (phoneNum != null) {
            bundle.putString("phoneNum", phoneNum);
        }
        bundle.putInt("requestCode", requestCode);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * @param phoneNum 传入指定手机号   不指定则传null
     */
    public static void startVerify(Activity activity, int requestCode, String phoneNum, String userName) {
        Intent intent = new Intent(activity, VerifyCodeActivity.class);
        Bundle bundle = new Bundle();
        if (phoneNum != null) {
            bundle.putString("phoneNum", phoneNum);
            bundle.putString("userName", userName);
        }
        bundle.putInt("requestCode", requestCode);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        ButterKnife.bind(this);
        mHandler = new Handler();

        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String phoneNum = bundle.getString("phoneNum");
            mUserName = bundle.getString("userName");
            mRequestCode = bundle.getInt("requestCode");
            if (phoneNum != null) {
                editPhone.setText(phoneNum);
                editPhone.setEnabled(false);
            }
        }
        btnVerifyCode.setEnabled(false);

        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editCode.getText().toString())) {
                    btnVerifyCode.setEnabled(false);
                } else {
                    btnVerifyCode.setEnabled(true);
                }
            }
        });
        initSMSEventHandler();
    }

    private void initSMSEventHandler() {
        mSMSEventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                new Handler(Looper.getMainLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理成功得到验证码的结果
                                // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                                Tools.toast(getApplicationContext(), "验证码发送成功", false);
                            } else {
                                // TODO 处理错误的结果
                                ((Throwable) data).printStackTrace();
                                Tools.toast(getApplicationContext(), "验证码发送失败", false);
                            }
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理验证码验证通过的结果
                                HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                                String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                                String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”

                                DataManager.getInstance().getRegisterUserInfo().setPhone(phone);
                                if (mRequestCode == REQUEST_REGISTER) {
                                    //注册
                                    Intent intent = new Intent(VerifyCodeActivity.this, RegisterInfoActivity.class);
                                    startActivity(intent);
                                } else if (mRequestCode == REQUEST_CHANGE_PASS) {
                                    Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("userName", mUserName);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                                finish();
                            } else {
                                // TODO 处理错误的结果
                                ((Throwable) data).printStackTrace();
                                Tools.toast(getApplicationContext(), "验证码错误", false);
                                textTip.setText("验证码错误");
                            }
                        }
                        // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                        return false;
                    }
                }).sendMessage(msg);
            }
        };
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(mSMSEventHandler);
    }

    private void setTextBtnCodeStatus(boolean isActive) {
        if (isActive) {
            textBtnCode.setTextColor(Color.parseColor("#32b7b9"));
        } else {
            textBtnCode.setTextColor(Color.parseColor("#aaaaaa"));
        }
        textBtnCode.setEnabled(isActive);
    }

    private void startTimeCountDown() {
        if (mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    int time = TOTAL_COUNT - mCurrentTime;
                    if (time > 0) {
                        textBtnCode.setText(String.valueOf(time + " S"));
                        setTextBtnCodeStatus(false);
                        mCurrentTime++;
                        mHandler.postDelayed(this, 1000);
                    } else {
                        textBtnCode.setText("获取验证码");
                        setTextBtnCodeStatus(true);
                        mCurrentTime = 0;
                        mHandler.removeCallbacks(this);
                    }
                }
            };
        }
        mHandler.removeCallbacks(mTimeRunnable);
        mHandler.post(mTimeRunnable);
    }

    private void requestSMSCode(final String country, final String phone) {
        if(!Tools.isTelPhoneNumber(phone)) {
            Tools.toast(getApplicationContext(),"非法的手机号码",false);
            return;
        }
        if (mRequestCode == REQUEST_REGISTER) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("获取验证码")
                    .setMessage("此手机号码将作为往后修改密码的手机号码，且同意此手机号作为本人的联系方式")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startTimeCountDown();
                            SMSSDK.getVerificationCode(country, phone);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        } else if (mRequestCode == REQUEST_CHANGE_PASS) {
            startTimeCountDown();
            SMSSDK.getVerificationCode(country, phone);
        }
    }

    @OnClick({R.id.btn_back, R.id.text_btn_code, R.id.btn_verify_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.text_btn_code:
                requestSMSCode("86", editPhone.getText().toString());
                break;
            case R.id.btn_verify_code:
                // 提交验证码，其中的code表示验证码，如“1357”
                textTip.setText("");
                SMSSDK.submitVerificationCode("86", editPhone.getText().toString(), editCode.getText().toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeRunnable != null) {
            mHandler.removeCallbacks(mTimeRunnable);
        }
        SMSSDK.unregisterEventHandler(mSMSEventHandler);
    }
}
