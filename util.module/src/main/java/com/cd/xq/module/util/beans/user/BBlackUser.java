package com.cd.xq.module.util.beans.user;

/**
 * Created by Administrator on 2019/1/16.
 */

public class BBlackUser {
    private String user_name = "";
    private String report_msg = "";
    private long start_time;
    private long end_time;
    private long room_id;
    private int status;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getReport_msg() {
        return report_msg;
    }

    public void setReport_msg(String report_msg) {
        this.report_msg = report_msg;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
