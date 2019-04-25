package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/10/13.
 */

public class StatusHelpDoingDisturbBean extends ChatBaseStatus {
    private boolean isSelfDisturbing;

    @Override
    public void initial() {
        super.initial();
        isSelfDisturbing = false;
    }

    @Override
    public String getTypesWithString() {
        return "Angel_Doing_disturb";
    }

    @Override
    public String getPublicString() {
        return "爱心大使插话";
    }

    @Override
    public int getLiveTimeCount() {
        return 120;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return 0;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_ALL;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLRTYPE_ANGEL;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_HELP_DOING_DISTURB;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return true;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            int nextIndex = getNextIndex(receiveBean) + 1;
            sendBean.setMsg("请爱心大使插话");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg("爱心大使开始插话");
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
    public boolean isHandleSelf() {
        return (statusManager.isDisturbing() && isSelfDisturbing);
    }

    @Override
    public void onEnd() {
        resetHandleCount();
        isSelfDisturbing = false;
        statusManager.setDisturbAngelIndex(-1);
        statusManager.setQuestDisturb(false);
        statusManager.setDisturbing(false);

        if(statusManager.getCurrentStatusResp().isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getNextStatus().getStartIndex());
            statusManager.sendRoomMessage(bean);
        }else {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getNextIndex(statusManager.getCurrentSendBean()));
            statusManager.sendRoomMessage(bean);
        }
    }

    @Override
    protected void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        this.setNextStatus(statusManager.getCurrentStatus());
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.speech(sendBean.getMsg());
        chartUIViewMg.startTiming(this,sendBean,statusResp);
        statusManager.setDisturbing(true);
        if(statusResp.isSelf()) {
            chartUIViewMg.setBtnEndVisible(true);
            //发送回复
            JMChartRoomSendBean bean = getChartSendBeanWillSend(sendBean,MessageType.TYPE_RESPONSE);
            statusManager.sendRoomMessage(bean);
        }
        chartUIViewMg.setLiveStatus(sendBean,statusResp.isSelf());
        isSelfDisturbing = true;
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
    }

    public void resetHandleCount() {
        if(isSelfDisturbing) {
            mHandledIndexList.clear();
        }
    }
}
