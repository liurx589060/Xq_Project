package com.cd.xq;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cd.xq.login.BlackCheckListener;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.DialogFactory;
import com.cd.xq.module.util.tools.Tools;

/**
 * Created by Administrator on 2019/2/15.
 */

public class DialogContainerActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.checkUserOrBlack(this, DataManager.getInstance().getUserInfo()
                .getUser_name(), new BlackCheckListener() {
            @Override
            public void onResult(boolean isBlack) {
            }
        });
    }
}
