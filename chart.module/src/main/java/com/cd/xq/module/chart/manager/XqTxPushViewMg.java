package com.cd.xq.module.chart.manager;

import android.app.Activity;
import android.app.Service;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;

import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq_chart.module.R;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.ref.WeakReference;


/**
 * Created by Administrator on 2018/9/15.
 */

public class XqTxPushViewMg extends AbsChartView {
    private final String prefixStr = this.getClass().getName() + "--";

    private TXLivePusher mLivePusher;
    private TXCloudVideoView mVideoView;
    private TXLivePushConfig mLivePushConfig;

    private int mNetBusyCount = 0;

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onResume() {
        if(mVideoView != null) {
            mVideoView.onResume();
        }

        if (mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void onPause() {
        if(mVideoView != null) {
            mVideoView.onPause();
        }

        if(mLivePusher != null) {
            mLivePusher.pausePusher();
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void onDestroy() {
        if(mVideoView != null) {
            mVideoView.onDestroy();
        }

        if(mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopCameraPreview(true);
            mLivePusher.setPushListener(null);
            mLivePusher.stopPusher();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    private void start(boolean isPureAudio) {
        super.start();
        if(mLivePusher != null && mLivePushConfig != null) {
            mLivePushConfig.enablePureAudioPush(isPureAudio);   // true 为启动纯音频推流，而默认值是 false；
            mLivePusher.setConfig(mLivePushConfig);

            mLivePusher.startPusher(mAddress);
        }
    }

    @Override
    public void start() {
        start(false);
    }

    @Override
    public void stop() {
        super.stop();

        if(mLivePusher != null) {
            mLivePusher.stopPusher();
            mLivePusher.stopBGM();
        }

        setVisible(false);
    }

    @Override
    public void init(Activity activity,String address) {
        super.init(activity,address);
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_viewmg_xqtxpush,null);

        mLivePusher = new TXLivePusher(mActivity);
        mLivePushConfig = new TXLivePushConfig();

        mVideoView = (TXCloudVideoView) mRootView.findViewById(R.id.tx_push_videoview);
        mVideoView.setMirror(true);
        mLivePusher.startCameraPreview(mVideoView);
        mLivePusher.setMirror(true);

        mLivePusher.setPushListener(new ITXLivePushListener() {
            @Override
            public void onPushEvent(int event, Bundle param) {
                String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
                String pushEventLog = "receive event: " + event + ", " + msg;
                Log.i("yy", pushEventLog);

                //错误还是要明确的报一下
                if (event < 0) {
                    Tools.toast(mActivity.getApplication(), param.getString(TXLiveConstants.EVT_DESCRIPTION), true);
                    Log.e(param.getString(TXLiveConstants.EVT_DESCRIPTION));
                    if(event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL || event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL){
                        stop();
                    }
                }

                if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {
                    stop();
                    Tools.toast(mActivity.getApplication(), "网络连接失败", true);
                    Log.e(prefixStr + "网络连接失败");
                }else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
                    ++mNetBusyCount;
                    Log.e(prefixStr + "net busy. count=" + mNetBusyCount);
                    Tools.toast(mActivity.getApplication(), param.getString(TXLiveConstants.EVT_DESCRIPTION), true);
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

        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION,
                false,
                false);

        TXPhoneStateListener mPhoneListener = new TXPhoneStateListener(mLivePusher);
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void setAddress(String newAddress) {
        mAddress = newAddress;
        stop();
    }

    public String getAddress() {
        return mAddress;
    }

    public class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXLivePusher> mPusher;
        public TXPhoneStateListener(TXLivePusher pusher) {
            mPusher = new WeakReference<TXLivePusher>(pusher);
        }
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXLivePusher pusher = mPusher.get();
            switch(state){
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (pusher != null) pusher.resumePusher();
                    break;
            }
        }
    }

    /**
     * 是否静音
     * @param isMute
     */
    public void setMute(boolean isMute) {
        mLivePusher.setMute(isMute);
    }
}
