package com.cd.xq.beans;

/**
 * Created by Administrator on 2019/3/20.
 */

public class BGetPayHistory {
    private String order_id = "";
    private String serial_id = "";   //流水号
    private int pay_type = -1;       //支付方式
    private int coin = 0;
    private String user_name = "";
    private int money = 0;
    private int status = 0;  //状态
    private long create_time = 0;
    private long modify_time = 0;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getSerial_id() {
        return serial_id;
    }

    public void setSerial_id(String serial_id) {
        this.serial_id = serial_id;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getModify_time() {
        return modify_time;
    }

    public void setModify_time(long modify_time) {
        this.modify_time = modify_time;
    }
}
