package com.cd.xq.module.chart.status.statusBeans;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusManPerformanceBean extends StatusManIntroBean {
    @Override
    public String getTypesWithString() {
        return "Man_Performance_Status";
    }

    @Override
    public String getPublicString() {
        return "男生才艺表演阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 180;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE;
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
            sendBean.setMsg("请男嘉宾才艺表演");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg("男嘉宾开始才艺表演");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }
}
