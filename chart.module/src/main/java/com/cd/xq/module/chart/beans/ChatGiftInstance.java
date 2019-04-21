package com.cd.xq.module.chart.beans;

/**
 * Created by Administrator on 2019/4/21.
 */

public class ChatGiftInstance {
    private String targetUser = "";
    private BGetGiftItem giftItem;

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public BGetGiftItem getGiftItem() {
        return giftItem;
    }

    public void setGiftItem(BGetGiftItem giftItem) {
        this.giftItem = giftItem;
    }
}
