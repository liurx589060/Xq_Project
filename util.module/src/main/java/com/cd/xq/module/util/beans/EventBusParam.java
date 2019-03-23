package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2019/1/27.
 */

public class EventBusParam<T> {
    public static final int EVENT_BUS_CHATROOM_CREATE = 1;  //创建房间
    public static final int EVENT_BUS_CHATROOM_DELETE = 2;  //删除房间

    public static final int EVENT_BUS_GET_FRIENDLIST = 200;   //获取好友列表
    public static final int EVENT_BUS_UPDATE_FRIENDLIST = 201; //更新好友列表

    public static final int EVENT_BUS_PAY_SUCCESS = 100; //订单支付成功

    public static final int EVENT_BUS_GIFT_BUY = 300; //购买礼物

    private int eventBusCode;
    private T data;

    public int getEventBusCode() {
        return eventBusCode;
    }

    public void setEventBusCode(int eventBusCode) {
        this.eventBusCode = eventBusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
