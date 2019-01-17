package com.cd.xq.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cd.xq.module.util.interfaces.ICheckBlackListener;

/**
 * Created by Administrator on 2019/1/17.
 */

public abstract class BlackCheckListener implements ICheckBlackListener {
    @Override
    public void onDialogCheckClick(Activity activity) {
        if(!(activity instanceof LoginActivity)) {
            Intent intent = new Intent(activity, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("from", LoginActivity.FROM_WELCOME);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }
}
