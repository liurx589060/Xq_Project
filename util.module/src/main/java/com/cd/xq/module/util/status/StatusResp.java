package com.cd.xq.module.util.status;

/**
 * Created by Administrator on 2018/9/23.
 */

public class StatusResp {
    private boolean isLast = false;
    private boolean isEndButtonVisible = false;
    private BaseStatus.MessageType messageType = BaseStatus.MessageType.TYPE_SEND;
    private boolean isSelf = false;
    private BaseStatus.HandleType handleType = BaseStatus.HandleType.HANDLE_NONE;
    private String publicString = "";

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public BaseStatus.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(BaseStatus.MessageType messageType) {
        this.messageType = messageType;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public BaseStatus.HandleType getHandleType() {
        return handleType;
    }

    public void setHandleType(BaseStatus.HandleType handleType) {
        this.handleType = handleType;
    }

    public String getPublicString() {
        return publicString;
    }

    public void setPublicString(String publicString) {
        this.publicString = publicString;
    }

}
