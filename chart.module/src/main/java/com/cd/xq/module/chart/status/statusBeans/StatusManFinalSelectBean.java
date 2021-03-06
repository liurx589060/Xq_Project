package com.cd.xq.module.chart.status.statusBeans;

import android.annotation.TargetApi;
import android.os.Build;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;

import java.util.Map;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusManFinalSelectBean extends StatusManFirstSelectBean {
    @Override
    public String getTypesWithString() {
        return "Man_Final_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "男生最终选择";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL;
    }

    @Override
    public BaseStatus.HandleType getHandleType() {
        return BaseStatus.HandleType.HANDLE_SELECT_MAN_FINAL;
    }

    @Override
    public void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean) {
        chartUIViewMg.addSystemEventAndRefresh(sendBean);
        mSelectLadyIndex = sendBean.getManSelects();

        String text = "非常遗憾，匹配失败";
        Map<Integer,Boolean> ladySelectedMap = ((StatusLadyFinalSelectBean)statusManager.getStatus(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL))
                .getLadySelectedResultMap();
        boolean finalResult = false;
        if(ladySelectedMap.containsKey(mSelectLadyIndex)) {
            finalResult = ladySelectedMap.get(mSelectLadyIndex);
        }
        boolean mIsRoomMatchSuccess = false;
        boolean mIsSelfMatchSuccess = false;
        if(finalResult) {
            text = "恭喜匹配成功";
            mIsRoomMatchSuccess = true;
            if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                if (DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS
                        && DataManager.getInstance().getUserInfo().getGender().equals(Constant.GENDER_LADY)
                        && mSelectLadyIndex == DataManager.getInstance().getSelfMember().getIndex()) {
                    mIsSelfMatchSuccess = true;
                }

                if (DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS
                        && DataManager.getInstance().getUserInfo().getGender().equals(Constant.GENDER_MAN)) {
                    //男生
                    mIsSelfMatchSuccess = true;
                }
            }
        }

        statusManager.setRoomMatchSuccess(mIsRoomMatchSuccess);
        statusManager.setSelfMatchSuccess(mIsSelfMatchSuccess);
        sendBean.setMsg(text);
        chartUIViewMg.statusManFinalSelected(sendBean);
        Tools.toast(activity,"自己匹配成功--" + mIsSelfMatchSuccess,false);
        Log.e("yy","自己匹配成功--" + mIsSelfMatchSuccess);
    }
}
