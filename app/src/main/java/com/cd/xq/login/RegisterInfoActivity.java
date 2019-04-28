package com.cd.xq.login;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.frame.MainActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.BitmapUtil;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2018/11/11.
 */

public class RegisterInfoActivity extends BaseActivity {
    public static final int FROM_MY = 1; //从我的来
    public static final int FROM_LOGIN = 2;//从登陆页面来
    public static final int FROM_LEAK_INFO = 3;//确实信息
    @BindView(R.id.register_relayout_marrage)
    RelativeLayout registerRelayoutMarrage;
    @BindView(R.id.register_text_marrage)
    TextView registerTextMarrage;
    private int mFrom = FROM_LOGIN;
    private RequestApi mApi;
    private boolean isUpdate;

    @BindView(R.id.register_done)
    TextView registerDone;
    @BindView(R.id.register_btn_close)
    Button registerBtnClose;
    @BindView(R.id.register_img_head)
    ImageView registerImgHead;
    @BindView(R.id.register_text_role)
    TextView registerTextRole;
    @BindView(R.id.register_relayout_role)
    RelativeLayout registerRelayoutRol;
    @BindView(R.id.register_text_nick)
    TextView registerTextNick;
    @BindView(R.id.register_relayout_nick)
    RelativeLayout registerRelayoutNick;
    @BindView(R.id.register_text_gender)
    TextView registerTextGender;
    @BindView(R.id.register_relayout_gender)
    RelativeLayout registerRelayoutGender;
    @BindView(R.id.register_text_age)
    TextView registerTextAge;
    @BindView(R.id.register_relayout_age)
    RelativeLayout registerRelayoutAge;
    @BindView(R.id.register_text_tall)
    TextView registerTextTall;
    @BindView(R.id.register_relayout_tall)
    RelativeLayout registerRelayoutTall;
    @BindView(R.id.register_text_xueli)
    TextView registerTextXueli;
    @BindView(R.id.register_relayout_xueli)
    RelativeLayout registerRelayoutXueli;
    @BindView(R.id.register_text_jiguan)
    TextView registerTextJiguan;
    @BindView(R.id.register_relayout_jiguan)
    RelativeLayout registerRelayoutJiguan;
    @BindView(R.id.register_text_zhiye)
    TextView registerTextZhiye;
    @BindView(R.id.register_relayout_zhiye)
    RelativeLayout registerRelayoutZhiye;
    @BindView(R.id.register_text_gzdd)
    TextView registerTextGzdd;
    @BindView(R.id.register_relayout_gzdd)
    RelativeLayout registerRelayoutGzdd;
    @BindView(R.id.register_edit_ps)
    EditText registerEditPs;

