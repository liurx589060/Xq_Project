package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusManFirstQuestionBean extends StatusManIntroBean {
    @Override
    public String getTypesWithString() {
        return "Man_First_Question_Status";
    }

    @Override
    public String getPublicString() {
        return "第一次问答环节-男";
    }

    @Override
    public int getLiveTimeCount() {
        return 60;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)%mData.getLimit_man();
        return index;
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
        return HandleType.HANDLE_TIME;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        int allCount = mData.getLimit_man();
        boolean isLast = completeCount>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            int nextIndex;
            if(receiveBean.getProcessStatus() != getStatus()) {
                nextIndex = getStartIndex() + 1;
            }else {
                nextIndex = getNextIndex(receiveBean) + 1;
            }
            sendBean.setMsg("请男" + nextIndex + "提问");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "开始提问");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onEnd() {
        if(statusManager.getCurrentStatusResp().isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            int startIndex = ((StatusManFirstSelectBean)statusManager.getStatus(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).getSelectLadyIndex();
            bean.setIndexNext(startIndex);
            statusManager.sendRoomMessage(bean);
        }else {
            JMChartRoomSendBean bean = getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextIndex(statusManager.getCurrentSendBean()));
            statusManager.sendRoomMessage(bean);
        }
    }
}
