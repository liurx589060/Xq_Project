package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

import io.reactivex.CompletableOnSubscribe;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusLadyChartFirstBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Lady_Chart_First_Status";
    }

    @Override
    public String getPublicString() {
        return "女生自我介绍阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 120;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_INTRO_LADY;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)%mData.getLimitLady();
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
        int allCount = mData.getLimitLady();
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
            sendBean.setMsg("请女" + nextIndex + "嘉宾发言");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "嘉宾开始");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onStartTime() {

    }

    @Override
    public void onStopTime() {

    }

    @Override
    public void onEnd() {
        if(getStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                || getStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
            if(statusManager.isQuestDisturb()) {
                JMChartRoomSendBean bean = statusManager.getStatus(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING)
                        .getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
                bean.setIndexNext(statusManager.getDisturbAngelIndex());
                statusManager.sendRoomMessage(bean);
                return;
            }
        }

        if(statusManager.getCurrentStatusResp().isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getStartIndex());
            statusManager.sendRoomMessage(bean);
        }else {
            JMChartRoomSendBean bean = getChartSendBeanWillSend(statusManager.getCurrentSendBean(),MessageType.TYPE_SEND);
            bean.setIndexNext(getNextIndex(statusManager.getCurrentSendBean()));
            statusManager.sendRoomMessage(bean);
        }
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        //清空插话状态
        statusManager.setDisturbAngelIndex(-1);
        statusManager.setQuestDisturb(false);
        statusManager.setDisturbing(false);
        if(mHandledIndexList.size() == 1) {
            chartUIViewMg.resetQuestDisturbCount();
        }
        ((StatusHelpDoingDisturbBean)statusManager.getStatus(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING))
                .resetHandleCount();

        chartUIViewMg.stopTiming();
        chartUIViewMg.resetLiveStatus();

        chartUIViewMg.setTipText(getPublicString());
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.speech(sendBean.getMsg());
        chartUIViewMg.startTiming(this,sendBean,statusResp);
        if(getStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                || getStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
            if(selfUserInfoBean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                chartUIViewMg.setBtnDisturbVisible(true);
            }
        }

        if(statusResp.isSelf()) {
            chartUIViewMg.setBtnEndVisible(true);
            //发送回复
            JMChartRoomSendBean bean = getChartSendBeanWillSend(sendBean,MessageType.TYPE_RESPONSE);
            statusManager.sendRoomMessage(bean);
        }
        chartUIViewMg.setLiveStatus(sendBean,statusResp.isSelf());
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
    }
}
