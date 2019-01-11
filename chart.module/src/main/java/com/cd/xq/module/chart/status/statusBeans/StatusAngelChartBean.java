package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusAngelChartBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Angel_Chart_Status";
    }

    @Override
    public String getPublicString() {
        return "爱心大使有话说";
    }

    @Override
    public int getLiveTimeCount() {
        return 120;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)%mData.getLimitAngel();
        return index;
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
        return HandleType.HANDLE_TIME;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        int allCount = mData.getLimitAngel();
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
            sendBean.setMsg("请爱心大使" + nextIndex + "发言");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "爱心大使开始介绍");
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
        JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
        bean.setIndexNext(getNextStatus().getStartIndex());
        statusManager.sendRoomMessage(bean);
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        //清空插话状态
        statusManager.setDisturbAngelIndex(-1);
        statusManager.setQuestDisturb(false);
        statusManager.setDisturbing(false);

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
