package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2018/3/10.
 */

public class JMNormalSendBean {
    public static final int NORMAL_EXIT = 0x040;//创建者解散聊天室

    private int code;
    private String msg = "";
    private String targetUserName = "";
    private String time = "";
    private long roomId;
    private String extra;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
