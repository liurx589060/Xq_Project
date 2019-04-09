package com.cd.xq.module.chart.manager;

import android.app.Activity;

import com.cd.xq.module.chart.status.statusBeans.ChatBaseStatus;
import com.cd.xq.module.chart.status.statusBeans.EmptyChatBean;
import com.cd.xq.module.chart.status.statusBeans.StatusAngelChartBean;
import com.cd.xq.module.chart.status.statusBeans.StatusChartFinalBean;
import com.cd.xq.module.chart.status.statusBeans.StatusConsumeGiftBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpChangeLiveTypeBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpDoingDisturbBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpExitBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpQuestDisturbBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyChartFirstBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyChartSecondBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyFinalSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyFirstQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyFirstSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadySecondQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadySecondSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManFinalSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManFirstQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManFirstSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManIntroBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManPerformanceBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManSecondQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManSecondSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusMatchBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerEnterBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerExitBean;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.StatusResp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/30.
 */

public class StatusManager {
    private XqStatusChartUIViewMg mChatUIViewMg;
    private Activity mActivity;
    private boolean isQuestDisturb;   //是否申请了插话
    private boolean isDisturbing; //是否正在插化中
    private ChatBaseStatus currentStatus;
    private StatusResp currentStatusResp;
    private JMChartRoomSendBean currentSendBean;
    private int manSelected = -1;
    private boolean isLadyAccept;
    private boolean mIsRoomMatchSuccess;
    private boolean mIsSelfMatchSuccess;
    private int disturbAngelIndex = -1;

    private Map<Integer,ChatBaseStatus> mOrderStatusMap = null;
    private Map<Integer,ChatBaseStatus> mHelpStatusMap = null;
    private List<ChatBaseStatus> mHandleSelfList;

    public StatusManager(Activity activity,XqStatusChartUIViewMg uiViewMg) {
        mChatUIViewMg = uiViewMg;
        mActivity = activity;

        initStatus();
    }

    private void initStatus() {
        mOrderStatusMap = new LinkedHashMap<>();
        mHelpStatusMap = new LinkedHashMap<>();
        mHandleSelfList = new ArrayList<>();

        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MATCHING,new StatusMatchBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN,new StatusManIntroBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST,new StatusLadyFirstSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_INTRO_LADY,new StatusLadyChartFirstBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST,new StatusManFirstSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE,new StatusManPerformanceBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND,new StatusLadySecondSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND,new StatusLadyChartSecondBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT,new StatusAngelChartBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND,new StatusManSecondSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL,new StatusLadyFinalSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST,new StatusManFirstQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST,new StatusLadyFirstQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_SECOND,new StatusManSecondQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND,new StatusLadySecondQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL,new StatusManFinalSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL,new StatusChartFinalBean());

        //设置流程序列
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        BaseStatus preStatus = null;
        while (entry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) entry.next();
            int key = en.getKey();
            en.getValue().setmOrder(key);
            en.getValue().setStatusManager(this);

