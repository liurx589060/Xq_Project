package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusManIntroBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Intro_Man_Status";
    }

    @Override
    public String getPublicString() {
        return "男生自我介绍阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 180;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_INTRO_MAN;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)% mBChatRoom.getLimit_man();
        return index;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_MAN;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLETYPE_GUEST;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_TIME;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        int allCount = mBChatRoom.getLimit_man();
        boolean isLast = completeCount>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            int nextIndex;
            if(receiveBean.getProcessStatus() != getStatus()) {
                nextIndex = getStartIndex() + 1;
            }else {
                nextIndex = getNextIndex(receiveBean) + 1;
            }
            sendBean.setMsg("请男" + nextIndex + "嘉宾发言");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "嘉宾开始介绍");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onStartTime() {

    }

    @Override
    public void onStopTime() {

    }

    @Override
    public void onEnd() {
        if(statusManager.getCurrentStatusResp().isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getStartIndex());
            statusManager.sendRoomMessage(bean);
        }else {
            JMChartRoomSendBean bean = getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextIndex(statusManager.getCurrentSendBean()));
            statusManager.sendRoomMessage(bean);
        }
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.speech(sendBean.getMsg());
        chartUIViewMg.startTiming(this,sendBean,statusResp);
        if(statusResp.isSelf()) {
            chartUIViewMg.setBtnEndVisible(true);
            //发送回复
            JMChartRoomSendBean bean = getChartSendBeanWillSend(sendBean,MessageType.TYPE_RESPONSE);
            statusManager.sendRoomMessage(bean);
        }
        chartUIViewMg.setLiveStatus(sendBean,statusResp.isSelf());
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
    }
}
