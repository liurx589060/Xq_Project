package com.cd.xq.beans;

/**
 * Created by Administrator on 2019/1/27.
 */

public class JMOnlineParam {
    public static final int TYPE_SEND = 1;
    public static final int TYPE_RESPONSE = 2;

    private int type;
    private boolean isOnLine = false;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }
}
