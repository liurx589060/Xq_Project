package com.cd.xq.module.util.manager;

import com.cd.xq.module.util.beans.jmessage.BChatRoom;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2018/5/15.
 */

public class DataManager {
    private static DataManager instance = new DataManager();
    private UserInfoBean userInfo;
    private BChatRoom chartBChatRoom;
    private String jmUserName;
    private boolean isInChatRoom;
    private UserInfoBean registerUserInfo;

    private DataManager(){}

    public static DataManager getInstance() {
        return instance;
    }

    public UserInfoBean getUserInfo() {
        if(userInfo == null) {
            userInfo = new UserInfoBean();
        }
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        if(userInfo != null && this.userInfo != null) {
            userInfo.setManualOnLine(this.userInfo.isManualOnLine());
            setJmUserName(userInfo.getUser_name());
            userInfo.setOnLine(true);
        }
        this.userInfo = userInfo;
    }

    public BChatRoom getChartBChatRoom() {
        if(chartBChatRoom == null) {
            chartBChatRoom = new BChatRoom();
        }
        return chartBChatRoom;
    }

    public void setChartBChatRoom(BChatRoom chartBChatRoom) {
        this.chartBChatRoom = chartBChatRoom;
    }

    public Member getSelfMember() {
        for(Member member: getChartBChatRoom().getMembers()) {
            if(member.getUserInfo().getUser_name().equals(getUserInfo().getUser_name())) {
                return member;
            }
        }
        return new Member();
    }

    public String getJmUserName() {
        return jmUserName;
    }

    public void setJmUserName(String jmUserName) {
        this.jmUserName = jmUserName;
    }

    public boolean isInChatRoom() {
        return isInChatRoom;
    }

    public void setInChatRoom(boolean inChatRoom) {
        isInChatRoom = inChatRoom;
    }

    public UserInfoBean getRegisterUserInfo() {
        if(registerUserInfo == null) {
            registerUserInfo = new UserInfoBean();
        }
        return registerUserInfo;
    }

    public void setRegisterUserInfo(UserInfoBean registerUserInfo) {
        this.registerUserInfo = registerUserInfo;
    }
}
