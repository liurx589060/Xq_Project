package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/10/1.
 */

public class StatusManFirstSelectBean extends ChatBaseStatus {
    private int mCompleteCount = 0;
    protected int mSelectLadyIndex = -1;

    @Override
    public String getTypesWithString() {
        return "Man_First_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "男生第一次选择";
    }

    @Override
    public int getLiveTimeCount() {
        return 20;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return 0;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_MAN;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLETYPE_GUEST;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_MAN_FIRST;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            sendBean.setMsg("请男生做出选择");
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
            int allCount = mData.getLimit_man();
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
            bean.setManSelects(statusManager.getManSelected());
            statusManager.sendRoomMessage(bean);
        }
        statusManager.setManSelected(-1);
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        //清空插话状态
        statusManager.setDisturbAngelIndex(-1);
        statusManager.setQuestDisturb(false);
        statusManager.setDisturbing(false);

        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.speech(sendBean.getMsg());
        chartUIViewMg.operate_SelectMan(this,sendBean,statusResp);
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        if(sendBean.getManSelects() == -1) {
            mSelectLadyIndex = 0;
        }else {
            mSelectLadyIndex = sendBean.getManSelects();
        }
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

    public int getSelectLadyIndex() {
        return mSelectLadyIndex;
    }

    public void setSelectLadyIndex(int mSelectLadyIndex) {
        this.mSelectLadyIndex = mSelectLadyIndex;
    }
}
