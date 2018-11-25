package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusLadyChartSecondBean extends StatusLadyChartFirstBean {
    @Override
    public String getTypesWithString() {
        return "Lady_Chart_Second_Status";
    }

    @Override
    public String getPublicString() {
        return "女生第二次谈话阶段";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_TIME;
    }
}
