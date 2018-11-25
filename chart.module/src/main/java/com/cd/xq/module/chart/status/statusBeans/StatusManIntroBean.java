package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusManIntroBean extends BaseStatus {
    @Override
    public String getTypesWithString() {
        return "Intro_Man_Status";
    }

    @Override
    public String getPublicString() {
        return "男生自我介绍阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 180;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_INTRO_MAN;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)%mData.getLimitMan();
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
        int allCount = mData.getLimitMan();
        boolean isLast = completeCount>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            int nextIndex;
            if(receiveBean.getProcessStatus() != getStatus()) {
                nextIndex = getStartIndex();
            }else {
                nextIndex = getNextIndex(receiveBean);
            }
            sendBean.setMsg("请男" + nextIndex + "玩家发言");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "玩家开始介绍");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setResetLive(true);
            resp.setStopTiming(true);
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            resp.setResetLive(false);
            resp.setStopTiming(false);
        }
    }
}
