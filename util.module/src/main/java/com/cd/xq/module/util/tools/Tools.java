package com.cd.xq.module.util.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.user.BBlackUser;
import com.cd.xq.module.util.interfaces.ICheckBlackListener;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/5/15.
 */

public class Tools {
    public static void toast(Context context,String text,boolean isLong) {
        if(context == null) return;
        if(context instanceof Activity) {
            if(((Activity)context).isFinishing()) {
                return;
            }
        }
        Toast.makeText(context,text,isLong?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        return format.format(new Date());
    }

    public static int parseInt(String value) {
        int i = -1;
        try {
            i = Integer.parseInt(value);
        }catch (Exception e) {
            Log.e("parseInt--" + value);
        }
        return i;
    }

    /**
     * MD5 加密
     * @param string
     * @return
     */
    public static String MD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void checkUserOrBlack(final Activity activity, String userName,final ICheckBlackListener listener) {
        if(activity == null || userName == null) return;
        RequestApi api = NetWorkMg.newRetrofit().create(RequestApi.class);
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("status",1);
        api.getBlackUserByName(params)
                .subscribeOn(Schedulers.io())
                .compose(((RxAppCompatActivity)activity).<NetResult<BBlackUser>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BBlackUser>>() {
                    @Override
                    public void accept(NetResult<BBlackUser> bBlackUserNetResult) throws Exception {
                        if(bBlackUserNetResult.getStatus() == XqErrorCode.SUCCESS
                                && bBlackUserNetResult.getData() != null) {
                            String report_msg = bBlackUserNetResult.getData().getReport_msg();
                            String startData = bBlackUserNetResult.getData().getStart_time();
                            String endData = bBlackUserNetResult.getData().getEnd_time();
                            String content = "您因违法以下条款：\n" + report_msg + "\n" + "将从" + startData + "到" + endData + "被禁止登录使用";
                            Dialog dialog = DialogFactory.createBlackDialog(activity, content,listener);
                            dialog.show();

                            if(listener != null) {
                                listener.onResult(true);
                            }
                            return;
                        }

                        if(listener != null) {
                            listener.onResult(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("checkUserOrBlack--" + throwable.toString());
                        if(listener != null) {
                            listener.onResult(false);
                        }
                    }
                });
    }

    /**
     * 判断当前应用是否是debug状态
     * @param context
     * @return
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("getVersionCode--" + e.toString());
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("getVerName--" + e.toString());
        }
        return verName;
    }
}
