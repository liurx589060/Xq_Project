package com.cd.xq.module.util.beans.jmessage;


import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2018/5/24.
 */

public class Member {
    private int index = 0;
    private UserInfoBean userInfo;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public UserInfoBean getUserInfo() {
        if(userInfo == null) {
            userInfo = new UserInfoBean();
        }
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }
}
