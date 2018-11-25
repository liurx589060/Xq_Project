package com.cd.xq.module.util.tools;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;

import com.cd.xq.module.util.views.PoterDuffLoadingView;
import com.cd.xq_util.com.R;

/**
 * Created by Administrator on 2018/11/18.
 */

public class DialogFactory {

    public static Dialog createLoadingDialog(Activity activity) {
        Dialog dialog = new Dialog(activity, R.style.loading_dialog);
        View view = new PoterDuffLoadingView(activity);
        int width = (int) activity.getResources().getDimension(R.dimen.dp_67);
        int height = width;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);
        view.setLayoutParams(params);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
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
