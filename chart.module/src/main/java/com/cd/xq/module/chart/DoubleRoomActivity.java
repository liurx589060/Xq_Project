package com.cd.xq.module.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq_chart.module.R;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by Administrator on 2018/12/20.
 */

public class DoubleRoomActivity extends BaseActivity {
    private final String PRE_MSG_PLAY = "DoubleRoomActivity_play_";
    private final String PRE_MSG_PUSH = "DoubleRoomActivity_push_";
    private final float  CACHE_TIME_FAST = 1.0f;

    private int mNetBusyCount = 0;

    private TXLivePusher mLivePusher;
    private TXLivePlayer mLivePlayer;

    private TXCloudVideoView mPushTxVideoView;
    private TXCloudVideoView mPlayTxVideoView;
    private ImageView mImgExit;

    private String mPushAddress;
    private String mPlayAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_room);

        Bundle bundle = getIntent().getExtras();
        mPlayAddress = bundle.getString("play","");
        mPushAddress = bundle.getString("push","");

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPush();
        stopPlay();
    }

    private void init() {
        mPlayTxVideoView = findViewById(R.id.double_room_tx_play_view);
        mPushTxVideoView = findViewById(R.id.double_room_tx_push_view);
        mImgExit = findViewById(R.id.double_room_img_exit);
        
        mImgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitRoom();
            }
        });

        initPush();
        initPlay();
    }

    private void initPush() {
        mLivePusher = new TXLivePusher(this);
        TXLivePushConfig mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.enableAEC(true);
        mPushTxVideoView.setMirror(true);
        mLivePusher.startCameraPreview(mPushTxVideoView);
        mLivePusher.setMirror(true);

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
                    Tools.toast(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), true);
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
        finish();
    }
}
