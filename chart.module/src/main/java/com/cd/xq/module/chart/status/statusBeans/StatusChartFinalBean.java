package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;
import com.cd.xq.module.util.tools.Tools;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusChartFinalBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Chart_Final_Status";
    }

    @Override
    public String getPublicString() {
        return "流程结束";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL;
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
        return HandleType.HANDLE_FINISH;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        sendBean.setMsg("流程结束");
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

    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        String text = sendBean.getMsg() + ",流程已结束";
        chartUIViewMg.setTipText(text);
        text += ",可自行离开";
        chartUIViewMg.speech(text);
        chartUIViewMg.sendDoubleRoom();
        Tools.toast(activity.getApplication(),sendBean.getMsg(),false);
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }

    @Override
    public boolean checkSelfIndex(JMChartRoomSendBean receiveBean) {
        return true;
    }
}
