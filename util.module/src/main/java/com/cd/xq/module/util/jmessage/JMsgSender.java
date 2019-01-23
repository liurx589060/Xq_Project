package com.cd.xq.module.util.jmessage;

import com.cd.xq.module.util.beans.JMNormalSendBean;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.jmessage.JMSendBean;
import com.cd.xq.module.util.tools.Log;
import com.google.gson.Gson;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2018/3/10.
 */

public class JMsgSender {


    /**
     * 发送JMSendBean 的文本消息
     * @param sendBean
     */
    public static void sendTextMessage(JMSendBean sendBean) {
        if(sendBean == null || sendBean.getTargetUserName() == null || sendBean.getData() == null) {
            return;
        }

        Message message = JMessageClient.createSingleTextMessage(sendBean.getTargetUserName(),
                new Gson().toJson(sendBean));
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (0 == i) {
                    Log.e("sendTextMessage success --");
                } else {
                    Log.e("sendTextMessage failed --");
                }
            }
        });
        MessageSendingOptions options = new MessageSendingOptions();
        options.setShowNotification(false);
        JMessageClient.sendMessage(message,options);
    }

    /**
     * 发送普通的消息
     * @param sendBean
     */
    public static void sendNomalMessage(JMNormalSendBean sendBean) {
        Message message = JMessageClient.createSingleTextMessage(sendBean.getTargetUserName(),new Gson().toJson(sendBean));
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (0 == i) {
                    Log.i("yy","sendNormalMessage success --");
                } else {
                    Log.e("yy","sendRoomMessage failed --");
                }
            }
        });
        MessageSendingOptions options = new MessageSendingOptions();
        options.setShowNotification(false);
        JMessageClient.sendMessage(message,options);
    }


    /**
     * 发送聊天室信息
     * @param chartRoomSendBean
     */
    public static void sendRoomMessage(final JMChartRoomSendBean chartRoomSendBean) {
        if(chartRoomSendBean == null) return;
        Conversation conv = JMessageClient.getChatRoomConversation(chartRoomSendBean.getRoomId());
        if (null == conv) {
            conv = Conversation.createChatRoomConversation(chartRoomSendBean.getRoomId());
        }
        final Message msg = conv.createSendTextMessage(new Gson().toJson(chartRoomSendBean));
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (0 == responseCode) {
                    Log.i("yy","sendRoomMessage success --" + chartRoomSendBean.getUserName());
                } else {
                    Log.e("yy","sendRoomMessage failed --" + chartRoomSendBean.getUserName() + "===\n" + responseMessage + "\nroomID="
                            + chartRoomSendBean.getRoomId());
                }
            }
        });
        JMessageClient.sendMessage(msg);
    }
}
