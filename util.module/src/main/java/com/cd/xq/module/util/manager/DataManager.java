package com.cd.xq.module.util.manager;

import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2018/5/15.
 */

public class DataManager {
    private static DataManager instance = new DataManager();
    private UserInfoBean userInfo;
    private Data chartData;
    private String jmUserName;

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

    public Data getChartData() {
        if(chartData == null) {
            chartData = new Data();
        }
        return chartData;
    }

    public void setChartData(Data chartData) {
        this.chartData = chartData;
    }

    public Member getSelfMember() {
        for(Member member:getChartData().getMembers()) {
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
}
