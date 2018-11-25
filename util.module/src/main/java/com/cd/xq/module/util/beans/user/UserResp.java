package com.cd.xq.module.util.beans.user;

import com.cd.xq.module.util.beans.BaseResp;

/**
 * Created by Administrator on 2018/5/14.
 */

public class UserResp extends BaseResp {
    private UserInfoBean data;

    public UserInfoBean getData() {
        return data;
    }

    public void setData(UserInfoBean data) {
        this.data = data;
    }
}
