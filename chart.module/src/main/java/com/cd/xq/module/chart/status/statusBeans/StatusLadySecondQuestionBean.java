package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/10/20.
 */

public class StatusLadySecondQuestionBean extends StatusLadyFirstQuestionBean {
    @Override
    public String getTypesWithString() {
        return "Lady_Second_Question_Status";
    }

    @Override
    public String getPublicString() {
        return "第二次问答环节-女";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND;
    }
}
