package com.cd.xq.module.util.beans.jmessage;

/**
 * Created by Administrator on 2019/1/23.
 */

public class JMSendBean<T> {
    public static final int JM_SEND_FRIEND_ONLINE = 0x1000;  //检查好友在线

    private int code;
    private String targetUserName;
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
}
