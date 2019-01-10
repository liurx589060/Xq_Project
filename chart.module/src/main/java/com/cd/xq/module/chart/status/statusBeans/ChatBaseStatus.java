package com.cd.xq.module.chart.status.statusBeans;

import android.app.Activity;

import com.cd.xq.module.chart.manager.StatusManager;
import com.cd.xq.module.chart.manager.XqStatusChartUIViewMg;
import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/12/30.
 */

public abstract class ChatBaseStatus extends BaseStatus {
    protected StatusManager statusManager;
    protected Activity activity;
    protected XqStatusChartUIViewMg chartUIViewMg;
    protected UserInfoBean selfUserInfoBean;
    protected Data chatRoomData;
    protected Member selfMember;

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
        activity = statusManager.getmActivity();
        chartUIViewMg = statusManager.getmChatUIViewMg();

        selfUserInfoBean = DataManager.getInstance().getUserInfo();
        chatRoomData = DataManager.getInstance().getChartData();
        selfMember = DataManager.getInstance().getSelfMember();
    }

    @Override
    public int handlerRoomChart(JMChartRoomSendBean receiveBean) {
        int result = super.handlerRoomChart(receiveBean);
        if(result != HANDLE_SUCCESS) {
            return result;
        }
        return result;
    }

    @Override
    protected void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        super.onPostHandler(resp, receiveBean);
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            statusManager.setCurrentStatus(this);
            statusManager.setCurrentSendBean(receiveBean);
            statusManager.setCurrentStatusResp(resp);
        }
    }

    public boolean isHandleSelf() {
        return (this == statusManager.getCurrentStatus());
    }
}
