package com.cd.xq.beans;

import com.cd.xq.module.chart.beans.BGetGiftItem;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/5/1.
 */

public class BGetBonusByUser {
    private int bonus_id;
    private String bonus_name = "";
    private String create_time = "";
    private ArrayList<BGetGiftItem> gift;

    public int getBonus_id() {
        return bonus_id;
    }

    public void setBonus_id(int bonus_id) {
        this.bonus_id = bonus_id;
    }

    public String getBonus_name() {
        return bonus_name;
    }

    public void setBonus_name(String bonus_name) {
        this.bonus_name = bonus_name;
    }

    public ArrayList<BGetGiftItem> getGift() {
        return gift;
    }

    public void setGift(ArrayList<BGetGiftItem> gift) {
        this.gift = gift;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
