package com.cd.xq.beans;

/**
 * Created by Administrator on 2018/12/9.
 */

public class BGetArrays {
    private long roomId;
    private int limitLevel;
    private int limitLadyCount;
    private int limitMan;
    private int limitAngel;
    private String describe = "";
    private String creater = "";

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(int limitLevel) {
        this.limitLevel = limitLevel;
    }

    public int getLimitLadyCount() {
        return limitLadyCount;
    }

    public void setLimitLadyCount(int limitLadyCount) {
        this.limitLadyCount = limitLadyCount;
    }

    public int getLimitMan() {
        return limitMan;
    }

    public void setLimitMan(int limitMan) {
        this.limitMan = limitMan;
    }

    public int getLimitAngel() {
        return limitAngel;
    }

    public void setLimitAngel(int limitAngel) {
        this.limitAngel = limitAngel;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
}
