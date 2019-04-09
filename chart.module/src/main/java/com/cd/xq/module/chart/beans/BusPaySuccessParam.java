package com.cd.xq.module.chart.beans;

import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2019/1/27.
 */

public class BusPaySuccessParam {
    private long balance;
    private int coin;
    private String modify_time = "";
    private UserInfoBean userInfo;


    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }
}
