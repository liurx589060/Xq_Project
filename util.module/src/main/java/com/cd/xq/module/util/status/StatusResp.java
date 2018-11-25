package com.cd.xq.module.util.status;

/**
 * Created by Administrator on 2018/9/23.
 */

public class StatusResp {
    private boolean isLast = false;
    private int timeDownCount = 0;
    private boolean isEndButtonVisible = false;
    private BaseStatus.MessageType messageType = BaseStatus.MessageType.TYPE_SEND;
    private boolean isSelf = false;
    private BaseStatus.HandleType handleType = BaseStatus.HandleType.HANDLE_NONE;
    private String publicString = "";
    private boolean isResetLive = true;
    private boolean isStopTiming = true;
    private boolean isLadySelect = false;
    private boolean isManSelect = false;

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public int getTimeDownCount() {
        return timeDownCount;
    }

    public void setTimeDownCount(int timeDownCount) {
        this.timeDownCount = timeDownCount;
    }

    public boolean isEndButtonVisible() {
        return isEndButtonVisible;
    }

    public void setEndButtonVisible(boolean endButtonVisible) {
        isEndButtonVisible = endButtonVisible;
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

    public boolean isResetLive() {
        return isResetLive;
    }

    public void setResetLive(boolean resetLive) {
        isResetLive = resetLive;
    }

    public boolean isStopTiming() {
        return isStopTiming;
    }

    public void setStopTiming(boolean stopTiming) {
        isStopTiming = stopTiming;
    }

    public boolean isLadySelect() {
        return isLadySelect;
    }

    public void setLadySelect(boolean ladySelect) {
        isLadySelect = ladySelect;
    }

    public boolean isManSelect() {
        return isManSelect;
    }

    public void setManSelect(boolean manSelect) {
        isManSelect = manSelect;
    }
}
