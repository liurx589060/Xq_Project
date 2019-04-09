package com.cd.xq.module.chart.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.util.interfaces.IDialogListener;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.DialogFactory;

/**
 * Created by Administrator on 2019/3/31.
 */

public class ChatTools {
    /**
     * 检查余额,跳转到充值Activity
     * @param activity
     */
    public static boolean checkBalance(final Activity activity, int coin) {
        if(DataManager.getInstance().getUserInfo().getBalance() < coin) {
            IDialogListener listener = new IDialogListener() {
                @Override
                public void onConfirm() {
                    Intent intent = new Intent("com.cd.xq.intent.action.MyBalanceActivity");
                    activity.startActivity(intent);
                }

                @Override
                public void onCancel() {

                }
            };
            ChatTools.checkBalance(activity,coin,listener);
            return false;
        }
        return true;
    }

    /**
     * 检查余额,跳转到房间充值页面
     * @param activity
     */
    public static boolean checkBalance(final Activity activity, int coin,IDialogListener listener) {
        long delta = DataManager.getInstance().getUserInfo().getBalance() - coin;
        if(delta < 0) {
            String text = String.format("余额不足差%d钻石，请充值",-delta);
            Dialog dialog = DialogFactory.createNotEnoughBalance(activity,text,listener);
            dialog.show();
            return false;
        }
        return true;
    }

    /**
     * 消费确认框
     * @param activity
     */
    public static void consumeConfirm(final Activity activity, BGetGiftItem item, IDialogListener listener) {
        String text = String.format("你将花费%d钻石购买%s",item.getCoin(),item.getName());
        Dialog dialog = DialogFactory.createConsumeDialog(activity,text,listener);
        dialog.show();
    }
}
