package com.cd.xq.module.util.beans.jmessage;

import com.cd.xq.module.util.beans.BaseResp;

/**
 * Created by Administrator on 2018/5/23.
 */

public class JMChartResp extends BaseResp {
    private BChatRoom data;

    public BChatRoom getData() {
        return data;
    }

    public void setData(BChatRoom data) {
        this.data = data;
    }
}
