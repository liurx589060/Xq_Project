package com.cd.xq.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/11/11.
 */

public class RegisterActivity extends BaseActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

    }

    @OnClick({R.id.register_relayout_role, R.id.register_relayout_nick, R.id.register_relayout_gender, R.id.register_relayout_age, R.id.register_relayout_tall, R.id.register_relayout_xueli, R.id.register_relayout_jiguan, R.id.register_relayout_zhiye, R.id.register_relayout_gzdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_relayout_role:
                break;
            case R.id.register_relayout_nick:
                break;
            case R.id.register_relayout_gender:
                break;
            case R.id.register_relayout_age:
                break;
            case R.id.register_relayout_tall:
                break;
            case R.id.register_relayout_xueli:
                break;
            case R.id.register_relayout_jiguan:
                break;
            case R.id.register_relayout_zhiye:
                break;
            case R.id.register_relayout_gzdd:
                break;
        }
    }

    @OnClick(R.id.register_done)
    public void onRegisterDoneClicked() {
    }

    @OnClick(R.id.register_btn_close)
    public void onRegisterBtnCloseClicked() {
        this.finish();
    }

    @OnClick(R.id.register_img_head)
    public void onViewClicked() {
    }
}
