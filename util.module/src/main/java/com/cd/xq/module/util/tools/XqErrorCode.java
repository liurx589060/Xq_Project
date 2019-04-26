package com.cd.xq.module.util.tools;

/**
 * Created by Administrator on 2018/5/16.
 */

public class XqErrorCode {
    public static int SUCCESS = 0;
    //put your code here
    public static int ERROR_NO_DATA = 9000;//没有数据

    public static int ERROR_NO_MATCH = 7000;//未匹配上
    public static int ERROR_EXIT_CHARTROOM = 7001;//退出失败
    public static int ERROR_LACK_PARAMS = 7002;//缺少参数
    public static int ERROR_DELETE_CHARTEOOM = 7003;//删除聊天室失败
    public static int ERROR_CREATE_CHARTEOOM = 7004;//创建聊天室失败
    public static int ERROR_JOIN_CHARTEOOM = 7005;//加入聊天室失败
    public static int ERROR_ALREADY_APPOINT_CHATROOM = 7006;//已经有预约的房间
    public static int ERROR_NOT_FIND_CHATROOM = 7007; //找不到房间
    public static int ERROR_FULL_CHATROOM = 7008; //房间满员
    public static int ERROR_ALREADY_START_CHATROOM = 7009; //房间已开始
    public static int ERROR_ROLETYPE_NOT_MATCH = 7010; //角色身份不对
    public static int ERROR_ALREADY_JOIN_CHATROOM = 7011;//已经加入过房间

    public static int ERROR_USER_REGIST = 7100;//注册失败
    public static int ERROR_USER_REGIST_EXIST = 7101;//已经存在账户
    public static int ERROR_USER_NOT_EXIST = 7102;//账号不存在
    public static int ERROR_USER_UPLOAD = 7103;//上传文件出错
    public static int ERROR_USER_PASSWORD_WRONG = 7104;//密码错误
    public static int ERROR_USER_PASSWORD_CHANGE = 7105;//修改密码出错
    public static int ERROR_PARAMS_SAME = 7106;//参数相同
    public static int ERROR_LACK_STOCK = 7107;//库存不够
}
