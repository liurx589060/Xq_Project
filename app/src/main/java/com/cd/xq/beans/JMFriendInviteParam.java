package com.cd.xq.beans;

import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2019/2/17.
 */

public class JMFriendInviteParam {
    public static final int ACTION_ACCEPT = 10;
    public static final int ACTION_REFUSE = 11;
    public static final int ACTION_CANCEL = 12;
    public static final int ACTION_CREATE = 13;

    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECEIVE = 2;

    private int action;
    private int type;
    private UserInfoBean fromUser;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserInfoBean getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserInfoBean fromUser) {
        this.fromUser = fromUser;
    }
}
