package com.cd.xq.beans;

import com.cd.xq.module.chart.beans.BGetGiftItem;

/**
 * Created by Administrator on 2019/3/23.
 */

public class BGetConsumeHistory extends BGetGiftItem {
    private String create_time = "";
    private String to_user = "";
    private String nick_name = "";
    private String head_image = "";

    public String getTo_user() {
        return to_user;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
