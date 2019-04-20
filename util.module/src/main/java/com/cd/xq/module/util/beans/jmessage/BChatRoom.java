/**
  * Copyright 2018 bejson.com 
  */
package com.cd.xq.module.util.beans.jmessage;

import java.util.ArrayList;
import java.util.List;

public class BChatRoom {
    private long room_id;
    private int limit_level;
    private int limit_lady;
    private int limit_man;
    private int limit_angel;
    private int count_lady;
    private int count_man;
    private int count_angel;
    private String push_address = "";
    private String play_address = "";
    private List<Member> members;
    private List<Member> onLookers;
    private String creater = "";
    private String appoint_time = "";
    private String title = "";
    private String describe = "";
    private int work = 2;  //默认结束
    private int room_role_type;

    public void setMembers(List<Member> members) {
         this.members = members;
     }
     public List<Member> getMembers() {
        if(members == null) {
            members = new ArrayList<>();
        }
        return members;
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

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getAppoint_time() {
        return appoint_time;
    }

    public void setAppoint_time(String appoint_time) {
        this.appoint_time = appoint_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public int getLimit_level() {
        return limit_level;
    }

    public void setLimit_level(int limit_level) {
        this.limit_level = limit_level;
    }

    public int getLimit_lady() {
        return limit_lady;
    }

    public void setLimit_lady(int limit_lady) {
        this.limit_lady = limit_lady;
    }

    public int getLimit_man() {
        return limit_man;
    }

    public void setLimit_man(int limit_man) {
        this.limit_man = limit_man;
    }

    public int getLimit_angel() {
        return limit_angel;
    }

    public void setLimit_angel(int limit_angel) {
        this.limit_angel = limit_angel;
    }

    public String getPush_address() {
        return push_address;
    }

    public void setPush_address(String push_address) {
        this.push_address = push_address;
    }

    public String getPlay_address() {
        return play_address;
    }

    public void setPlay_address(String play_address) {
        this.play_address = play_address;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public int getCount_lady() {
        return count_lady;
    }

    public void setCount_lady(int count_lady) {
        this.count_lady = count_lady;
    }

    public int getCount_man() {
        return count_man;
    }

    public void setCount_man(int count_man) {
        this.count_man = count_man;
    }

    public int getCount_angel() {
        return count_angel;
    }

    public void setCount_angel(int count_angel) {
        this.count_angel = count_angel;
    }

    public int getRoom_role_type() {
        return room_role_type;
    }

    public void setRoom_role_type(int room_role_type) {
        this.room_role_type = room_role_type;
    }
}