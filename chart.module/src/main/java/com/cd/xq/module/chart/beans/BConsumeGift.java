package com.cd.xq.module.chart.beans;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/31.
 */

public class BConsumeGift {
    private long balance;
    private List<BGetGiftItem> gift_list;

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<BGetGiftItem> getGift_list() {
        return gift_list;
    }

    public void setGift_list(List<BGetGiftItem> gift_list) {
        this.gift_list = gift_list;
    }
}
