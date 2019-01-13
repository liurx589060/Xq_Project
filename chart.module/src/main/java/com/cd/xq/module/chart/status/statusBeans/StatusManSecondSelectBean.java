package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusManSecondSelectBean extends StatusManFirstSelectBean {
    @Override
    public String getTypesWithString() {
        return "Man_Second_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "男生第二次选择";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_MAN_SECOND;
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        if(sendBean.getManSelects() == -1) {
            mSelectLadyIndex = ((StatusManFirstSelectBean)statusManager.getStatus(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST))
                    .getSelectLadyIndex() + 1;
        }else {
            mSelectLadyIndex = sendBean.getManSelects();
        }
        if(statusResp.isLast()) {
            JMChartRoomSendBean bean = getNextStatus().getChartSendBeanWillSend(sendBean,MessageType.TYPE_SEND);
            bean.setIndexNext(getNextStatus().getStartIndex());
            statusManager.sendRoomMessage(bean);
        }
    }
}
