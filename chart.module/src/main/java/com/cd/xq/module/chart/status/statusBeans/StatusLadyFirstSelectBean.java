package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/1.
 */

public class StatusLadyFirstSelectBean extends ChatBaseStatus {
    private int mCompleteCount = 0;
    private Map<Integer,Boolean> ladySelectedResultMap;

    @Override
    public void initial() {
        super.initial();
        mCompleteCount = 0;
        ladySelectedResultMap.clear();
    }

    public StatusLadyFirstSelectBean() {
        ladySelectedResultMap = new HashMap<>();
    }

    @Override
    public String getTypesWithString() {
        return "Lady_First_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "女生第一次选择";
    }

    @Override
    public int getLiveTimeCount() {
        return 20;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return 0;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_LADY;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLETYPE_GUEST;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_LADY_FIRST;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            sendBean.setMsg("请女生做出选择");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "已做出选择");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        super.onPostHandler(resp,receiveBean);
        if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            mCompleteCount ++;
            int allCount = mBChatRoom.getLimit_lady();
            boolean isLast = mCompleteCount>=allCount?true:false;
            resp.setLast(isLast);
            if(isLast) {
                mCompleteCount = 0;
            }
        }
    }

    @Override
    public void onStartTime() {

    }

    @Override
    public void onStopTime() {

    }

    @Override
    public void onEnd() {
        if(statusManager.getCurrentStatusResp().isSelf()) {
            JMChartRoomSendBean bean = getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_RESPONSE);
            bean.setLadySelected(statusManager.isLadyAccept());
            statusManager.sendRoomMessage(bean);
        }
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.speech(sendBean.getMsg());
        chartUIViewMg.operate_SelectLady(this,sendBean,statusResp);
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        ladySelectedResultMap.put(sendBean.getIndexSelf(),sendBean.isLadySelected());
        if(statusResp.isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(sendBean,MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getStartIndex());
            statusManager.sendRoomMessage(bean);
        }
    }

    @Override
    public boolean checkSelfIndex(JMChartRoomSendBean receiveBean) {
        return true;
    }

    public Map<Integer, Boolean> getLadySelectedResultMap() {
        return ladySelectedResultMap;
    }
}
