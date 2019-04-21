package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;

import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_MATCH;

/**
 * 匹配状态
 * Created by Administrator on 2018/9/26.
 */

public class StatusMatchBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Match_Status";
    }

    @Override
    public String getPublicString() {
        return "匹配阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_MATCHING;
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
        return HANDLE_MATCH;
    }

    @Override
    public boolean isLast(int completeCount,JMChartRoomSendBean receiveBean) {
        int allCount = mBChatRoom.getLimit_angel() + mBChatRoom.getLimit_man() + mBChatRoom.getLimit_lady();
        boolean isLast = receiveBean.getCurrentCount()>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        sendBean.setMsg("嘉宾"+ mUserInfo.getUser_name() +"进入房间");
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
        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.requestGetChatRoomMemberParticipants();
        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.speech(sendBean.getMsg());

        if(statusResp.isLast()) {
            chartUIViewMg.statusMatch(sendBean);
        }
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
