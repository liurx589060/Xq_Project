package com.cd.xq.beans;

import com.cd.xq.module.chart.beans.BGetGiftItem;

/**
 * Created by Administrator on 2019/4/10.
 */

public class BCheckRoomExpiry {
    private Expiry expiry;
    private BGetGiftItem targetGift;
    private BGetGiftItem gift;
    private int hasCard;

    public Expiry getExpiry() {
        return expiry;
    }

    public void setExpiry(Expiry expiry) {
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


    public class Expiry extends BGetGiftItem {
        private int gift_id;
        private int expiry_num;
        private String start_time = "";
        private String end_time = "";

        public int getGift_id() {
            return gift_id;
        }

        public void setGift_id(int gift_id) {
            this.gift_id = gift_id;
        }

        public int getExpiry_num() {
            return expiry_num;
        }

        public void setExpiry_num(int expiry_num) {
            this.expiry_num = expiry_num;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }
    }
}
