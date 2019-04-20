package com.cd.xq.module.chart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cd.xq.module.chart.manager.HeadInfoViewMg;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.JMNormalSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.jmessage.JMsgUtil;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq_chart.module.R;
import com.google.gson.Gson;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONObject;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;

/**
 * Created by Administrator on 2018/12/20.
 */

public class DoubleRoomActivity extends BaseActivity {
    private final String PRE_MSG_PLAY = "DoubleRoomActivity_play_";
    private final String PRE_MSG_PUSH = "DoubleRoomActivity_push_";
    private final int EXIT = 1000;
    private final float  CACHE_TIME_FAST = 1.0f;

    private int mNetBusyCount = 0;

    private TXLivePusher mLivePusher;
    private TXLivePlayer mLivePlayer;

    private TXCloudVideoView mPushTxVideoView;
    private TXCloudVideoView mPlayTxVideoView;
    private ImageView mImgExit;

    private String mPushAddress;
    private String mPlayAddress;

    private HeadInfoViewMg mHeadInfoViewMg;
    private View mHeadInfoBgRelayout;

    private ImageView mImgSelfHead;
    private ImageView mImgTargetHead;
    private UserInfoBean mTargetInfoBean;
    private UserInfoBean mSelfBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_room);

        Bundle bundle = getIntent().getExtras();
        mPlayAddress = bundle.getString("play","");
        mPushAddress = bundle.getString("push","");
        try {
            mTargetInfoBean = new Gson().fromJson(bundle.getString("target"),UserInfoBean.class);
        }catch (Exception e) {
            mTargetInfoBean = new UserInfoBean();
        }
        mSelfBean = DataManager.getInstance().getUserInfo();

        Log.i("playAddress-" + mPlayAddress + "\n" +
        "pushAddress-" + mPushAddress);

        init();


        startPush(mPushAddress);
        startPlay(mPlayAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPushTxVideoView.onResume();
        mPlayTxVideoView.onResume();

        mLivePusher.resumePusher();
        mLivePlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPushTxVideoView.onPause();
        mPlayTxVideoView.onPause();

        mLivePusher.pausePusher();
        mLivePlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPushTxVideoView.onDestroy();
        mPlayTxVideoView.onDestroy();

        JMessageClient.unRegisterEventReceiver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPush();
        stopPlay();
    }

    private void init() {
        JMessageClient.registerEventReceiver(this);
        mPushTxVideoView = findViewById(R.id.double_room_tx_play_view);
        mPlayTxVideoView = findViewById(R.id.double_room_tx_push_view);
        mImgSelfHead = findViewById(R.id.double_room_activity_headInfo_self);
        mImgTargetHead = findViewById(R.id.double_room_activity_headInfo_target);
        mImgExit = findViewById(R.id.double_room_img_exit);
        mHeadInfoViewMg = new HeadInfoViewMg(this,findViewById(R.id.double_room_activity_headInfo));
        mHeadInfoBgRelayout = findViewById(R.id.double_room_activity_relayout_headInfo);
        mHeadInfoBgRelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeadInfoBgRelayout.setVisibility(View.GONE);
            }
        });
        
        mImgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitRoom();
            }
        });

        mImgSelfHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeadInfoData(DataManager.getInstance().getUserInfo());
            }
        });

        mImgTargetHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeadInfoData(mTargetInfoBean);
            }
        });

        Glide.with(this)
                .load(DataManager.getInstance().getUserInfo().getHead_image())
                .placeholder(R.drawable.chart_room_default_head)
                .bitmapTransform(new GlideCircleTransform(this))
                .into(mImgSelfHead);

        Glide.with(this)
                .load(mTargetInfoBean.getHead_image())
                .placeholder(R.drawable.chart_room_default_head)
                .bitmapTransform(new GlideCircleTransform(this))
                .into(mImgTargetHead);

        initPush();
        initPlay();

        mHeadInfoBgRelayout.setVisibility(View.GONE);

    }

    private void initPush() {
        mLivePusher = new TXLivePusher(this);
        TXLivePushConfig mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.enableAEC(true);
        mLivePushConfig.setAutoAdjustBitrate(true);
        mPushTxVideoView.setMirror(true);
        mLivePusher.startCameraPreview(mPushTxVideoView);
        mLivePusher.setMirror(true);
        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION,true,true);

        mLivePusher.setPushListener(new ITXLivePushListener() {
            @Override
            public void onPushEvent(int event, Bundle param) {
                String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
                String pushEventLog = "receive event: " + event + ", " + msg;
                Log.i("yy", pushEventLog);

                //错误还是要明确的报一下
                if (event < 0) {
                    Tools.toast(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), true);
                    Log.e(param.getString(TXLiveConstants.EVT_DESCRIPTION));
                    if(event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL || event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL){
                        stopPush();
                    }
                }

                if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {
                    stopPush();
                    Tools.toast(getApplicationContext(), "网络连接失败", true);
                    Log.e(PRE_MSG_PUSH + "网络连接失败");
                }else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
                    ++mNetBusyCount;
                    Log.e(PRE_MSG_PUSH + "net busy. count=" + mNetBusyCount);
                    //Tools.toast(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), true);
                }
            }

            @Override
            public void onNetStatus(Bundle status) {
                Log.i(Constant.TAG, "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                        ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                        ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                        ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                        ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                        ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
            }
        });
    }

    private void startPush(String ipAddress) {
        stopPush();
        if(ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("the mAddress is not valid,can not null or empty");
        }

        mLivePusher.startPusher(ipAddress);
    }

    private void stopPush() {
        mLivePusher.stopPusher();
    }


    private void initPlay() {
        mLivePlayer = new TXLivePlayer(this);
        mLivePlayer.setPlayerView(mPlayTxVideoView);
        // 设置填充模式
        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        // 设置画面渲染方向
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
        mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
        mPlayConfig.enableAEC(true);
        mLivePlayer.setConfig(mPlayConfig);

        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int event, Bundle param) {
                String playEventLog = "receive event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
                Log.i(Constant.TAG, PRE_MSG_PLAY + playEventLog);

                if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                    Tools.toast(getApplicationContext(),"网络连接失败或者已停止",true);
                    stopPlay();
                }

                if (event < 0) {
                    Tools.toast(getApplicationContext(),param.getString(TXLiveConstants.EVT_DESCRIPTION),false);
                    Log.e(Constant.TAG,PRE_MSG_PLAY + param.getString(TXLiveConstants.EVT_DESCRIPTION));
                }
            }

            @Override
            public void onNetStatus(Bundle status) {
                Log.i(Constant.TAG, "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                        ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                        ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                        ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                        ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                        ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
            }
        });
    }

    private void startPlay(String ipAddress) {
        stopPlay();
        if(ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("the mAddress is not valid,can not null or empty");
        }

        mLivePlayer.startPlay(ipAddress, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
    }

    private void stopPlay() {
        mLivePlayer.stopPlay(true);
    }



    private void exitRoom() {
        JMNormalSendBean sendBean = new JMNormalSendBean();
        sendBean.setCode(EXIT);
        sendBean.setTargetUserName(mTargetInfoBean.getUser_name());
        JMsgUtil.sendNomalMessage(sendBean);
        finish();
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
            android.util.Log.e("yy",e.toString());
            return;
        }
        JMNormalSendBean normalSendBean = new Gson().fromJson(text,JMNormalSendBean.class);
        if(normalSendBean.getCode() == EXIT) {
            Tools.toast(this,"对方已离开房间",true);
        }
    }

    private void setHeadInfoData(UserInfoBean bean) {
        mHeadInfoBgRelayout.setVisibility(View.VISIBLE);
        mHeadInfoViewMg.setImgHead(bean.getHead_image());
        mHeadInfoViewMg.setSpecialInfo(bean.getSpecial_info());
        mHeadInfoViewMg.setNickName(bean.getNick_name());
        mHeadInfoViewMg.setSpecailInfoVisible(true);
        mHeadInfoViewMg.setSpecialInfo(bean.getSpecial_info());
        StringBuilder builder = new StringBuilder();
        builder.append("性别： ").append(bean.getGender()).append("        ")
                .append("年龄： ").append(String.valueOf(bean.getAge())).append("\n\n")
                .append("身高： ").append(String.valueOf(bean.getTall())).append("cm").append("\n\n")
                .append("学历： ").append(bean.getScholling()).append("\n\n")
                .append("籍贯： ").append(bean.getNative_place()).append("\n\n")
                .append("职业： ").append(bean.getProfessional()).append("\n\n")
                .append("工作地点： ").append(bean.getJob_address()).append("\n\n");
        mHeadInfoViewMg.setContent(builder.toString());
    }

    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.chart_room_exit)
                .setTitle("退出")
                .setMessage("确认退出?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitRoom();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create()
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
