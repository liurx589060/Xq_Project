package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2019/1/3.
 */

public class EmptyChatBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return null;
    }

    @Override
    public String getPublicString() {
        return null;
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return 0;
    }

    @Override
    public String getRequestGender() {
        return null;
    }

    @Override
    public String getRequestRoleType() {
        return null;
    }

    @Override
    public HandleType getHandleType() {
        return null;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        return null;
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

    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
