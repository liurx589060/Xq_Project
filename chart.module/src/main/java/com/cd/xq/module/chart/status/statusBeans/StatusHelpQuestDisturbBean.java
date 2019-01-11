package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;
import com.cd.xq.module.util.tools.Tools;

/**
 * Created by Administrator on 2018/10/13.
 */

public class StatusHelpQuestDisturbBean extends ChatBaseStatus {

    @Override
    public String getTypesWithString() {
        return "Angel_quest_disturb";
    }

    @Override
    public String getPublicString() {
        return "爱心大使请求插话";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_QUEST_DISTURB;
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
        return HandleType.HANDLE_HELP_QUEST_DISTURB;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean bean = createBaseChartRoomSendBean();
        bean.setMsg("爱心大使" + mUserInfo.getUser_name() + "要求插话");
        bean.setProcessStatus(getStatus());
        bean.setMessageType(messageType);
        return bean;
    }

    @Override
    protected boolean checkIsRepeatOrReturn(JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    protected boolean checkIsSelf(JMChartRoomSendBean bean) {
        return true;
    }

    @Override
    protected void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
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
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        statusManager.setDisturbAngelIndex(sendBean.getIndexSelf());
        Tools.toast(activity.getApplication(),sendBean.getMsg(),false);
        statusManager.setQuestDisturb(true);
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
