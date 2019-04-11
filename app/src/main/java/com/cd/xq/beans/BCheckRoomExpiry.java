package com.cd.xq.beans;

import com.cd.xq.module.chart.beans.BGetGiftItem;

/**
 * Created by Administrator on 2019/4/10.
 */

public class BCheckRoomExpiry {
    private BGetGiftItem expiry;
    private BGetGiftItem targetGift;
    private BGetGiftItem gift;
    private int hasCard;

    public BGetGiftItem getExpiry() {
        return expiry;
    }

    public void setExpiry(BGetGiftItem expiry) {
        this.expiry = expiry;
    }

    public BGetGiftItem getGift() {
        return gift;
    }

    public void setGift(BGetGiftItem gift) {
        this.gift = gift;
    }

    public int getHasCard() {
        return hasCard;
    }

    public void setHasCard(int hasCard) {
        this.hasCard = hasCard;
    }

    public BGetGiftItem getTargetGift() {
        return targetGift;
    }

    public void setTargetGift(BGetGiftItem targetGift) {
        this.targetGift = targetGift;
    }
}
