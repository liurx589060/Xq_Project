package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;
import com.cd.xq.module.util.tools.Tools;

import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_NONE;

/**
 * 解散房间
 * Created by Administrator on 2018/9/26.
 */

public class StatusDeleteRoomBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "delete_room_Status";
    }

    @Override
    public String getPublicString() {
        return "房间解散";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_DELETE_ROOM;
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
        sendBean.setMsg("房间已解散");
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
    protected void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        //解散房间
        chartUIViewMg.statusDeleteRoom();
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
