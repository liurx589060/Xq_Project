package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2019/5/3.
 */

public class BAppSettings {
    private int is_idcard_must;  //是否必须实名制身份证（失败则退出程序）

    public int getIs_idcard_must() {
        return is_idcard_must;
    }

    public void setIs_idcard_must(int is_idcard_must) {
        this.is_idcard_must = is_idcard_must;
    }
}
