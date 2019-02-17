package com.cd.xq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.beans.JMFriendInviteParam;
import com.cd.xq.module.util.beans.jmessage.JMSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2019/2/16.
 */

public class InviteRoomDlgActivity extends Activity {
    @BindView(R.id.dlg_invite_send_text)
    TextView dlgInviteSendText;
    @BindView(R.id.dlg_invite_send_btn_cancel)
    Button dlgInviteSendBtnCancel;
    @BindView(R.id.dlg_invite_send_linearLayout)
    LinearLayout dlgInviteSendLinearLayout;
    @BindView(R.id.dlg_invite_receive_img_head)
    CircleImageView dlgInviteReceiveImgHead;
    @BindView(R.id.dlg_invite_receive_text)
    TextView dlgInviteReceiveText;
    @BindView(R.id.dlg_invite_receive_btn_refuse)
    Button dlgInviteReceiveBtnRefuse;
    @BindView(R.id.dlg_invite_receive_btn_accept)
    Button dlgInviteReceiveBtnAccept;
    @BindView(R.id.dlg_invite_receive_linearLayout)
    LinearLayout dlgInviteReceiveLinearLayout;

    private UserInfoBean mTargetUserInfo;
    private Handler mHandler;
    private int mCurrentTime;
    private final int COUNT_TIME = 30;
    private Runnable mTimeRunnable;

    public static void startWithSend(Context context, UserInfoBean target) {
        startSelf(context,target,1);
    }

    public static void startWithReceive(Context context,UserInfoBean target) {
        startSelf(context,target,2);
    }

    private static void startSelf(Context context,UserInfoBean target,int type) {
        Intent intent = new Intent(context,InviteRoomDlgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putString("target",new Gson().toJson(target));
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlg_invite_room);
        ButterKnife.bind(this);

        JMessageClient.registerEventReceiver(this);

        mHandler = new Handler();
        Bundle bundle = getIntent().getExtras();
        int type = bundle.getInt("type");
        mTargetUserInfo = new Gson().fromJson(bundle.getString("target"),UserInfoBean.class);
        if(type == 1) {
            doSend();
        }else if(type == 2) {
            doReceive();
        }

    }

