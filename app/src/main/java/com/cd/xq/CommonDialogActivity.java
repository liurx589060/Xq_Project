package com.cd.xq;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.Log;

/**
 * Created by Administrator on 2019/4/21.
 */

public class CommonDialogActivity extends BaseActivity {
    public final static int TYPE_JM_TIP_DIALOG = 1000;  //预约倒计时提示dialog
    public final static int TYPE_JM_APPOINTTIME_CLOSE_DIALOG = 1001;  //预约到了dialog
    private int type;
    private String data;

    public interface ICommonTipStartListener {
        void onNegative();
        void onPositive();
    }
    public static ICommonTipStartListener mICommonTipStartListener;
    public static void setICommonTipStartListener(ICommonTipStartListener listener) {
        mICommonTipStartListener = listener;
    }

    ////////////////////////////
    public interface ICommonAppointTimeCloseListener {
        void onNegative();
        void onPositive();
    }
    public static ICommonAppointTimeCloseListener mICommonAppointTimeCloseListener;
    public static void setICommonAppointTimeCloseListener(ICommonAppointTimeCloseListener listener) {
        mICommonAppointTimeCloseListener = listener;
    }

    public static void showDialog(Context context,int type) {
        Intent intent = new Intent(context,CommonDialogActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    public static void showDialog(Context context,int type,String data) {
        Intent intent = new Intent(context,CommonDialogActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("data",data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            type = getIntent().getIntExtra("type",0);
            data = getIntent().getStringExtra("data");

            if(type == TYPE_JM_TIP_DIALOG) {
                showTipStartDialog();
            }else if(type == TYPE_JM_APPOINTTIME_CLOSE_DIALOG) {
                showAppointTimeCloseDialog();
            }
        }catch (Exception e) {
            Log.e("CommonDialogActivity--" + e.toString());
        }
    }

    /**
     * 提示进入房间
     */
    private void showTipStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("房间即将开始，是否进入房间？")
                .setPositiveButton("进入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mICommonTipStartListener != null) {
                            mICommonTipStartListener.onPositive();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mICommonTipStartListener != null) {
                            mICommonTipStartListener.onNegative();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    /**
     * 预约时间到
     */
    private void showAppointTimeCloseDialog() {
        if(data == null) return;
        final boolean isCreater = data.equals(DataManager.getInstance().getUserInfo().getUser_name());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String text = "房间启动了，请进入房间";
        builder.setTitle("提示")
                .setMessage(text)
                .setPositiveButton("进入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mICommonAppointTimeCloseListener != null) {
                            mICommonAppointTimeCloseListener.onPositive();
                        }
                    }
                })
                .setNegativeButton(isCreater?"删除":"退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mICommonAppointTimeCloseListener != null) {
                            mICommonAppointTimeCloseListener.onNegative();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }
}