    private String SUFFIX = "  >";
    private UserInfoBean mTempUserInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mFrom = getIntent().getExtras().getInt("from", FROM_LOGIN);
        }

        if (mFrom == FROM_LEAK_INFO || mFrom == FROM_LOGIN) {
            if(mFrom == FROM_LEAK_INFO) {
                registerBtnClose.setVisibility(View.INVISIBLE);
            }
            mTempUserInfo = new UserInfoBean();
        }else {
            //有些不能改变
//            registerRelayoutGender.setEnabled(false);
//            registerRelayoutRol.setEnabled(false);
//            registerRelayoutMarrage.setEnabled(false);

            Gson gson = new Gson();
            mTempUserInfo = gson.fromJson(gson.toJson(DataManager.getInstance().getUserInfo()),UserInfoBean.class);
        }
        init();
    }

    private void init() {
        Retrofit retrofit = NetWorkMg.newRetrofit();
        mApi = retrofit.create(RequestApi.class);

        setData();
//        if(mFrom == FROM_MY) {
//            registerImgHead.setEnabled(false);
//        }
    }

    private void setData() {
        UserInfoBean userInfo = mTempUserInfo;
        if (userInfo == null) {
            return;
        }

        Glide.with(this)
                .load(userInfo.getHead_image())
                .placeholder(R.drawable.chart_room_default_head)
                .dontAnimate()
                .centerCrop()
                .into(registerImgHead);
        if(!TextUtils.isEmpty(userInfo.getGender())) {
            registerTextGender.setText(userInfo.getGender() + (registerRelayoutGender.isEnabled()?SUFFIX:""));
        }
        if(userInfo.getAge() != -1) {
            registerTextAge.setText(String.valueOf(userInfo.getAge()) + (registerRelayoutAge.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getJob_address())) {
            registerTextGzdd.setText(userInfo.getJob_address() + (registerRelayoutGzdd.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getNative_place())) {
            registerTextJiguan.setText(userInfo.getNative_place() + (registerRelayoutJiguan.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getNick_name())) {
            registerTextNick.setText(userInfo.getNick_name() + (registerRelayoutNick.isEnabled()?SUFFIX:""));
        }
        if(userInfo.getTall() != -1) {
            registerTextTall.setText(String.valueOf(userInfo.getTall()) + "cm" + (registerRelayoutTall.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getScholling())) {
            registerTextXueli.setText(userInfo.getScholling() + (registerRelayoutXueli.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getProfessional())) {
            registerTextZhiye.setText(userInfo.getProfessional() + (registerRelayoutZhiye.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getRole_type())) {
            registerTextRole.setText(userInfo.getRole_type() + (registerRelayoutRol.isEnabled()?SUFFIX:""));
        }else {
            registerTextRole.setText("请选择角色" + (registerRelayoutZhiye.isEnabled()?SUFFIX:""));
        }
        if(userInfo.getMarrige() != -1) {
            registerTextMarrage.setText((userInfo.getMarrige() == Constant.ROLE_MARRIED ? "已婚" : "未婚")
                    + (registerRelayoutMarrage.isEnabled()?SUFFIX:""));
        }
        if(!TextUtils.isEmpty(userInfo.getSpecial_info())) {
            registerEditPs.setText(userInfo.getSpecial_info());
        }

        registerImgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
    }

    @OnClick({R.id.register_relayout_role, R.id.register_relayout_nick, R.id.register_relayout_gender, R.id.register_relayout_age, R.id.register_relayout_tall,
            R.id.register_relayout_xueli, R.id.register_relayout_jiguan, R.id.register_relayout_zhiye, R.id.register_relayout_gzdd, R.id.register_img_head,
            R.id.register_relayout_marrage})
    public void onViewClicked(View view) {
        UserInfoBean userInfo = mTempUserInfo;
        switch (view.getId()) {
            case R.id.register_relayout_role: {
                int checkIndex = -1;
                if (userInfo.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                    checkIndex = 0;
                } else if (userInfo.getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                    checkIndex = 1;
                } else if (userInfo.getRole_type().equals(Constant.ROLETYPE_AUDIENCE)) {
                    checkIndex = 2;
                }
                if(userInfo.getMarrige() == Constant.ROLE_MARRIED) {
                    showSelectDialog("角色身份", new String[]{"观众"}, registerTextRole, 0);
                }else {
                    showSelectDialog("角色身份", new String[]{"爱心大使","嘉宾","观众"}, registerTextRole, checkIndex);
                }
            }
            break;
            case R.id.register_relayout_nick:
                showEditDialog("昵称", registerTextNick.getText().toString(), registerTextNick);
                break;
            case R.id.register_relayout_gender: {
                int checkIndex = -1;
                if (userInfo.getGender().equals(Constant.GENDER_MAN)) {
                    checkIndex = 0;
                } else if (userInfo.getGender().equals(Constant.GENDER_LADY)) {
                    checkIndex = 1;
                }
                showSelectDialog("性别", new String[]{"男", "女"}, registerTextGender, checkIndex);
            }
            break;
            case R.id.register_relayout_age:
                showEditDialog("年龄", registerTextAge.getText().toString(), registerTextAge);
                break;
            case R.id.register_relayout_marrage: {
                int checkIndex = -1;
                if (userInfo.getMarrige() == Constant.ROLE_MARRIED) {
                    checkIndex = 0;
                } else if (userInfo.getMarrige() == Constant.ROLE_UNMARRIED) {
                    checkIndex = 1;
                }
                showSelectDialog("当前婚姻", new String[]{"已婚(视作观众身份)", "未婚"}, registerTextMarrage, checkIndex);
            }
            break;
            case R.id.register_relayout_tall:
                showEditDialog("身高", registerTextTall.getText().toString(), registerTextTall);
                break;
            case R.id.register_relayout_xueli: {
                int checkIndex = -1;
                if (userInfo.getScholling().equals(Constant.SCHOOL_BENKE_DOWN)) {
                    checkIndex = 0;
                } else if (userInfo.getScholling().equals(Constant.SCHOOL_BENKE)) {
                    checkIndex = 1;
                } else if (userInfo.getScholling().equals(Constant.SCHOOL_SHUOSHI)) {
                    checkIndex = 2;
                } else if (userInfo.getScholling().equals(Constant.SCHOOL_BOSHI_AND_UP)) {
                    checkIndex = 3;
                }
                showSelectDialog("学历", new String[]{Constant.SCHOOL_BENKE_DOWN, Constant.SCHOOL_BENKE,
                        Constant.SCHOOL_SHUOSHI, Constant.SCHOOL_BOSHI_AND_UP}, registerTextXueli, checkIndex);
            }
            break;
            case R.id.register_relayout_jiguan:
                showEditDialog("籍贯", registerTextJiguan.getText().toString(), registerTextJiguan);
                break;
            case R.id.register_relayout_zhiye:
                showEditDialog("职业", registerTextZhiye.getText().toString(), registerTextZhiye);
                break;
            case R.id.register_relayout_gzdd:
                showEditDialog("工作地点", registerTextGzdd.getText().toString(), registerTextGzdd);
                break;
        }
    }

    @OnClick(R.id.register_done)
    public void onRegisterDoneClicked() {
        updateUserInfo();
    }

    @OnClick(R.id.register_btn_close)
    public void onRegisterBtnCloseClicked() {
        if (isUpdate) {
            setResult(RESULT_OK);
        }
        this.finish();
    }

    private void showEditDialog(String title, String hintText, final View view) {
        String rHintText = hintText.replace(SUFFIX, "");
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_registerinfo_edit_dialog,null);
        final EditText editText = contentView.findViewById(R.id.edit_dialog);
        if (view == registerTextTall
                || view == registerTextAge) {
            rHintText = rHintText.replace("cm", "");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (mFrom == FROM_MY) {
            editText.setText(rHintText);
            editText.setSelection(editText.getText().length());
        } else {
            editText.setHint(rHintText);
        }
        final AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        inputDialog.setTitle(title).setView(contentView);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if(view == registerTextNick) {
                            mTempUserInfo.setNick_name(text);
                        }else if(view == registerTextAge) {
                            int age = 0;
                            try{
                                age = Integer.parseInt(text);
                            }catch (Exception e) {
                            }
                            mTempUserInfo.setAge(age);
                        }else if(view == registerTextTall) {
                            int tall = 0;
                            try{
                                tall = Integer.parseInt(text);
                            }catch (Exception e) {
                            }
                            mTempUserInfo.setTall(tall);
                        }else if(view == registerTextJiguan) {
                            mTempUserInfo.setNative_place(text);
                        }else if(view == registerTextZhiye) {
                            mTempUserInfo.setProfessional(text);
                        }else if(view == registerTextGzdd) {
                            mTempUserInfo.setJob_address(text);
                        }
                        setData();
                        dialog.dismiss();
                    }
                });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = inputDialog.create();
//        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //解决dilaog中EditText无法弹出输入的问题
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //弹出对话框后直接弹出键盘
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 50);
    }

    private void showSelectDialog(String title, final String[] items, final View view, int checkedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)//设置对话框的标题
                .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = items[which];
                        if(view == registerTextRole) {
                            mTempUserInfo.setRole_type(text);
                        }else if(view == registerTextGender) {
                            mTempUserInfo.setGender(text);
                        }else if(view == registerTextMarrage) {
                            if(which==0) {
                                //已婚
                                mTempUserInfo.setMarrige(Constant.ROLE_MARRIED);
                                mTempUserInfo.setRole_type(Constant.ROLETYPE_AUDIENCE);
                            }else {
                                //未婚
                                mTempUserInfo.setMarrige(Constant.ROLE_UNMARRIED);
                                mTempUserInfo.setRole_type("");
                            }
                        }else if(view == registerTextXueli) {
                            mTempUserInfo.setScholling(text);
                        }
                        setData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if(view == registerTextRole) {
            builder.setNeutralButton("角色详情",null);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/role_info.html";
                RegisterInfoDialogActivity.startWeb(RegisterInfoActivity.this,
                        url,
                        "角色详情");
            }
        });
    }


    private void updateUserInfo() {
        UserInfoBean mUserInfo = mTempUserInfo;
        mUserInfo.setSpecial_info(registerEditPs.getText().toString());

        Map<String, Object> params = new HashMap<>();
        params.put("userName", mUserInfo.getUser_name());
        params.put("nickName", mUserInfo.getNick_name());
        params.put("gender", mUserInfo.getGender());
        params.put("age", mUserInfo.getAge());
        params.put("tall", mUserInfo.getTall());
        params.put("scholling", mUserInfo.getScholling());
        params.put("professional", mUserInfo.getProfessional());
        params.put("native_place", mUserInfo.getNative_place());
        params.put("marrige", mUserInfo.getMarrige());
        params.put("phone", mUserInfo.getPhone());
        params.put("job_address", mUserInfo.getJob_address());
        params.put("phone", mUserInfo.getPhone());
        params.put("role_type", mUserInfo.getRole_type());
        params.put("special_info", mUserInfo.getSpecial_info());

        mApi.updateUserInfo(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<UserResp>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        Log.i(new Gson().toJson(userResp));
                        DataManager.getInstance().setUserInfo(userResp.getData());
                        if (mFrom == FROM_LOGIN) {
                            Tools.toast(getApplicationContext(), "注册成功", false);
                            Intent intent = new Intent(RegisterInfoActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Tools.toast(getApplicationContext(), "更新成功", false);
                            setResult(RESULT_OK);
                        }
                        RegisterInfoActivity.this.finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(RegisterInfoActivity.this, throwable.toString(), true);
                    }
                });
    }

    private void openImageSelector() {
        //裁剪图片的回调
        RxGalleryListener
                .getInstance()
                .setRadioImageCheckedListener(new IRadioImageCheckedListener() {
                    @Override
                    public void cropAfter(Object o) {
                        //裁剪结果后，上传图片
                        Log.d("yy", "" + o.toString());
                        String compressImagePath = BitmapUtil.compressImage(o.toString());
                        upLoadHeadImage(compressImagePath, DataManager.getInstance().getUserInfo().getUser_name());
                    }

                    @Override
                    public boolean isActivityFinish() {
                        return true;
                    }
                });

        RxGalleryFinal
                .with(this)
                .image()
                .radio()
                .crop()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        //图片选择结果
                        Log.e("yy", new Gson().toJson(imageRadioResultEvent));
                    }
                })
                .openGallery();
    }

    private void upLoadHeadImage(final String imagePath, String userName) {
        File file = new File(imagePath);

        Map<String, RequestBody> params = new HashMap<>();
        //以下参数是伪代码，参数需要换成自己服务器支持的
        params.put("userName", RequestBody.create(MediaType.parse("text/plain"), userName));

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("uploadFile", file.getName(), requestFile);
        mApi.uploadFile(params, body).enqueue(new Callback<UserResp>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Call<UserResp> call, Response<UserResp> response) {
                if (RegisterInfoActivity.this == null || RegisterInfoActivity.this.isDestroyed()) {
                    return;
                }

                if (response == null || response.body() == null) {
                    Tools.toast(getApplicationContext(), "设置头像失败", true);
                    return;
                }
                if (response.body().getStatus() != XqErrorCode.SUCCESS) {
                    Log.e("yy", response.body().getMsg());
                    Tools.toast(getApplicationContext(), response.body().getMsg(), true);
                    return;
                }
                Tools.toast(getApplicationContext(), "设置头像成功", false);
                DataManager.getInstance().getUserInfo().setHead_image(response.body().getData().getHead_image());
                isUpdate = true;

                //更新头像
                Glide.with(RegisterInfoActivity.this)
                        .load(DataManager.getInstance().getUserInfo().getHead_image())
                        .centerCrop()
                        .bitmapTransform(new GlideCircleTransform(RegisterInfoActivity.this))
                        .into(registerImgHead);

                //删除裁剪的图片
                File file1 = new File(imagePath);
                if (file1.exists()) {
                    file1.delete();
                }
            }

            @Override
            public void onFailure(Call<UserResp> call, Throwable throwable) {
                Log.e("yy", throwable.getMessage());
                Tools.toast(getApplicationContext(), throwable.getMessage(), true);
                //删除裁剪的图片
                File file1 = new File(imagePath);
                if (file1.exists()) {
                    file1.delete();
                }
            }
        });
    }

    private int parseInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {
            Log.e("yy", e.toString());
        }
        return result;
    }
}
