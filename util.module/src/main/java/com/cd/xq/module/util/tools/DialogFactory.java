package com.cd.xq.module.util.tools;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.cd.xq.module.util.views.PoterDuffLoadingView;
import com.cd.xq_util.com.R;

/**
 * Created by Administrator on 2018/11/18.
 */

public class DialogFactory {

    public static Dialog createLoadingDialog(Activity activity) {
        return createLoadingDialog(activity, Color.parseColor("#00b7ee"));
    }

    public static Dialog createLoadingDialog(Activity activity,int color) {
        Dialog dialog = new Dialog(activity, R.style.loading_dialog);
        PoterDuffLoadingView view = new PoterDuffLoadingView(activity);
        int width = (int) activity.getResources().getDimension(R.dimen.dp_67);
        int height = width;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);
        view.setLayoutParams(params);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        view.setColor(color);
        return dialog;
    }
}
