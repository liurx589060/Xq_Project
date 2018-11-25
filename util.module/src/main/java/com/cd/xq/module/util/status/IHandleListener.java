package com.cd.xq.module.util.status;

import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/9/23.
 */

public interface IHandleListener {
    void onHandleResp(BaseStatus statusInstance, StatusResp statusResp, JMChartRoomSendBean sendBean);
}
