package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/10/1.
 */

public class StatusManFirstSelectBean extends BaseStatus {
    private int mCompleteCount = 0;
    private int mSelectLadyIndex = -1;

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
        if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            mCompleteCount ++;
            int allCount = mData.getLimitMan();
            boolean isLast = mCompleteCount>=allCount?true:false;
            resp.setLast(isLast);
            if(isLast) {
                mCompleteCount = 0;
            }
        }

        resp.setManSelect(true);
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setResetLive(true);
            resp.setStopTiming(true);
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            resp.setResetLive(false);
            resp.setStopTiming(false);
            try {
                mSelectLadyIndex = Integer.parseInt(receiveBean.getManSelects());
            }catch (Exception e) {
                mSelectLadyIndex = -1;
            }
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
