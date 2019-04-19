package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2019/1/27.
 */

public class JMSingleSendParam {
    private int code;
    private String targetUser = "";
    private String dataStr;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }
}
