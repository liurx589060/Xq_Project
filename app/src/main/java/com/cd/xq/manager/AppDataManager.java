package com.cd.xq.manager;

import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/27.
 */

public class AppDataManager {
    private static AppDataManager instance = new AppDataManager();
    private List<UserInfoBean> friendList;

    public static AppDataManager getInstance() {
        return instance;
    }

    private AppDataManager() {}

    public List<UserInfoBean> getFriendList() {
        if(friendList == null) {
            friendList = new ArrayList<>();
        }
        return friendList;
    }
}
