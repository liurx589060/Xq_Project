package com.cd.xq.module.chart.beans;

/**
 * Created by Administrator on 2019/3/21.
 */

public class BGetGiftItem {
    private int sort;
    private int type;
    private int gift_id;
    private String name = "";
    private int coin;
    private String value;
    private int is_trade;
    private String image = "";
    private String description = "";
    private int num;
    private String gif = "";
    private int status;
    private int expiry_num;
    private String start_time = "";
    private String end_time = "";

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    public int getIs_trade() {
        return is_trade;
    }

    public void setIs_trade(int is_trade) {
        this.is_trade = is_trade;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
