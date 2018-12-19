package com.cd.xq;

/**
 * Created by Administrator on 2018/12/17.
 */

public class AppConstant {
    public static final String BROADCAST_JPUSH_MESSAGE_ACTION = "com.cd.xq.android.intent.JPUSH_MESSAGE";
    public static final String JPUSH_TAG_CHAT = "tag_chat_room";  //创建和删除房间的推送tag
    public static final int JPUSH_TYPE_CHAT_CREATE = 1;   //创建聊天室
    public static final int JPUSH_TYPE_CHAT_DELETE = 2;   //删除聊天室
}
