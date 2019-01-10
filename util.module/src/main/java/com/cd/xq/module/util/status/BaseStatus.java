package com.cd.xq.module.util.status;

import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.Tools;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/23.
 */

public abstract class BaseStatus {
    public static final int HANDLE_SUCCESS = 1;
    public static final int HANDLE_NULL = 7000;  //参数为null
    public static final int HANDLE_NOT_MATCH = 7001;  //不是自己的状态
    public static final int HANDLE_REPEAT = 7002;  //重复

    public int getmOrder() {
        return mOrder;
    }

    public void setmOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public void setStartIndex(int mStartIndex) {
        this.mStartIndex = mStartIndex;
    }

    public enum MessageType {
        TYPE_SEND,
        TYPE_RESPONSE,
        TYPE_END
    }

    public enum HandleType {
        HANDLE_NONE,
        HANDLE_TIME,
        HANDLE_SELECT_MAN_FIRST,
        HANDLE_SELECT_LADY_FIRST,
        HANDLE_SELECT_LADY_SECOND,
        HANDLE_SELECT_MAN_SECOND,
        HANDLE_SELECT_LADY_FINAL,
        HANDLE_SELECT_MAN_FINAL,
        HANDLE_MATCH,
        HANDLE_FINISH,

        HANDLE_HELP_QUEST_DISTURB,
        HANDLE_HELP_DOING_DISTURB,
        HANDLE_HELP_CHANGE_LIVETYPE,
        HANDLE_HELP_EXIT
    }

    private int mCompleteCount = 0;
    protected Data mData ;
    protected UserInfoBean mUserInfo;
    protected Member mSelfMember;
    private int mOrder = -1;//流程序号
    private int mStartIndex = 0;//轮转的开始索引
    protected ArrayList<Integer> mHandledIndexList;
    private BaseStatus mNextStatus;

    public BaseStatus() {
        mData = DataManager.getInstance().getChartData();
        mUserInfo = DataManager.getInstance().getUserInfo();
        mSelfMember = DataManager.getInstance().getSelfMember();

        mHandledIndexList = new ArrayList<>();
    }

    /**
     * 字符的类型标识
     * @return
     */
    public abstract String getTypesWithString();
    /**
     * 对外解释
     * @return
     */
    public abstract String getPublicString();

    /**
     * 倒计时
     * @return
     */
    public abstract int getLiveTimeCount();

    /**
     * 状态（int型）
     * @return
     */
    public abstract int getStatus();

    /**
     *下一个的算法
     * @param receiveBean
     * @return
     */
    public abstract int getNextIndex(JMChartRoomSendBean receiveBean);
    /**
     *判断下一个的索引
     * @param receiveBean
     * @return
     */
    public boolean checkSelfIndex(JMChartRoomSendBean receiveBean) {
        if(mSelfMember.getIndex() == receiveBean.getIndexNext()) {
            return true;
        }
        return false;
    }
    /**
     * 必须的性别
     * @return
     */
    public abstract String getRequestGender();

    /**
     * 必须的角色
     * @return
     */
    public abstract String getRequestRoleType();

    /**
     * 接受消息后需要处理的方式
     * @return
     */
    public abstract HandleType getHandleType();

    /**
     * 检测是不是最后一个人
     * @param completeCount
     * @param receiveBean
     * @return
     */
    public abstract boolean isLast(int completeCount,JMChartRoomSendBean receiveBean);

    /**
     * 获取要发送的JMChartRoomSendBean
     * @return
     */
    public abstract JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType);

    /**
     * 状态自己解析，然后把内容传入resp和sendBean中
     * @param resp
     * @param receiveBean
     */
    protected void onPostHandler(StatusResp resp,JMChartRoomSendBean receiveBean) {}

    /**
     * 检查是否重复
     * @param receiveBean
     * @return
     */
    protected boolean checkIsRepeatOrReturn(JMChartRoomSendBean receiveBean) {
        boolean isReturn = false;
        if(receiveBean.getProcessStatus() == getStatus()
                && receiveBean.getMessageType() == MessageType.TYPE_SEND
                && mHandledIndexList.contains(receiveBean.getIndexNext())) {
            isReturn = true;
        }

        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            mHandledIndexList.add(receiveBean.getIndexNext());
        }
        return isReturn;
    }

    /**
     * 处理信息前的事件
     * @param receiveBean
     */
    protected void onPreHandle(JMChartRoomSendBean receiveBean) { }

    /**
     * 处理信息
     * @param receiveBean
     * @return 1:成功   7000:参数为null
     */
    public int handlerRoomChart(JMChartRoomSendBean receiveBean) {
        onPreHandle(receiveBean);

        if(receiveBean == null) {
            return HANDLE_NULL;
        }

        if(getStatus() != receiveBean.getProcessStatus()) {
            //不处理其他的消息，只处理自己的消息
            return HANDLE_NOT_MATCH;
        }

        //重置屏蔽消息的index
        if(receiveBean.isRestCurrentIndex()) {
            mHandledIndexList.clear();
        }

        if(checkIsRepeatOrReturn(receiveBean)) {
            return HANDLE_REPEAT;
        }


        StatusResp resp = new StatusResp();
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setMessageType(MessageType.TYPE_SEND);
            mCompleteCount ++;
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE){
            resp.setMessageType(MessageType.TYPE_RESPONSE);
        }

        resp.setSelf(checkIsSelf(receiveBean));
        resp.setHandleType(getHandleType());
        resp.setPublicString(getPublicString());
        boolean last = isLast(mCompleteCount,receiveBean);
        resp.setLast(last);
        if(last) {
            mCompleteCount = 0;
        }

        onPostHandler(resp,receiveBean);

        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            handleSend(resp,receiveBean);
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            handleResponse(resp,receiveBean);
        }

        return HANDLE_SUCCESS;
    }

    /**
     * 创建基础的发送JMChartRoomSendBean
     * @return
     */
    public JMChartRoomSendBean createBaseChartRoomSendBean() {
        JMChartRoomSendBean bean = new JMChartRoomSendBean();
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
        bean.setGender(selfInfo.getGender());
        bean.setCurrentCount(data.getMembers().size());
        bean.setLimitCount(data.getLimitAngel() + data.getLimitMan() + data.getLimitLady());
        bean.setIndexSelf(DataManager.getInstance().getSelfMember().getIndex());
        bean.setRoomId(data.getRoomId());
        bean.setTime(Tools.getCurrentDateTime());
        bean.setUserName(selfInfo.getUser_name());
        bean.setProcessStatus(getStatus());
        return bean;
    }

    /**
     * 检查是否是自己
     * @param bean
     * @return
     */
    protected boolean checkIsSelf(JMChartRoomSendBean bean) {
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        boolean isSelf = false;
        if(getRequestGender().contains(userInfoBean.getGender())
                &&getRequestRoleType().contains(userInfoBean.getRole_type())
                &&checkSelfIndex(bean)) {
            isSelf = true;
        }
        return isSelf;
    }

    public abstract void onStartTime();
    public abstract void onStopTime();
    public abstract void onEnd();
    public abstract void handleSend(StatusResp statusResp, JMChartRoomSendBean sendBean);
    public abstract void handleResponse(StatusResp statusResp, JMChartRoomSendBean sendBean);

    public void setNextStatus(BaseStatus baseStatus) {
        mNextStatus = baseStatus;
    }

    public BaseStatus getNextStatus() {
        return mNextStatus;
    }
}
