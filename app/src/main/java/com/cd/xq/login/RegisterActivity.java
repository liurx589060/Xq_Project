package com.cd.xq.login;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.frame.MainActivity;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
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

public class RegisterActivity extends BaseActivity {
    public static final int FROM_MY = 1; //从我的来
    public static final int FROM_LOGIN = 2;//从登陆页面来
    public static final int FROM_LEAK_INFO = 3;//确实信息
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mFrom = getIntent().getExtras().getInt("from", FROM_LOGIN);
        }

        if (mFrom == FROM_LEAK_INFO) {
            registerBtnClose.setVisibility(View.INVISIBLE);
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
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }

        Glide.with(this)
                .load(userInfo.getHead_image())
                .centerCrop()
                .bitmapTransform(new GlideCircleTransform(this))
                .into(registerImgHead);
        registerTextGender.setText(userInfo.getGender() + SUFFIX);
        registerTextAge.setText(String.valueOf(userInfo.getAge()) + SUFFIX);
        registerTextGzdd.setText(userInfo.getJob_address() + SUFFIX);
        registerTextJiguan.setText(userInfo.getNative_place() + SUFFIX);
        registerTextNick.setText(userInfo.getNick_name() + SUFFIX);
        registerTextTall.setText(String.valueOf(userInfo.getTall()) + "cm" + SUFFIX);
        registerTextXueli.setText(userInfo.getScholling() + SUFFIX);
        registerTextZhiye.setText(userInfo.getProfessional() + SUFFIX);
        registerTextRole.setText(userInfo.getRole_type() + SUFFIX);
        registerEditPs.setText(userInfo.getSpecial_info());
    }

    @OnClick({R.id.register_relayout_role, R.id.register_relayout_nick, R.id.register_relayout_gender, R.id.register_relayout_age, R.id.register_relayout_tall, R.id.register_relayout_xueli, R.id.register_relayout_jiguan, R.id.register_relayout_zhiye, R.id.register_relayout_gzdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_relayout_role:
                showSelectDialog("角色", new String[]{"angel", "guest"}, registerTextRole);
                break;
            case R.id.register_relayout_nick:
                showEditDialog("昵称", registerTextNick.getText().toString(), registerTextNick);
                break;
            case R.id.register_relayout_gender:
                showSelectDialog("性别", new String[]{"男", "女"}, registerTextGender);
                break;
            case R.id.register_relayout_age:
                showEditDialog("年龄", registerTextAge.getText().toString(), registerTextAge);
                break;
            case R.id.register_relayout_tall:
                showEditDialog("身高", registerTextTall.getText().toString(), registerTextTall);
                break;
            case R.id.register_relayout_xueli:
                showSelectDialog("学历", new String[]{"本科以下", "本科", "硕士", "博士及以上"}, registerTextXueli);
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

    @OnClick(R.id.register_img_head)
    public void onViewClicked() {
        openImageSelector();
    }

    private void showEditDialog(String title, String hintText, final View view) {
        String rHintText = hintText.replace(SUFFIX, "");
        final EditText editText = new EditText(this);
        if (view == registerTextTall) {
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
        inputDialog.setTitle(title).setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text;
                        text = editText.getText().toString();

                        if (view == registerTextTall) {
                            text += "cm" + SUFFIX;
                        } else {
                            text += SUFFIX;
                        }
                        if (view instanceof TextView) {
                            ((TextView) view).setText(text);
                        }
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showSelectDialog(String title, final String[] items, final View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)//设置对话框的标题
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (view instanceof TextView) {
                            ((TextView) view).setText(items[which] + SUFFIX);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }


    private void updateUserInfo() {
        UserInfoBean mUserInfo = DataManager.getInstance().getUserInfo();
        mUserInfo.setNick_name(registerTextNick.getText().toString().replace(SUFFIX, ""));
        mUserInfo.setAge(parseInt(registerTextAge.getText().toString().replace(SUFFIX, "")));
        mUserInfo.setGender(registerTextGender.getText().toString().replace(SUFFIX, ""));
        mUserInfo.setJob_address(registerTextGzdd.getText().toString().replace(SUFFIX, ""));
        int marrige = 0;
//        if(mEditMarrige.getText().toString().contains("已婚")) {
//            marrige = 1;
//        }
        mUserInfo.setMarrige(marrige);
        mUserInfo.setNative_place(registerTextJiguan.getText().toString().replace(SUFFIX, ""));
        mUserInfo.setPhone("");
        mUserInfo.setProfessional(registerTextZhiye.getText().toString().replace(SUFFIX, ""));
        mUserInfo.setScholling(registerTextXueli.getText().toString().replace(SUFFIX, ""));
        mUserInfo.setTall(parseInt(registerTextTall.getText().toString().replace("cm", "").replace(SUFFIX, "")));
        mUserInfo.setRole_type(registerTextRole.getText().toString().replace(SUFFIX, ""));
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
        params.put("job_address", mUserInfo.getJob_address());
        params.put("phone", mUserInfo.getPhone());
        params.put("role_type", mUserInfo.getRole_type());
        params.put("special_info", mUserInfo.getSpecial_info());

        mApi.updateUserInfo(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        Log.i(new Gson().toJson(userResp));
                        DataManager.getInstance().setUserInfo(userResp.getData());
                        if (mFrom == FROM_LOGIN) {
                            Tools.toast(getApplicationContext(), "注册成功", false);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Tools.toast(getApplicationContext(), "更新成功", false);
                            setResult(RESULT_OK);
                        }
                        RegisterActivity.this.finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(RegisterActivity.this, throwable.toString(), true);
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
                        upLoadHeadImage(o.toString(), DataManager.getInstance().getUserInfo().getUser_name());
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
                if (RegisterActivity.this == null || RegisterActivity.this.isDestroyed()) {
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
                Glide.with(RegisterActivity.this)
                        .load(DataManager.getInstance().getUserInfo().getHead_image())
                        .centerCrop()
                        .bitmapTransform(new GlideCircleTransform(RegisterActivity.this))
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
