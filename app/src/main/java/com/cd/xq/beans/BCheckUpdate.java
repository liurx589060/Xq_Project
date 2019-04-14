package com.cd.xq.beans;

/**
 * Created by Administrator on 2019/4/12.
 */

public class BCheckUpdate {
    private int version_code;
    private String version_name = "";
    private String message = "";
    private String down_url = "";
    private String time = "";
    private int is_force;

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDown_url() {
        return down_url;
    }

    public void setDown_url(String down_url) {
        this.down_url = down_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIs_force() {
        return is_force;
    }

    public void setIs_force(int is_force) {
        this.is_force = is_force;
    }
}
