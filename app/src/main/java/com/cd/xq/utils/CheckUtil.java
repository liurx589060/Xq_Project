package com.cd.xq.utils;

import com.cd.xq.module.util.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2018/11/24.
 */

public class CheckUtil {

    public static boolean checkToCompleteUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean == null) {
            return true;
        }
        if (userInfoBean.getNick_name() == null || userInfoBean.getNick_name().isEmpty()
                || userInfoBean.getHead_image() == null || userInfoBean.getHead_image().isEmpty()
                || userInfoBean.getGender() == null || userInfoBean.getGender().isEmpty()) {
            return true;
        }
        return false;
    }
}