    private void doSend() {
        dlgInviteSendLinearLayout.setVisibility(View.VISIBLE);
        dlgInviteReceiveLinearLayout.setVisibility(View.GONE);
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                int timeDown = COUNT_TIME - mCurrentTime;
                if(timeDown > 0) {
                    String text = "已发送邀请，等待对方接受中...\n" + String.valueOf(timeDown) + "s";
                    dlgInviteSendText.setText(text);
                }else {
                    finish();
                    Tools.toast(getApplicationContext(),"对方未应答",false);

                    JMSendBean sendBean = new JMSendBean();
                    sendBean.setCode(JMSendBean.JM_SEND_FRIEND_INVITE);
                    sendBean.setTargetUserName(mTargetUserInfo.getUser_name());
                    sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
                    JMFriendInviteParam param = new JMFriendInviteParam();
                    param.setType(JMFriendInviteParam.TYPE_SEND);
                    param.setAction(JMFriendInviteParam.ACTION_CANCEL);
                    param.setFromUser(DataManager.getInstance().getUserInfo());
                    sendBean.setJsonData(new Gson().toJson(param));
                    JMsgSender.sendTextMessage(sendBean);
                }
                mCurrentTime ++;
                mHandler.postDelayed(this,1000);
            }
        };
        mHandler.post(mTimeRunnable);

        JMSendBean sendBean = new JMSendBean();
        sendBean.setCode(JMSendBean.JM_SEND_FRIEND_INVITE);
        sendBean.setTargetUserName(mTargetUserInfo.getUser_name());
        sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
        JMFriendInviteParam param = new JMFriendInviteParam();
        param.setType(JMFriendInviteParam.TYPE_SEND);
        param.setAction(JMFriendInviteParam.ACTION_CREATE);
        param.setFromUser(DataManager.getInstance().getUserInfo());
        sendBean.setJsonData(new Gson().toJson(param));
        JMsgSender.sendTextMessage(sendBean);
    }

    private void doReceive() {
        dlgInviteSendLinearLayout.setVisibility(View.GONE);
        dlgInviteReceiveLinearLayout.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(mTargetUserInfo.getHead_image())
                .into(dlgInviteReceiveImgHead);

        String text = mTargetUserInfo.getNick_name() + "邀请您视频...";
        dlgInviteReceiveText.setText(text);
    }


    @OnClick({R.id.dlg_invite_send_btn_cancel, R.id.dlg_invite_receive_btn_refuse, R.id
            .dlg_invite_receive_btn_accept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dlg_invite_send_btn_cancel:
            {
                finish();
                Tools.toast(getApplicationContext(),"您取消了邀请 ",false);

                JMSendBean sendBean = new JMSendBean();
                sendBean.setCode(JMSendBean.JM_SEND_FRIEND_INVITE);
                sendBean.setTargetUserName(mTargetUserInfo.getUser_name());
                sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
                JMFriendInviteParam param = new JMFriendInviteParam();
                param.setType(JMFriendInviteParam.TYPE_SEND);
                param.setAction(JMFriendInviteParam.ACTION_CANCEL);
                param.setFromUser(DataManager.getInstance().getUserInfo());
                sendBean.setJsonData(new Gson().toJson(param));
                JMsgSender.sendTextMessage(sendBean);
            }
                break;
            case R.id.dlg_invite_receive_btn_refuse:
            {
                finish();

                JMSendBean sendBean = new JMSendBean();
                sendBean.setCode(JMSendBean.JM_SEND_FRIEND_INVITE);
                sendBean.setTargetUserName(mTargetUserInfo.getUser_name());
                sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
                JMFriendInviteParam param = new JMFriendInviteParam();
                param.setType(JMFriendInviteParam.TYPE_RECEIVE);
                param.setAction(JMFriendInviteParam.ACTION_REFUSE);
                param.setFromUser(DataManager.getInstance().getUserInfo());
                sendBean.setJsonData(new Gson().toJson(param));
                JMsgSender.sendTextMessage(sendBean);
            }
                break;
            case R.id.dlg_invite_receive_btn_accept:
            {
                finish();

                JMSendBean sendBean = new JMSendBean();
                sendBean.setCode(JMSendBean.JM_SEND_FRIEND_INVITE);
                sendBean.setTargetUserName(mTargetUserInfo.getUser_name());
                sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
                JMFriendInviteParam param = new JMFriendInviteParam();
                param.setType(JMFriendInviteParam.TYPE_RECEIVE);
                param.setAction(JMFriendInviteParam.ACTION_ACCEPT);
                param.setFromUser(DataManager.getInstance().getUserInfo());
                sendBean.setJsonData(new Gson().toJson(param));
                JMsgSender.sendTextMessage(sendBean);

                //进入两人聊天室
                startDoubleRoomActivity();
            }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);

        if(mTimeRunnable != null) {
            mHandler.removeCallbacks(mTimeRunnable);
        }
    }

    /**
     * 接收普通消息
     * @param event
     */
    public void onEventMainThread(MessageEvent event){
        String message = event.getMessage().getContent().toJson();
        String text = null;
        try {
            JSONObject object = new JSONObject(message);
            text = object.getString("text");
        }catch (Exception e) {
            Log.e("InviteRoomDlgActivity--onEventMainThread--" + e.toString());
            return;
        }

        JMSendBean bean = new Gson().fromJson(text,JMSendBean.class);
        if(bean.getCode() != JMSendBean.JM_SEND_FRIEND_INVITE) return;
        JMFriendInviteParam param = new Gson().fromJson(bean.getJsonData(),JMFriendInviteParam.class);
        if(param.getType() == JMFriendInviteParam.TYPE_RECEIVE) {
            if(param.getAction() == JMFriendInviteParam.ACTION_REFUSE) {
                finish();
                Tools.toast(getApplicationContext(),"对方拒绝视频 ",false);
            }else if(param.getAction() == JMFriendInviteParam.ACTION_ACCEPT) {
                //进入两人聊天室
                finish();
                startDoubleRoomActivity();
            }
        }else if(param.getType() == JMFriendInviteParam.TYPE_SEND) {
            if(param.getAction() == JMFriendInviteParam.ACTION_CANCEL) {
                finish();
                Tools.toast(getApplicationContext(),"对方取消视频 ",false);
            }
        }
    }

    private void startDoubleRoomActivity() {
        String pushAddress = NetWorkMg.getCameraUrl() + "_" + DataManager.getInstance().getUserInfo().getUser_id();
        String playAddress = NetWorkMg.getCameraUrl() + "_" + mTargetUserInfo.getUser_id();

        Intent intent = new Intent("com.cd.xq_chart/doubleRoomActivity");
        Bundle bundle = new Bundle();
        bundle.putString("push",pushAddress);
        bundle.putString("play",playAddress);
        bundle.putString("target",new Gson().toJson(mTargetUserInfo));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
