package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusLadyFirstQuestionBean extends StatusLadyChartFirstBean {

    @Override
    public String getTypesWithString() {
        return "Lady_First_Question_Status";
    }

    @Override
    public String getPublicString() {
        return "第一次问答环节-女";
    }

    @Override
    public int getLiveTimeCount() {
        return 90;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)% mBChatRoom.getLimit_lady();
        return index;
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
        return HandleType.HANDLE_TIME;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        int allCount = mBChatRoom.getLimit_lady();
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
            sendBean.setMsg("请女嘉宾" + nextIndex + "回答");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg("女嘉宾" + (getNextIndex(receiveBean)+1) + "开始");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onEnd() {
        if(statusManager.getCurrentStatusResp().isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getStartIndex());
            statusManager.sendRoomMessage(bean);
        }else {
            JMChartRoomSendBean bean = getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            int startIndex = ((StatusManSecondSelectBean)statusManager.getStatus(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).getSelectLadyIndex();
            bean.setIndexNext(startIndex);
            statusManager.sendRoomMessage(bean);
        }
    }
}
