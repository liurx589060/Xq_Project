package com.cd.xq.frame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.login.LoginActivity;
import com.cd.xq.login.RegisterActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/11/11.
 */

public class MyFragment extends BaseFragment {
    @BindView(R.id.my_img_head)
    ImageView myImgHead;
    @BindView(R.id.my_text_edit)
    TextView myTextEdit;
    @BindView(R.id.my_text_nick)
    TextView myTextNick;
    @BindView(R.id.my_img_gender)
    ImageView myImgGender;
    @BindView(R.id.my_text_age)
    TextView myTextAge;
    @BindView(R.id.my_text_tall)
    TextView myTextTall;
    @BindView(R.id.my_text_xueli)
    TextView myTextXueli;
    @BindView(R.id.register_text_jiguan)
    TextView registerTextJiguan;
    @BindView(R.id.register_relayout_role)
    RelativeLayout registerRelayoutRole;
    @BindView(R.id.register_text_zhiye)
    TextView registerTextZhiye;
    @BindView(R.id.register_relayout_nick)
    RelativeLayout registerRelayoutNick;
    @BindView(R.id.register_text_gzdd)
    TextView registerTextGzdd;
    @BindView(R.id.register_relayout_gzdd)
    RelativeLayout registerRelayoutGzdd;
    Unbinder unbinder;
    @BindView(R.id.my_btn_switch)
    Button myBtnSwitch;

    private String SUFFIX = "  >";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_my, null);
        unbinder = ButterKnife.bind(this, mRootView);

        init();
        return mRootView;
    }

    private void jumpToRegisterActivity() {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("from", RegisterActivity.FROM_MY);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, 1);
    }

    private void jumpToLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("from", LoginActivity.FROM_MY);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, 200);
    }

    public void setData() {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        if(userInfo == null) {
            return;
        }

        Glide.with(this)
                .load(userInfo.getHead_image())
                .centerCrop()
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .into(myImgHead);
        myImgGender.setImageResource(userInfo.getGender().equals(Constant.GENDER_LADY) ? R.drawable.my_gender_lady : R.drawable.my_gender_man);
        myTextAge.setText(String.valueOf(userInfo.getAge()));
        myTextTall.setText(String.valueOf(userInfo.getTall()) + "cm");
        myTextNick.setText(userInfo.getNick_name());
        myTextXueli.setText(userInfo.getScholling());
        registerTextJiguan.setText(userInfo.getNative_place() + SUFFIX);
        registerTextGzdd.setText(userInfo.getJob_address() + SUFFIX);
        registerTextZhiye.setText(userInfo.getProfessional() + SUFFIX);
    }

    private void init() {
        setData();

        myTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRegisterActivity();
            }
        });

        myBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToLoginActivity();
            }
        });

        initIp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            setData();
        }else if(requestCode == 2 && resultCode == RESULT_OK) {
            setData();
        }else if(requestCode == 200 && resultCode == RESULT_OK) {
            setData();
        }
    }

    private void initIp() {
        final EditText editText = mRootView.findViewById(R.id.ip_edit);
        CheckBox checkBox = mRootView.findViewById(R.id.checkbox_remote);
        Button button = mRootView.findViewById(R.id.btn_save);
        editText.setText(getSpIpAddress());
        checkBox.setChecked(getIsRemote());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpIpAddress(editText.getText().toString());
                NetWorkMg.IP_ADDRESS = editText.getText().toString();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSpRemoteIpFlag(isChecked);
            }
        });
    }

    private void setSpIpAddress(String ipAddress) {
        SharedPreferences sp = getActivity().getSharedPreferences(Constant.SP_NAME, Activity.MODE_PRIVATE);
        sp.edit().putString("ipAddress",ipAddress).commit();
    }

    private void setSpRemoteIpFlag(boolean isRemote) {
        SharedPreferences sp = getActivity().getSharedPreferences(Constant.SP_NAME, Activity.MODE_PRIVATE);
        sp.edit().putBoolean("isRemote",isRemote).commit();
    }

    private String getSpIpAddress() {
        SharedPreferences sp = getActivity().getSharedPreferences(Constant.SP_NAME, Activity.MODE_PRIVATE);
        return sp.getString("ipAddress","192.168.1.101");
    }

    private boolean getIsRemote() {
        SharedPreferences sp = getActivity().getSharedPreferences(Constant.SP_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("isRemote",false);
    }

    @Override
    public void onLogin() {
        super.onLogin();
        init();
    }
}
