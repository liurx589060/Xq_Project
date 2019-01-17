package com.cd.xq.module.util.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cd.xq.module.util.interfaces.ICheckBlackListener;
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

    public static Dialog createBlackDialog(final Activity activity, String text, final ICheckBlackListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("您被禁止登陆")
                .setMessage(text)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null) {
                            listener.onDialogCheckClick(activity);
                        }
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }).create();

        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
