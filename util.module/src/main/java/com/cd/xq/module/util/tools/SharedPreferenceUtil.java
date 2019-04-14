package com.cd.xq.module.util.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.cd.xq.module.util.AppConfig;
import com.cd.xq.module.util.Constant;

/**
 * Created by Administrator on 2019/4/14.
 */

public class SharedPreferenceUtil {
    public static void setSpIpAddress(Context context,String ipAddress) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Activity
                .MODE_PRIVATE);
        sp.edit().putString("ipAddress", ipAddress).commit();
    }

    public static void setSpRemoteIpFlag(Context context,boolean isRemote) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Activity
                .MODE_PRIVATE);
        sp.edit().putBoolean("isRemote", isRemote).commit();
    }

    public static String getSpIpAddress(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Activity
                .MODE_PRIVATE);
        return sp.getString("ipAddress", AppConfig.isRemote?Constant.CONSTANT_REMOTE_IP:Constant.CONSTANT_LOCOL_IP);
    }

    public static boolean getIsRemote(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Activity
                .MODE_PRIVATE);
        return sp.getBoolean("isRemote", AppConfig.isRemote);
    }
}
