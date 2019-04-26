package com.cd.xq.module.util.network;

import android.content.Context;

import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;

public class NetParse {

    public static boolean parseNetResult(Context context,NetResult result) {
       return parseNetResult(context,result,true);
    }

    public static boolean parseNetResult(Context context,NetResult result,boolean showToast) {
        if(result == null) return false;
        boolean isSuccess = true;
        String msg = "成功";
        if(result.getStatus() != XqErrorCode.SUCCESS) {
            isSuccess = false;
            if (result.getStatus() == XqErrorCode.ERROR_NO_DATA) {
                msg = "暂无数据";
            } else if (result.getStatus() == XqErrorCode.ERROR_NO_MATCH) {
                msg = "未匹配上";
            } else if (result.getStatus() == XqErrorCode.ERROR_EXIT_CHARTROOM) {
                msg = "退出房间失败";
            } else if (result.getStatus() == XqErrorCode.ERROR_LACK_PARAMS) {
                msg = "缺少参数";
            } else if (result.getStatus() == XqErrorCode.ERROR_DELETE_CHARTEOOM) {
                msg = "删除房间失败";
            } else if (result.getStatus() == XqErrorCode.ERROR_CREATE_CHARTEOOM) {
                msg = "创建房间失败";
            } else if (result.getStatus() == XqErrorCode.ERROR_JOIN_CHARTEOOM) {
                msg = "加入房间失败";
            } else if (result.getStatus() == XqErrorCode.ERROR_NOT_FIND_CHATROOM) {
                msg = "找不到房间";
            } else if (result.getStatus() == XqErrorCode.ERROR_ALREADY_APPOINT_CHATROOM) {
                msg = "已经有预约的房间";
            } else if (result.getStatus() == XqErrorCode.ERROR_FULL_CHATROOM) {
                msg = "房间已满员";
            } else if (result.getStatus() == XqErrorCode.ERROR_ALREADY_START_CHATROOM) {
                msg = "房间已开始";
            } else if (result.getStatus() == XqErrorCode.ERROR_ROLETYPE_NOT_MATCH) {
                msg = "角色身份不对";
            } else if (result.getStatus() == XqErrorCode.ERROR_ALREADY_JOIN_CHATROOM) {
                msg = "已经加入了房间，不能重复加入";
            } else if (result.getStatus() == XqErrorCode.ERROR_USER_REGIST) {
                msg = "注册失败";
            } else if (result.getStatus() == XqErrorCode.ERROR_USER_REGIST_EXIST) {
                msg = "已经存在账户";
            } else if (result.getStatus() == XqErrorCode.ERROR_USER_NOT_EXIST) {
                msg = "账号不存在";
            } else if (result.getStatus() == XqErrorCode.ERROR_USER_UPLOAD) {
                msg = "上传文件出错";
            } else if (result.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_WRONG) {
                msg = "密码错误";
            } else if (result.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_CHANGE) {
                msg = "修改密码出错";
            } else if (result.getStatus() == XqErrorCode.ERROR_PARAMS_SAME) {
                msg = "参数相同";
            } else if (result.getStatus() == XqErrorCode.ERROR_LACK_STOCK) {
                msg = "库存不够";
            }else {
                msg = result.getMsg();
            }

            Log.e(msg);
            if (showToast) {
                Tools.toast(context, msg, false);
            }
        }
        return isSuccess;
    }

    public static void parseError(Context context,Throwable throwable) {
        parseError(context,throwable,true);
    }

    public static void parseError(Context context,Throwable throwable,boolean showToast) {
        Log.e(throwable.toString());
        if(showToast) {
            Tools.toast(context, throwable.toString(), false);
        }
    }
}
