package com.cd.xq.module.util.beans.jmessage;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2018/5/24.
 */

public class Member {
    private int index = 0;
    private int inRoom = 0;
    private int roomRoleType = Constant.ROOM_ROLETYPE_ONLOOKER;
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

    public int getRoomRoleType() {
        return roomRoleType;
    }

    public int getInRoom() {
        return inRoom;
    }

    public void setInRoom(int inRoom) {
        this.inRoom = inRoom;
    }
}
