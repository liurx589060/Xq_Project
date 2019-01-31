package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2019/1/27.
 */

public class EventBusParam<T> {
    public static final int EVENT_BUS_UPDATE_CHATROOM = 0x2000;
    public static final int EVENT_BUS_GET_FRIENDLIST = 0x2001;
    public static final int EVENT_BUS_UPDATE_FRIENDLIST = 0x2002;

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
