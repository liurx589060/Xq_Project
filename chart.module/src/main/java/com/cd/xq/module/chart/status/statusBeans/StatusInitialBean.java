package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;

import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_MATCH;
import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_NONE;

/**
 * 最初状态
 * Created by Administrator on 2018/9/26.
 */

public class StatusInitialBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Initial_Status";
    }

    @Override
    public String getPublicString() {
        return "人员进场";
    }

    @Override
    public int getLiveTimeCount() {
        return 60;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_INITIAL;
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
        return Constant.ROLETYPE_ALL;
    }

    @Override
    public HandleType getHandleType() {
        return HANDLE_NONE;
    }

    @Override
    public boolean isLast(int completeCount,JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        sendBean.setMsg("重新准备房间");
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);

        return sendBean;
    }

    @Override
    protected boolean checkIsRepeatOrReturn(JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public void onStartTime() {

    }

    @Override
    public void onStopTime() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();
        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.statusInitialRoom(sendBean);
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
