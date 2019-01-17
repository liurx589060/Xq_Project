package com.cd.xq.module.util.beans;

/**
 * Created by Administrator on 2018/12/9.
 */

public class NetResult<T> {
    private int status;
    private String msg = "";
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
