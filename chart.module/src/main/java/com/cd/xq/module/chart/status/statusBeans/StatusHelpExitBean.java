package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/10/13.
 */

public class StatusHelpExitBean extends BaseStatus {
    @Override
    public String getTypesWithString() {
        return "Help_Exit";
    }

    @Override
    public String getPublicString() {
        return "离开房间";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM;
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
        return HandleType.HANDLE_HELP_EXIT;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        sendBean.setMsg("嘉宾"+ mUserInfo.getUser_name() +"离开房间");
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        sendBean.setUpdateMembers(true);
        return sendBean;
    }

    @Override
    public void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        resp.setResetLive(false);
        resp.setStopTiming(false);
    }

    @Override
    protected boolean checkIsRepeatOrReturn(JMChartRoomSendBean receiveBean) {
        return false;
    }
}
