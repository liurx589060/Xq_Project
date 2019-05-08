package com.cd.xq.login;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.cd.xq.AppConstant;
import com.cd.xq.R;
import com.cd.xq.beans.ProvinceJsonBean;
import com.cd.xq.frame.MainActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.BitmapUtil;
import com.cd.xq.module.util.tools.IDCardUtils;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
    @BindView(R.id.register_text_real_name)
    TextView registerTextRealName;
    @BindView(R.id.register_relayout_real_name)
    RelativeLayout registerRelayoutRealName;
    @BindView(R.id.register_text_id_card)
    TextView registerTextIdCard;
    @BindView(R.id.register_relayout_id_card)
    RelativeLayout registerRelayoutIdCard;
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
    private final String STR_REGISTER = "注册";
    private final String STR_EDIT = "编辑";
    private final String STR_UODATE = "更新";

    private List<ProvinceJsonBean> options1Items;
    private ArrayList<ArrayList<String>> options2Items;
    private ArrayList<ArrayList<ArrayList<String>>> options3Items;
    private boolean mIsProvinceLoaded;
    private boolean mIsSelectedMarried;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mFrom = getIntent().getExtras().getInt("from", FROM_LOGIN);
        }

        if (mFrom == FROM_LEAK_INFO || mFrom == FROM_LOGIN) {
            mTempUserInfo = DataManager.getInstance().getRegisterUserInfo();
            if (mFrom == FROM_LEAK_INFO) {
                registerBtnClose.setVisibility(View.INVISIBLE);
                setStatus(STR_EDIT);
            }
            if (mFrom == FROM_LOGIN) {
                setStatus(STR_REGISTER);
            }
        } else {
            //有些不能改变
            Gson gson = new Gson();
            mTempUserInfo = gson.fromJson(gson.toJson(DataManager.getInstance().getUserInfo()), UserInfoBean.class);
            setStatus(STR_EDIT);
            registerRelayoutRealName.setVisibility(View.GONE);
            registerRelayoutIdCard.setVisibility(View.GONE);
        }
        init();
        getProvinceJsonData(null);
    }

    private void setStatus(String str) {
        registerDone.setText(str);
        if (registerDone.getText().toString().equals(STR_EDIT)) {
            //编辑，状态不可用
            registerRelayoutRol.setEnabled(false);
            registerRelayoutNick.setEnabled(false);
            registerRelayoutGender.setEnabled(false);
            registerRelayoutMarrage.setEnabled(false);
            registerRelayoutAge.setEnabled(false);
            registerRelayoutTall.setEnabled(false);
            registerRelayoutXueli.setEnabled(false);
            registerRelayoutJiguan.setEnabled(false);
            registerRelayoutZhiye.setEnabled(false);
            registerRelayoutGzdd.setEnabled(false);
            registerEditPs.setEnabled(false);
        } else if (registerDone.getText().toString().equals(STR_REGISTER)) {
            registerRelayoutRol.setEnabled(true);
            registerRelayoutNick.setEnabled(true);
            registerRelayoutGender.setEnabled(true);
            registerRelayoutMarrage.setEnabled(true);
            registerRelayoutAge.setEnabled(true);
            registerRelayoutTall.setEnabled(true);
            registerRelayoutXueli.setEnabled(true);
            registerRelayoutJiguan.setEnabled(true);
            registerRelayoutZhiye.setEnabled(true);
            registerRelayoutGzdd.setEnabled(true);
            registerEditPs.setEnabled(true);
        } else if (registerDone.getText().toString().equals(STR_UODATE)) {
//            registerRelayoutRol.setEnabled(false);
            registerRelayoutRol.setEnabled(true);
            registerRelayoutNick.setEnabled(true);
//            registerRelayoutGender.setEnabled(false);
//            registerRelayoutMarrage.setEnabled(false);
            registerRelayoutGender.setEnabled(true);
            registerRelayoutMarrage.setEnabled(true);
            registerRelayoutAge.setEnabled(true);
            registerRelayoutTall.setEnabled(true);
            registerRelayoutXueli.setEnabled(true);
            registerRelayoutJiguan.setEnabled(true);
            registerRelayoutZhiye.setEnabled(true);
            registerRelayoutGzdd.setEnabled(true);
            registerEditPs.setEnabled(true);
        }
        setData();
    }

    private void getProvinceJsonData(final Runnable doneRunnable) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //读取数据
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    AssetManager assetManager = getAssets();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(
                            assetManager.open("province.json")));
                    String line;
                    while ((line = bf.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String jsonStr = stringBuilder.toString();
                    ArrayList<ProvinceJsonBean> provinceJsonBean = parseData(jsonStr);//用Gson 转成实体
                    options1Items = provinceJsonBean;
                    for (int i = 0; i < provinceJsonBean.size(); i++) {//遍历省份
                        ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
                        ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                        for (int c = 0; c < provinceJsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                            String cityName = provinceJsonBean.get(i).getCityList().get(c).getName();
                            cityList.add(cityName);//添加城市
                            ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                            city_AreaList.addAll(provinceJsonBean.get(i).getCityList().get(c).getArea());
                            province_AreaList.add(city_AreaList);//添加该省所有地区数据
                        }

                        /**
                         * 添加城市数据
                         */
                        options2Items.add(cityList);

                        /**
                         * 添加地区数据
                         */
                        options3Items.add(province_AreaList);

                        mIsProvinceLoaded = true;
                        if (doneRunnable != null) {
                            runOnUiThread(doneRunnable);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("parseData--" + e.toString());
                }
            }
        };
        new Thread(runnable).start();
    }

    private ArrayList<ProvinceJsonBean> parseData(String result) {//Gson 解析
        ArrayList<ProvinceJsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ProvinceJsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), ProvinceJsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parseData--" + e.toString());
        }
        return detail;
    }

    private void init() {
        options1Items = new ArrayList<>();
        options2Items = new ArrayList<>();
        options3Items = new ArrayList<>();

        Retrofit retrofit = NetWorkMg.newRetrofit();
        mApi = retrofit.create(RequestApi.class);

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
        if (!TextUtils.isEmpty(userInfo.getGender())) {
            registerTextGender.setText(userInfo.getGender() + (registerRelayoutGender.isEnabled() ? SUFFIX : ""));
        }
        if (userInfo.getAge() != -1) {
            registerTextAge.setText(String.valueOf(userInfo.getAge()) + (registerRelayoutAge.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getJob_address())) {
            registerTextGzdd.setText(userInfo.getJob_address() + (registerRelayoutGzdd.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getNative_place())) {
            registerTextJiguan.setText(userInfo.getNative_place() + (registerRelayoutJiguan.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getNick_name())) {
            registerTextNick.setText(userInfo.getNick_name() + (registerRelayoutNick.isEnabled() ? SUFFIX : ""));
        }
        if (userInfo.getTall() != -1) {
            registerTextTall.setText(String.valueOf(userInfo.getTall()) + "cm" + (registerRelayoutTall.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getScholling())) {
            registerTextXueli.setText(userInfo.getScholling() + (registerRelayoutXueli.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getProfessional())) {
            registerTextZhiye.setText(userInfo.getProfessional() + (registerRelayoutZhiye.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getRole_type())) {
            registerTextRole.setText(userInfo.getRole_type() + (registerRelayoutRol.isEnabled() ? SUFFIX : ""));
        } else {
            registerTextRole.setText("请选择角色" + (registerRelayoutZhiye.isEnabled() ? SUFFIX : ""));
        }
        if (userInfo.getMarrige() != -1) {
            registerTextMarrage.setText((userInfo.getMarrige() == Constant.ROLE_MARRIED ? "已婚" : "未婚")
                    + (registerRelayoutMarrage.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getSpecial_info())) {
            registerEditPs.setText(userInfo.getSpecial_info());
        }
        if (!TextUtils.isEmpty(userInfo.getReal_name())) {
            registerTextRealName.setText(userInfo.getReal_name() + (registerTextRealName.isEnabled() ? SUFFIX : ""));
        }
        if (!TextUtils.isEmpty(userInfo.getId_card())) {
            registerTextIdCard.setText(userInfo.getId_card() + (registerTextRealName.isEnabled() ? SUFFIX : ""));
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
            R.id.register_relayout_marrage,R.id.register_relayout_real_name, R.id.register_relayout_id_card})
    public void onViewClicked(View view) {
        UserInfoBean userInfo = mTempUserInfo;
        switch (view.getId()) {
            case R.id.register_relayout_real_name: {
                showEditDialog("姓名", registerTextRealName.getText().toString(), registerTextRealName);
            }
            break;
            case R.id.register_relayout_id_card: {
                showEditDialog("身份证", registerTextIdCard.getText().toString(), registerTextIdCard);
            }
            break;
            case R.id.register_relayout_role: {
                int checkIndex = -1;
                if (userInfo.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                    checkIndex = 0;
                } else if (userInfo.getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                    checkIndex = 1;
                } else if (userInfo.getRole_type().equals(Constant.ROLETYPE_AUDIENCE)) {
                    checkIndex = 2;
                }
                if (userInfo.getMarrige() == Constant.ROLE_MARRIED) {
                    showSelectDialog("角色身份", new String[]{"观众"}, registerTextRole, 0);
                } else {
                    showSelectDialog("角色身份", new String[]{"爱心大使", "嘉宾", "观众"}, registerTextRole, checkIndex);
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
                doCitySelectDialog(registerTextJiguan);
                break;
            case R.id.register_relayout_zhiye:
                showEditDialog("职业", registerTextZhiye.getText().toString(), registerTextZhiye);
                break;
            case R.id.register_relayout_gzdd:
                doCitySelectDialog(registerTextGzdd);
                //showEditDialog("工作地点", registerTextGzdd.getText().toString(), registerTextGzdd);
                break;
        }
    }

    @OnClick(R.id.register_done)
    public void onRegisterDoneClicked() {
        if (registerDone.getText().toString().equals(STR_EDIT)) {
            setStatus(STR_UODATE);
        } else if (registerDone.getText().toString().equals(STR_UODATE)) {
            updateUserInfo();
        } else if (registerDone.getText().toString().equals(STR_REGISTER)) {
            toRegister();
        }
    }

    @OnClick(R.id.register_btn_close)
    public void onRegisterBtnCloseClicked() {
        if (isUpdate) {
            setResult(RESULT_OK);
        }

        if (mFrom == FROM_LOGIN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("取消注册")
                    .setMessage("是否确定放弃注册，还是继续完成注册？")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            DataManager.getInstance().setRegisterUserInfo(null);
                        }
                    });
            builder.create().show();
        } else {
            this.finish();
        }
    }

    private void doCitySelectDialog(final TextView textView) {
        if (mIsProvinceLoaded) {
            showCitySelectDialog(textView);
        } else {
            getProvinceJsonData(new Runnable() {
                @Override
                public void run() {
                    showCitySelectDialog(textView);
                }
            });
        }
    }

    private void showCitySelectDialog(final TextView textView) {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
                if (textView == registerTextJiguan) {
                    mTempUserInfo.setNative_place(tx);
                } else if (textView == registerTextGzdd) {
                    mTempUserInfo.setJob_address(tx);
                }
                setData();
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void showEditDialog(String title, String hintText, final View view) {
        String rHintText = hintText.replace(SUFFIX, "");
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_registerinfo_edit_dialog, null);
        final EditText editText = contentView.findViewById(R.id.edit_dialog);
        if (view == registerTextTall
                || view == registerTextAge) {
            rHintText = rHintText.replace("cm", "");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if(view == registerTextIdCard) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
            editText.setKeyListener(new NumberKeyListener() {
                @Override
                public int getInputType() {
                    return android.text.InputType.TYPE_CLASS_PHONE;
                }

                @Override
                protected char[] getAcceptedChars() {
                    char[] numberChars = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'X' };
                    return numberChars;
                }
            });
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
                        if (view == registerTextNick) {
                            mTempUserInfo.setNick_name(text);
                        } else if (view == registerTextAge) {
                            int age = 0;
                            try {
                                age = Integer.parseInt(text);
                            } catch (Exception e) {
                            }
                            mTempUserInfo.setAge(age);
                        } else if (view == registerTextTall) {
                            int tall = 0;
                            try {
                                tall = Integer.parseInt(text);
                            } catch (Exception e) {
                            }
                            mTempUserInfo.setTall(tall);
                        } else if (view == registerTextJiguan) {
                            mTempUserInfo.setNative_place(text);
                        } else if (view == registerTextZhiye) {
                            mTempUserInfo.setProfessional(text);
                        } else if (view == registerTextGzdd) {
                            mTempUserInfo.setJob_address(text);
                        } else if(view == registerTextRealName) {
                            mTempUserInfo.setReal_name(text);
                        }else if(view == registerTextIdCard) {
                            mTempUserInfo.setId_card(text);
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
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        if (view == registerTextRole) {
                            mTempUserInfo.setRole_type(text);
                        } else if (view == registerTextGender) {
                            mTempUserInfo.setGender(text);
                        } else if (view == registerTextMarrage) {
                            if (which == 0) {
                                //已婚
                                mTempUserInfo.setMarrige(Constant.ROLE_MARRIED);
                                mTempUserInfo.setRole_type(Constant.ROLETYPE_AUDIENCE);
                                mIsSelectedMarried = true;
                            } else {
                                //未婚
                                mTempUserInfo.setMarrige(Constant.ROLE_UNMARRIED);
                                if (mIsSelectedMarried) {
                                    mTempUserInfo.setRole_type("");
                                    mIsSelectedMarried = false;
                                }
                            }
                        } else if (view == registerTextXueli) {
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
        if (view == registerTextRole) {
            builder.setNeutralButton("角色详情", null);
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

    private boolean checkInfoToRegister() {
        if (TextUtils.isEmpty(mTempUserInfo.getRole_type())
                || TextUtils.isEmpty(mTempUserInfo.getNick_name())
                || TextUtils.isEmpty(mTempUserInfo.getGender())
                || mTempUserInfo.getMarrige() == -1
                || mTempUserInfo.getAge() == -1
                || mTempUserInfo.getTall() == -1
                || TextUtils.isEmpty(mTempUserInfo.getScholling())
                || TextUtils.isEmpty(mTempUserInfo.getNative_place())
                || TextUtils.isEmpty(mTempUserInfo.getProfessional())
                || TextUtils.isEmpty(mTempUserInfo.getJob_address())) {
            Tools.toast(getApplicationContext(), "请完善资料在提交注册", false);
            return false;
        }

        if(mFrom == FROM_LOGIN) {
            if(!Tools.isLegalName(mTempUserInfo.getReal_name())) {
                Tools.toast(getApplicationContext(), "非法的中文姓名", false);
                return false;
            }

            try {
                if(!IDCardUtils.IDCardValidate(mTempUserInfo.getId_card())) {
                    Tools.toast(getApplicationContext(), "非法的身份证号码", false);
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (TextUtils.isEmpty(mTempUserInfo.getHead_image())) {
            Tools.toast(getApplicationContext(), "请先上传头像", false);
            return false;
        }
        return true;
    }

    private void toRegister() {
        if (!checkInfoToRegister()) return;

        //先注册
        final String pss = Tools.MD5(AppConstant.MD5_PREFIX + mTempUserInfo.getPassword());
        getLoadingDialog().show();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> observableEmitter) throws Exception {
                JMessageClient.register(mTempUserInfo.getUser_name(), pss
                        , new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0 || i == 898001) {//成功或者已注册过
                                    observableEmitter.onNext(i);
                                    saveUser(JMessageClient.getMyInfo().getUserName(), pss, JMessageClient.getMyInfo().getAppKey());
                                } else {
                                    observableEmitter.onError(new Throwable("JMessage regist error--" + s));
                                }
                            }
                        });
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<Integer, ObservableSource<NetResult>>() {
            @Override
            public ObservableSource<NetResult> apply(Integer integer) throws Exception {
                HashMap<String,Object> params = new HashMap<>();
                params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
                params.put("name",mTempUserInfo.getReal_name());
                params.put("idCard",mTempUserInfo.getId_card());
                params.put("handleType",1);
                return mApi.checkIDCard(params);
            }
        }).flatMap(new Function<NetResult, ObservableSource<UserResp>>() {
            @Override
            public ObservableSource<UserResp> apply(NetResult netResult) throws Exception {
                try {
                    if(netResult.getStatus() != XqErrorCode.SUCCESS) {
                        Tools.toast(getApplicationContext(),"身份证和姓名未能匹配",false);
                        return Observable.error(new Throwable("身份证和姓名未能匹配"));
                    }
                    return mApi.regist(mTempUserInfo.getUser_name(), pss);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        getLoadingDialog().dismiss();
                        if (userResp.getStatus() == XqErrorCode.SUCCESS) {//注册成功
                            //updateUserInfo();
                            //上次图片
                            upLoadHeadImage(mTempUserInfo.getHead_image(), userResp.getData().getUser_name());
                        } else {
                            Log.e("toRegister--" + userResp.getMsg());
                            Tools.toast(getApplicationContext(), "注册失败", false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("toRegister--" + throwable.toString());
                        Tools.toast(getApplicationContext(), "注册失败", false);
                        getLoadingDialog().dismiss();
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


    private void updateUserInfo() {
        if (!checkInfoToRegister()) return;

        getLoadingDialog().show();
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
        params.put("real_name", mUserInfo.getReal_name());
        params.put("id_card", mUserInfo.getId_card());
        if(mFrom == FROM_LOGIN) {
            params.put("handleType",1); //注册
        }else {
            params.put("handleType",2);  //更新
        }

        mApi.updateUserInfo(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        getLoadingDialog().dismiss();
                        Log.i(new Gson().toJson(userResp));
                        DataManager.getInstance().setUserInfo(userResp.getData());
                        DataManager.getInstance().setRegisterUserInfo(null);
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
                        getLoadingDialog().dismiss();
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
                        mTempUserInfo.setHead_image(compressImagePath);
                        if (mFrom == FROM_MY || mFrom == FROM_LEAK_INFO) {
                            upLoadHeadImage(compressImagePath, DataManager.getInstance().getUserInfo().getUser_name());
                        } else {
                            File file = new File(o.toString());
                            //更新头像
                            Glide.with(RegisterInfoActivity.this)
                                    .load(file)
                                    .centerCrop()
                                    .bitmapTransform(new GlideCircleTransform(RegisterInfoActivity.this))
                                    .into(registerImgHead);
                        }
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
        getLoadingDialog().show();
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
                getLoadingDialog().dismiss();
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
                if (mFrom == FROM_LEAK_INFO || mFrom == FROM_MY) {
                    Tools.toast(getApplicationContext(), "设置头像成功", false);
                    DataManager.getInstance().getUserInfo().setHead_image(response.body().getData().getHead_image());
                    isUpdate = true;
                } else {
                    updateUserInfo();
                }

                //更新头像
                if (mFrom != FROM_LOGIN) {
                    Glide.with(RegisterInfoActivity.this)
                            .load(DataManager.getInstance().getUserInfo().getHead_image())
                            .centerCrop()
                            .bitmapTransform(new GlideCircleTransform(RegisterInfoActivity.this))
                            .into(registerImgHead);
                }

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
                getLoadingDialog().dismiss();
            }
        });
    }
}
