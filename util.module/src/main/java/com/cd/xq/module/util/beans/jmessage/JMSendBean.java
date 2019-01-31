package com.cd.xq.module.util.beans.jmessage;

/**
 * Created by Administrator on 2019/1/23.
 */

public class JMSendBean<T> {
    public static final int JM_SEND_USER_CHECK_ONLINE = 0x1000;  //检查用户是否在线

    private int code;
    private String targetUserName;
    private String fromUserName;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
}
