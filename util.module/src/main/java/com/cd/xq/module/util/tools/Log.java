package com.cd.xq.module.util.tools;

import com.cd.xq.module.util.Constant;

/**
 * Created by Administrator on 2018/11/18.
 */

public class Log {
    private static boolean isDebug = true;

    public static void e(String tag,String message) {
        android.util.Log.e(tag,message);
    }

    public static void e(String message) {
        android.util.Log.e(Constant.TAG,message);
    }

    public static void d(String tag,String message) {
        if(isDebug) {
            android.util.Log.d(tag,message);
        }
    }

    public static void d(String message) {
        if(isDebug) {
            android.util.Log.d(Constant.TAG,message);
        }
    }

    public static void i(String tag,String message) {
        if(isDebug) {
            android.util.Log.i(tag,message);
        }
    }

    public static void i(String message) {
        if(isDebug) {
            android.util.Log.i(Constant.TAG,message);
        }
    }
}
