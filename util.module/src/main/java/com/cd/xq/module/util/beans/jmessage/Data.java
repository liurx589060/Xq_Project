/**
  * Copyright 2018 bejson.com 
  */
package com.cd.xq.module.util.beans.jmessage;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private long roomId;
    private int limitLevel;
    private int limitLady;
    private int limitMan;
    private int limitAngel;
    private String pushAddress = "";
    private String playAddress = "";
    private List<Member> members;
    private List<Member> onLookers;
    public void setRoomId(long roomId) {
         this.roomId = roomId;
     }
     public long getRoomId() {
         return roomId;
     }

    public void setMembers(List<Member> members) {
         this.members = members;
     }
     public List<Member> getMembers() {
        if(members == null) {
            members = new ArrayList<>();
        }
        return members;
     }

    public int getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(int limitLevel) {
        this.limitLevel = limitLevel;
    }

    public int getLimitLady() {
        return limitLady;
    }

    public void setLimitLady(int limitLady) {
        this.limitLady = limitLady;
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

    public String getPushAddress() {
        return pushAddress;
    }

    public void setPushAddress(String pushAddress) {
        this.pushAddress = pushAddress;
    }

    public String getPlayAddress() {
        return playAddress;
    }

    public void setPlayAddress(String playAddress) {
        this.playAddress = playAddress;
    }

    public List<Member> getOnLookers() {
        if(onLookers == null) {
            onLookers = new ArrayList<>();
        }
        return onLookers;
    }

    public void setOnLookers(List<Member> onLookers) {
        this.onLookers = onLookers;
    }
}