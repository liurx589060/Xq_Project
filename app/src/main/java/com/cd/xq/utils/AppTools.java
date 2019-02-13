package com.cd.xq.utils;

import com.cd.xq.beans.JMOnlineParam;
import com.cd.xq.manager.AppDataManager;
import com.cd.xq.module.util.beans.jmessage.JMSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;

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
        sendBean.setData(param);
        JMsgSender.sendTextMessage(sendBean);
    }
}
