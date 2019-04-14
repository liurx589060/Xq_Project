package com.cd.xq.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.cd.xq.beans.BCheckUpdate;
import com.cd.xq.beans.JMOnlineParam;
import com.cd.xq.manager.AppDataManager;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.JMSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.network.XqRequestApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.cd.xq.module.util.network.NetWorkMg.IP_ADDRESS;

/**
 * Created by Administrator on 2019/2/13.
 */

public class AppTools {

    /**
     * 告知好友是否上线或离线
     * @param isOnLine
     */
    public static void notifyFriendOnLine(boolean isOnLine) {
        for(int i = 0; i < AppDataManager.getInstance().getFriendList().size() ; i++) {
            UserInfoBean userInfoBean = AppDataManager.getInstance().getFriendList().get(i);
            notifyFriendOnLineByUser(userInfoBean,isOnLine);
        }
    }

    public static void notifyFriendOnLineByUser(UserInfoBean userInfoBean,boolean isOnLine) {
        JMSendBean sendBean = new JMSendBean();
        sendBean.setCode(JMSendBean.JM_SEND_USER_CHECK_ONLINE);
        sendBean.setTargetUserName(userInfoBean.getUser_name());
        sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
        JMOnlineParam param = new JMOnlineParam();
        param.setType(JMOnlineParam.TYPE_SEND);
        param.setOnLine(isOnLine);
        sendBean.setJsonData(new Gson().toJson(param));
        JMsgSender.sendTextMessage(sendBean);
    }
}