            if(preStatus != null) {
                preStatus.setNextStatus(en.getValue());
            }
            preStatus = en.getValue();
        }

        //添加辅助状态机
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_CHANGR_LIVETYPE,new StatusHelpChangeLiveTypeBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING,new StatusHelpDoingDisturbBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_QUEST_DISTURB,new StatusHelpQuestDisturbBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM,new StatusHelpExitBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_ONLOOKER_ENTER,new StatusOnLookerEnterBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_ONLOOKER_EXIT,new StatusOnLookerExitBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_GIFT_CONSUMR_STATUS,new StatusConsumeGiftBean());

        //设置流程序列
        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) helpEntry.next();
            int key = en.getKey();
            en.getValue().setmOrder(key);
            en.getValue().setStatusManager(this);
        }
    }

    public BaseStatus getStatus(int statusIndex) {
        BaseStatus baseStatus = mOrderStatusMap.get(statusIndex);
        if(baseStatus == null) {
            baseStatus = mHelpStatusMap.get(statusIndex);
        }
        return baseStatus;
    }

    public void handlerRoomChart(JMChartRoomSendBean sendBean) {
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) entry.next();
            en.getValue().handlerRoomChart(sendBean);
        }

        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) helpEntry.next();
            en.getValue().handlerRoomChart(sendBean);
        }
    }

    public void onStartTime() {
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) entry.next();
            en.getValue().onStartTime();
        }

        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) helpEntry.next();
            en.getValue().onStartTime();
        }
    }

    public void onStopTime() {
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) entry.next();
            en.getValue().onStopTime();
        }

        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) helpEntry.next();
            en.getValue().onStopTime();
        }
    }

    public void onEnd() {
        mHandleSelfList.clear();
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) entry.next();
            if(en.getValue().isHandleSelf()) {
                mHandleSelfList.add(en.getValue());
            }
        }

        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry<Integer,ChatBaseStatus> en = (Map.Entry) helpEntry.next();
            if(en.getValue().isHandleSelf()) {
                mHandleSelfList.add(en.getValue());
            }
        }

        for (ChatBaseStatus status:mHandleSelfList
             ) {
            status.onEnd();
        }
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public XqStatusChartUIViewMg getmChatUIViewMg() {
        return mChatUIViewMg;
    }

    public ChatBaseStatus getCurrentStatus() {
        if(currentStatus == null) {
            currentStatus = new EmptyChatBean();
        }
        return currentStatus;
    }

    public void setCurrentStatus(ChatBaseStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getManSelected() {
        return manSelected;
    }

    public void setManSelected(int manSelected) {
        this.manSelected = manSelected;
    }

    /**
     *发送聊天室信息
     * @param chartRoomSendBean
     */
    public void sendRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        JMsgSender.sendRoomMessage(chartRoomSendBean);
        handlerRoomChart(chartRoomSendBean);
    }

    public boolean isLadyAccept() {
        return isLadyAccept;
    }

    public void setLadyAccept(boolean ladyAccept) {
        isLadyAccept = ladyAccept;
    }

    public StatusResp getCurrentStatusResp() {
        return currentStatusResp;
    }

    public void setCurrentStatusResp(StatusResp currentStatusResp) {
        if(currentStatusResp == null) {
            currentStatusResp = new StatusResp();
        }
        this.currentStatusResp = currentStatusResp;
    }

    public JMChartRoomSendBean getCurrentSendBean() {
        if(currentSendBean == null) {
            currentSendBean = new JMChartRoomSendBean();
        }
        return currentSendBean;
    }

    public void setCurrentSendBean(JMChartRoomSendBean currentSendBean) {
        this.currentSendBean = currentSendBean;
    }

    public boolean isRoomMatchSuccess() {
        return mIsRoomMatchSuccess;
    }

    public void setRoomMatchSuccess(boolean mIsRoomMatchSuccess) {
        this.mIsRoomMatchSuccess = mIsRoomMatchSuccess;
    }

    public boolean isSelfMatchSuccess() {
        return mIsSelfMatchSuccess;
    }

    public void setSelfMatchSuccess(boolean mIsSelfMatchSuccess) {
        this.mIsSelfMatchSuccess = mIsSelfMatchSuccess;
    }

    public int getDisturbAngelIndex() {
        return disturbAngelIndex;
    }

    public void setDisturbAngelIndex(int disturbAngelIndex) {
        this.disturbAngelIndex = disturbAngelIndex;
    }

    public boolean isQuestDisturb() {
        return isQuestDisturb;
    }

    public void setQuestDisturb(boolean questDisturb) {
        isQuestDisturb = questDisturb;
    }

    public boolean isDisturbing() {
        return isDisturbing;
    }

    public void setDisturbing(boolean disturbing) {
        isDisturbing = disturbing;
    }
}
