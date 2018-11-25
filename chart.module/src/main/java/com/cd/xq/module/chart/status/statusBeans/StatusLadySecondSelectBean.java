package com.cd.xq.module.chart.status.statusBeans;


import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusLadySecondSelectBean extends StatusLadyFirstSelectBean {
    @Override
    public String getTypesWithString() {
        return "Lady_First_Second_Status";
    }

    @Override
    public String getPublicString() {
        return "女生第二次选择";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_LADY_SECOND;
    }
}
