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
import com.cd.xq.module.util.AppConfig;
import com.cd.xq.R;
import com.cd.xq.login.RegisterActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.SharedPreferenceUtil;
import com.cd.xq.my.MyBalanceActivity;
import com.cd.xq.my.MyCertificateActivity;
import com.cd.xq.my.MyFootprinterActivity;
import com.cd.xq.my.MyNotifyActivity;
import com.cd.xq.my.MyProfitActivity;
import com.cd.xq.my.MySettingsActivity;

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
    Unbinder unbinder;
    @BindView(R.id.my_img_notify_dot)
    ImageView myImgNotifyDot;
    @BindView(R.id.my_title_relayout)
    RelativeLayout myTitleRelayout;
    @BindView(R.id.checkbox_remote)
    CheckBox checkboxRemote;
    @BindView(R.id.my_top_head_layout)
    RelativeLayout myTopHeadLayout;
    @BindView(R.id.ip_edit)
    EditText ipEdit;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.my_img_gender)
    ImageView myImgGender;
    @BindView(R.id.my_text_id)
    TextView myTextId;
    @BindView(R.id.my_text_yue)
    TextView myTextYue;
    @BindView(R.id.my_relayout_yue)
    RelativeLayout myRelayoutYue;
    @BindView(R.id.my_text_liquan)
    TextView myTextLiquan;
    @BindView(R.id.my_relayout_liquan)
    RelativeLayout myRelayoutLiquan;
    @BindView(R.id.my_text_shouyi)
    TextView myTextShouyi;
    @BindView(R.id.my_relayout_shouyi)
    RelativeLayout myRelayoutShouyi;
    @BindView(R.id.my_text_role)
    TextView myTextRole;
    @BindView(R.id.my_text_nick)
    TextView myTextNick;
    @BindView(R.id.my_relayout_footprint)
    RelativeLayout myRelayoutFootprint;
    @BindView(R.id.my_text_settings)
    TextView myTextSettings;
    @BindView(R.id.my_relayout_settings)
    RelativeLayout myRelayoutSettings;

    private String SUFFIX = "  >";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
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

    public void setData() {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }

        Glide.with(this)
                .load(userInfo.getHead_image())
                .placeholder(R.drawable.chart_room_default_head)
                .dontAnimate()
                .centerCrop()
                .into(myImgHead);

        myImgGender.setImageResource(userInfo.getGender().equals(Constant.GENDER_LADY) ?
                R.drawable.my_gender_lady : R.drawable.my_gender_man);
        if (userInfo.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            myTextRole.setText("爱心大使");
        } else {
            if(userInfo.getMarrige() == Constant.ROLE_MARRIED) {
                //非未婚状态
                myTextRole.setText("观众");
            }else {
                myTextRole.setText("嘉宾");
            }
        }
        myTextNick.setText(userInfo.getNick_name());
        myTextId.setText("ID:" + userInfo.getUser_id());
    }

    private void init() {
        setData();

//        ipEdit.setVisibility(View.GONE);
//        btnSave.setVisibility(View.GONE);
//        checkboxRemote.setVisibility(View.GONE);

        myImgNotifyDot.setVisibility(View.GONE);
        myTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyNotifyActivity.class);
                getActivity().startActivity(intent);
            }
        });
        myTopHeadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRegisterActivity();
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setData();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            setData();
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            setData();
        }
    }

    private void initIp() {
        final EditText editText = mRootView.findViewById(R.id.ip_edit);
        CheckBox checkBox = mRootView.findViewById(R.id.checkbox_remote);
        Button button = mRootView.findViewById(R.id.btn_save);
        editText.setText(SharedPreferenceUtil.getSpIpAddress(getActivity()));
        checkBox.setChecked(SharedPreferenceUtil.getIsRemote(getActivity()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceUtil.setSpIpAddress(getActivity(),editText.getText().toString());
                NetWorkMg.IP_ADDRESS = editText.getText().toString();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferenceUtil.setSpRemoteIpFlag(getActivity(),isChecked);
            }
        });
    }

    @Override
    public void onLogin() {
        super.onLogin();
        init();
    }

    @OnClick({R.id.my_relayout_yue, R.id.my_relayout_liquan, R.id.my_relayout_shouyi, R.id
            .my_text_role,R.id.my_relayout_footprint, R.id.my_relayout_settings})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.my_relayout_yue:
                intent = new Intent(getActivity(), MyBalanceActivity.class);
                break;
            case R.id.my_relayout_liquan:
                intent = new Intent(getActivity(), MyCertificateActivity.class);
                break;
            case R.id.my_relayout_shouyi:
                intent = new Intent(getActivity(), MyProfitActivity.class);
                break;
            case R.id.my_text_role:
                break;
            case R.id.my_relayout_footprint:
                intent = new Intent(getActivity(), MyFootprinterActivity.class);
                break;
            case R.id.my_relayout_settings:
                intent = new Intent(getActivity(), MySettingsActivity.class);
                break;
        }
        if (intent != null) {
            getActivity().startActivity(intent);
        }
    }
}
