package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.manager.XqStatusChartUIViewMg;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;
import com.google.gson.Gson;

import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_NONE;

/**
 * 匹配状态
 * Created by Administrator on 2018/9/26.
 */

public class StatusConsumeGiftBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Gift_Consume_Status";
    }

    @Override
    public String getPublicString() {
        return "礼物消费";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_HELP_GIFT_CONSUMR_STATUS;
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
    protected void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
    }

    @Override
    public void onStopTime() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        XqStatusChartUIViewMg.ChatGiftInstance item = new Gson().fromJson(sendBean.getData()
                ,XqStatusChartUIViewMg.ChatGiftInstance.class);
        chartUIViewMg.doConsumeGift(item);
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.updateOnLookerList();
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
