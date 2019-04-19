package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2019/1/27.
 */

public class JMRoomSendParam {
    private int code;
    private long roomId;
    private String dataStr;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }
}
