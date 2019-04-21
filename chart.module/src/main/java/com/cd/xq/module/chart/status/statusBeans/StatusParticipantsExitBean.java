package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.status.StatusResp;

import java.util.List;

import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_NONE;

/**
 * 参与者离开房间
 * Created by Administrator on 2018/9/26.
 */

public class StatusParticipantsExitBean extends ChatBaseStatus {
    @Override
    public String getTypesWithString() {
        return "Participants_Exit_Status";
    }

    @Override
    public String getPublicString() {
        return "参与者离开房间";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_PARTICIPANTS_EXIT_ROOM;
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
        String str = "";
        if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            str += "爱心大使";
        }else if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)) {
            if(DataManager.getInstance().getUserInfo().getGender().equals(Constant.GENDER_MAN)) {
                str += "男嘉宾";
            }else {
                str += "女嘉宾" + DataManager.getInstance().getSelfMember().getIndex();
            }
        }
        sendBean.setMsg(str +"离开房间");
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
    protected void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
    }

    @Override
    public void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        chartUIViewMg.statusParticipantsExit(sendBean);
        String angelName = "";
        List<Member> list = DataManager.getInstance().getChartBChatRoom().getMembers();
        for(int i = 0 ; i < list.size();i++) {
            Member member = list.get(i);
            if(member.getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                angelName = member.getUserInfo().getUser_name();
                break;
            }
        }
        if(sendBean.getUserName().equals(angelName)) {
            //爱心大使退出
            chartUIViewMg.resetLiveStatus();
        }
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {

    }
}
