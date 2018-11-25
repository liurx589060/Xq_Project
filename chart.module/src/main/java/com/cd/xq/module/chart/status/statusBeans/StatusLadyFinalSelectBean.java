package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusLadyFinalSelectBean extends StatusLadyFirstSelectBean {
    @Override
    public String getTypesWithString() {
        return "Lady_Final_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "女生最终选择";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL;
    }

    @Override
    public BaseStatus.HandleType getHandleType() {
        return BaseStatus.HandleType.HANDLE_SELECT_LADY_FINAL;
    }
}
