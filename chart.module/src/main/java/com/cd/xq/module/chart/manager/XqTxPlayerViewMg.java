package com.cd.xq.module.chart.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq_chart.module.R;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class XqTxPlayerViewMg extends AbsChartView {
    private final String prefixStr = this.getClass().getName() + "--";
    private static final float  CACHE_TIME_FAST = 1.0f;
    private static final float  CACHE_TIME_SMOOTH = 5.0f;

    private TXLivePlayer mLivePlayer;
    private TXCloudVideoView mVideoView;

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onResume() {
        mLivePlayer.resume();
    }

    @Override
    public void onPause() {
        mLivePlayer.pause();
    }

    @Override
    public void onDestroy() {
        stop();
        mVideoView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
    }

    @Override
    public void start() {
        super.start();
        if(mAddress == null || mAddress.isEmpty()) {
            throw new IllegalArgumentException("the mAddress is not valid,can not null or empty");
        }
        if(mLivePlayer != null) {
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int event, Bundle param) {
                    String playEventLog = "receive event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
                    Log.i(Constant.TAG, prefixStr + playEventLog);

                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                        Tools.toast(mActivity.getApplication(),"网络连接失败或者已停止",true);
                        stop();
                    }

                    if (event < 0) {
                        Tools.toast(mActivity.getApplication(),param.getString(TXLiveConstants.EVT_DESCRIPTION),false);
                        Log.e(Constant.TAG,prefixStr + param.getString(TXLiveConstants.EVT_DESCRIPTION));
                    }
                }

                @Override
                public void onNetStatus(Bundle status) {
                    String str = getNetStatusString(status);
                    Log.i(Constant.TAG, "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                            ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                            ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                            ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                            ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                            ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
                }
            });
            mLivePlayer.startPlay(mAddress, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
        }
    }

    //公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-8s %-8s %-8s %-8s\n%-14s %-14s\n%-14s %-14s",
                "CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps",
                "JIT:"+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "GOP:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_GOP)+"s",
                "ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps",
                "QUE:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_V_DEC_CACHE_SIZE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_AV_RECV_INTERVAL)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL)
                        +","+String.format("%.1f", status.getFloat(TXLiveConstants.NET_STATUS_AUDIO_PLAY_SPEED)).toString(),
                "VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps",
                "SVR:"+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP),
                "AUDIO:"+status.getString(TXLiveConstants.NET_STATUS_AUDIO_INFO));
        return str;
    }

    @Override
    public void stop() {
        super.stop();
        if(mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
        }
        setVisible(false);
    }

    @Override
    public void init(Activity activity,String address) {
        super.init(activity,address);

        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_viewmg_xqtxplayer,null);
        mVideoView = mRootView.findViewById(R.id.video_view);
        try {
            mLivePlayer = new TXLivePlayer(activity);
            mLivePlayer.setPlayerView(mVideoView);
            // 设置填充模式
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            // 设置画面渲染方向
            mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

            TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
//            mPlayConfig.setAutoAdjustCacheTime(true);
//            mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
//            mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);

            mPlayConfig.setAutoAdjustCacheTime(true);
            mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
            mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
            mPlayConfig.enableAEC(true);
            mLivePlayer.setConfig(mPlayConfig);
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("yy",e.toString());
            Tools.toast(activity,"启动TXLivePlayer失败", true);
        }
    }

    public void setAddress(String address) {
        mAddress = address;
        stop();
    }

    public String getAddress() {
        return mAddress;
    }
}
