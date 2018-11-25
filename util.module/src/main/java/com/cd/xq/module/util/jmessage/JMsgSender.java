package com.cd.xq.module.util.jmessage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cd.xq.module.util.beans.JMNormalSendBean;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
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
    public static void sendMessage(Context context,int code, String str, String exinfo) {
        String userName = null;
        if(JMessageClient.getMyInfo().getUserName().equals("wys30201")) {
            userName = "wys30202";
        }else if (JMessageClient.getMyInfo().getUserName().equals("wys30202")){
            userName = "wys30201";
        }
        if(userName == null) {
            Toast.makeText(context.getApplicationContext(),"userName is null",Toast.LENGTH_SHORT).show();
            return;
        }
        JMNormalSendBean bean = new JMNormalSendBean();
        bean.setMsg(str);
        bean.setCode(code);
        Message message = JMessageClient.createSingleTextMessage(userName,new Gson().toJson(bean));
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
//                if(i == 0) {
//                    Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getApplicationContext(),"发送失败--" + s,Toast.LENGTH_SHORT).show();
//                }
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
                    Log.e("yy","sendRoomMessage failed --" + chartRoomSendBean.getUserName());
                }
            }
        });
        JMessageClient.sendMessage(msg);
    }
}
