package com.cd.xq.module.chart.manager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.chart.DoubleRoomActivity;
import com.cd.xq.module.chart.status.statusBeans.StatusAngelChartBean;
import com.cd.xq.module.chart.status.statusBeans.StatusChartFinalBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpChangeLiveTypeBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpDoingDisturbBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpExitBean;
import com.cd.xq.module.chart.status.statusBeans.StatusHelpQuestDisturbBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyChartFirstBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyChartSecondBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyFinalSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyFirstQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadyFirstSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadySecondQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusLadySecondSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManFinalSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManFirstQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManFirstSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManIntroBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManPerformanceBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManSecondQuestionBean;
import com.cd.xq.module.chart.status.statusBeans.StatusManSecondSelectBean;
import com.cd.xq.module.chart.status.statusBeans.StatusMatchBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerEnterBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerExitBean;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.JMNormalSendBean;
import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.status.IHandleListener;
import com.cd.xq.module.util.status.StatusResp;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq_chart.module.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hc.lib.msc.HCMscParams;
import com.hc.lib.msc.ISpeechListener;
import com.hc.lib.msc.MscDefaultSpeech;
import com.iflytek.cloud.SpeechError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.cd.xq.module.chart.manager.XqStatusChartUIViewMg.EnumMemberStatus.STATUS_CHART;
import static com.cd.xq.module.chart.manager.XqStatusChartUIViewMg.EnumMemberStatus.STATUS_NORMAL;
import static com.cd.xq.module.chart.manager.XqStatusChartUIViewMg.EnumMemberStatus.STATUS_SELECT;
import static com.cd.xq.module.util.status.BaseStatus.HandleType.HANDLE_SELECT_MAN_FINAL;

/**
 * Created by Administrator on 2018/5/26.
 */

public class XqStatusChartUIViewMg extends AbsChartView implements IHandleListener {
    public FrameLayout mParentFrameLayout;
    public enum EnumMemberStatus {
        STATUS_NORMAL, //正常
        STATUS_SELECT, //选择
        STATUS_CHART  //发言
    }

    private XqTxPushViewMg mXqCameraViewMg;
    private XqTxPlayerViewMg mXqPlayerViewMg;
    private ArrayList<AbsChartView> viewMgList = new ArrayList<>();

    private View mRootView;
    private RequestApi mApi;
    private Map<Integer,BaseStatus> mOrderStatusMap = null;
    private Map<Integer,BaseStatus> mHelpStatusMap = null;

    private ChartRoomActivity mXqActivity;
    private Map<Integer,UserInfoBean> mAngelMembersMap;
    private Map<Integer,UserInfoBean> mManMembersMap;
    private Map<Integer,UserInfoBean> mLadyMembersMap;
    private List<JMChartRoomSendBean> mSystemEventList;

    private RecyclerView mRecyclerMembers;
    private RecyclerView mRecyclerSystem;
    private ImageView mBtnExit;
    //private ImageView mBtnGift;
    private ImageView mBtnEnd;
    private TextView mTextTip;
    private TextView mTextCountDown;
    private ImageView mBtnDisturb;
    private RadioGroup mRadioGroupLiveType;

    private ViewInstance mAngelViewInstance;
    private ViewInstance mManViewInstance;
    private MemberRecyclerdapter mMemberAdapter;
    private SystemRecyclerdapter mSystemAdapter;
    private LiveTypeRadioChangeListener mCheckChangedListener;

    private Handler mHandler;
    private Runnable mTimeRunnable;
    private int timeCount = 0;

    private AlertDialog mLadySelectDialog;
    private ArrayList<String> mLadySelectedResultList = new ArrayList<>();
    private ArrayList<String> mTempLadySelectedTrueList = new ArrayList<>();
    private boolean mLadySelecteResult = true;
    private boolean mIsDistub = false;
    private int mDistubIndex = -1;
    private int mManSelectedIndex = -1;
    private StatusResp mStartStatusTimeStatusResp;
    private JMChartRoomSendBean mStartStatusRoomSendBean;
    private BaseStatus mStartStatusBasebean;

    private StatusResp mHelpStatusTimeStatusResp;
    private JMChartRoomSendBean mHelpStatusRoomSendBean;
    private BaseStatus mHelpStatusBasebean;

    private int mCurrentQuestDisturbCount = 0;
    private final int DISTURB_COUNT = 3; //一轮可插话的次数
    private int mPreManSelectedIndex = -1;
    private HeadInfoViewMg mHeadInfoViewMg;
    private View mHeadInfoBgRelayout;
    private boolean mIsOnLookerRecCurrentStatus = false;  //是否接受过围观者当前
    private boolean mIsRoomMatchSuccess = false;  //是否匹配成功
    private boolean mIsSelefMatchSuccess = false;  //是否自己匹配成功
    private boolean mIsGoToDouble = false;  //是否接受过进入双人聊天室
    private final int STATUS_PROGRESS = 1000;//流程的status
    private final int STATUS_HELP = 1001;//辅助help的status
    private int mCurrentStatusType = -1;

    private ISpeechListener mSpeechListener = new ISpeechListener() {
        @Override
        public void onInit(boolean b) {
            if(!b) {
                Tools.toast(mXqActivity.getApplicationContext(),"语音播报初始化失败",true);
            }
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onProgress(int i, int i1, int i2) {

        }

        @Override
        public void onComplete(SpeechError speechError) {

        }
    };


    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onResume() {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onPause();
            }
        }
    }

    @Override
    public void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        resetLiveStatus();
        stopTiming();
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onDestroy();
            }
        }
        //重置roomId
        DataManager.getInstance().getChartData().setRoomId(0);
        MscDefaultSpeech.getInstance().destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onConfigurationChanged(newConfig);
            }
        }
    }

    public void init() {
        MscDefaultSpeech.getInstance().getMscParams().setEngineType(HCMscParams.CLOUND);
        MscDefaultSpeech.getInstance().setmSpeechListener(mSpeechListener);
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mAngelMembersMap = new HashMap<>();
        mManMembersMap = new HashMap<>();
        mLadyMembersMap = new HashMap<>();
        mSystemEventList = new ArrayList<>();
        mStartStatusBasebean = new StatusMatchBean();
        mStartStatusRoomSendBean = mStartStatusBasebean.createBaseChartRoomSendBean();
        mStartStatusTimeStatusResp = new StatusResp();
        mCheckChangedListener = new LiveTypeRadioChangeListener();

        mHandler = new Handler();

        mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_room,null);
        mRecyclerMembers = mRootView.findViewById(R.id.chart_room_recyclerView_member);
        mRecyclerSystem = mRootView.findViewById(R.id.chart_room_recyclerView_system);
        mBtnExit = mRootView.findViewById(R.id.chart_room_activity_img_exit);
        //mBtnGift = mRootView.findViewById(R.id.btn_chart_gift);
        mBtnEnd = mRootView.findViewById(R.id.chart_room_activity_img_end);
        mTextCountDown = mRootView.findViewById(R.id.chart_room_activity_text_time);
        mTextTip = mRootView.findViewById(R.id.chart_room_activity_text_describe);
        mBtnDisturb = mRootView.findViewById(R.id.chart_room_activity_img_disturb);
        mRadioGroupLiveType = mRootView.findViewById(R.id.chart_room_activity_radioGroup_liveType);
        mTextTip.setVisibility(View.INVISIBLE);
        mTextCountDown.setVisibility(View.INVISIBLE);

        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

//        mBtnGift.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mXqCameraViewMg.setVisible(true);
//                mXqCameraViewMg.start();
//            }
//        });

        mBtnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentStatusType == STATUS_PROGRESS) {
                    onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
                }else if(mCurrentStatusType == STATUS_HELP) {
                    onOperateEnd(mHelpStatusBasebean,mHelpStatusRoomSendBean,mHelpStatusTimeStatusResp);
                }

            }
        });

        mBtnDisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                if(!userInfoBean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) return;
                final StatusHelpQuestDisturbBean questDisturbBean = (StatusHelpQuestDisturbBean) mHelpStatusMap
                        .get(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_QUEST_DISTURB);
                if(mCurrentQuestDisturbCount >= DISTURB_COUNT) {
                    Tools.toast(mXqActivity,"您已经插话次数已超，不能插话了",true);
                    return;
                }

                if(mIsDistub) {
                    Tools.toast(mXqActivity,"本次已经申请插话了",true);
                    return;
                }

                //发送插话请求
                JMChartRoomSendBean sendBean = questDisturbBean.getChartSendBeanWillSend(null
                        , BaseStatus.MessageType.TYPE_SEND);
                sendBean.setIndexNext(DataManager.getInstance().getSelfMember().getIndex());
                sendRoomMessage(sendBean);
                mCurrentQuestDisturbCount ++;
                Tools.toast(mXqActivity,"您要求插话",false);
            }
        });

        mRadioGroupLiveType.setOnCheckedChangeListener(mCheckChangedListener);
        mHeadInfoViewMg = new HeadInfoViewMg(mXqActivity,mRootView.findViewById(R.id.chart_room_activity_headInfo));
        mHeadInfoBgRelayout = mRootView.findViewById(R.id.chart_room_activity_relayout_headInfo);
        mHeadInfoBgRelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeadInfoBgRelayout.setVisibility(View.GONE);
            }
        });

        initAndSetContentView();
        initAngelManViewInstance();
        initMemberRecyclerView();
        initSystemRecyclerView();
        initOrderStatus();

        JMessageClient.registerEventReceiver(this);
        upDataMembers();

        mBtnDisturb.setVisibility(View.GONE);
        mBtnEnd.setVisibility(View.GONE);
        mRadioGroupLiveType.setVisibility(View.GONE);
        mHeadInfoBgRelayout.setVisibility(View.GONE);

        mTextTip.setText(mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_MATCHING).getPublicString());
    }

    private void initAngelManViewInstance() {
        MemberViewHolder viewHolder = new MemberViewHolder(mRootView.findViewById(R.id.chart_room_activity_member_item_0));
        mAngelViewInstance = viewHolder.viewHolderLeft;
        mManViewInstance = viewHolder.viewHolderRight;
    }

    private void initMemberRecyclerView() {
        mMemberAdapter = new MemberRecyclerdapter();
        mRecyclerMembers.setLayoutManager(new LinearLayoutManager(mXqActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerMembers.setAdapter(mMemberAdapter);
        mRecyclerMembers.addItemDecoration(new MemberSpaceDecoration());
    }

    private class MemberSpaceDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = (int)mXqActivity.getResources().getDimension(R.dimen.dp_8);
        }
    }

    private void upDataMembers() {
        mAngelMembersMap.clear();
        mManMembersMap.clear();
        mLadyMembersMap.clear();
        List<Member> members = DataManager.getInstance().getChartData().getMembers();
        for (int i = 0 ; i < members.size() ; i++) {
            Member member = members.get(i);
            int index = member.getIndex();
            UserInfoBean bean = member.getUserInfo();
            if(bean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                mAngelMembersMap.put(index,bean);
            }else if(bean.getRole_type().equals(Constant.ROLETYPE_GUEST)){
                if(bean.getGender().equals(Constant.GENDER_MAN)) {
                    mManMembersMap.put(index,bean);
                }else {
                    mLadyMembersMap.put(index,bean);
                }
            }
        }

        UserInfoBean angelBean = mAngelMembersMap.get(0);
        mAngelViewInstance.textIndex.setVisibility(View.GONE);
        mAngelViewInstance.imageIndex.setVisibility(View.VISIBLE);
        if(angelBean == null) {
            angelBean = new UserInfoBean();
        }
        final UserInfoBean angelBeanResult = angelBean;
        if(angelBeanResult != null) {
            String text = "爱";
            if(angelBeanResult.getNick_name().equals(DataManager.getInstance().getUserInfo().getNick_name())) {
                mAngelViewInstance.textIndex.setTextColor(Color.RED);
            }
            mAngelViewInstance.textIndex.setText(text);
            if(angelBeanResult.getGender().equals(Constant.GENDER_LADY)) {
                mAngelViewInstance.imageGender.setImageResource(R.drawable.chart_room_gender_lady);
            }else {
                mAngelViewInstance.imageGender.setImageResource(R.drawable.chart_room_gender_man);
            }
            mAngelViewInstance.imageGender.setVisibility(View.VISIBLE);
            Glide.with(mXqActivity)
                    .load(angelBeanResult.getHead_image())
                    .placeholder(R.drawable.chart_room_default_head)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(mAngelViewInstance.imageHead);
            mAngelViewInstance.imageHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHeadInfoData(angelBeanResult);
                }
            });
        }

        UserInfoBean manBean = mManMembersMap.get(0);
        mManViewInstance.textIndex.setTextColor(Color.parseColor("#0000ff"));
        mManViewInstance.textIndex.setText("男");
        if(manBean == null) {
            manBean = new UserInfoBean();
        }
        final UserInfoBean manBeanResult = manBean;
        if(manBeanResult != null) {
            String text = "男";
            if(manBeanResult.getNick_name().equals(DataManager.getInstance().getUserInfo().getNick_name())) {
                mManViewInstance.textIndex.setTextColor(Color.RED);
            }
            mManViewInstance.textIndex.setText(text);
            if(manBeanResult.getGender().equals(Constant.GENDER_LADY)) {
                mManViewInstance.imageGender.setImageResource(R.drawable.chart_room_gender_lady);
            }else {
                mManViewInstance.imageGender.setImageResource(R.drawable.chart_room_gender_man);
            }
            mManViewInstance.imageGender.setVisibility(View.VISIBLE);
            Glide.with(mXqActivity)
                    .load(manBeanResult.getHead_image())
                    .placeholder(R.drawable.chart_room_default_head)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(mManViewInstance.imageHead);
            mManViewInstance.imageHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHeadInfoData(manBeanResult);
                }
            });
        }

        mMemberAdapter.notifyDataSetChanged();
    }

    private int getLadyIndex(int ladyIndex) {
        return mAngelMembersMap.size() + mManMembersMap.size() + ladyIndex;
    }

    @Override
    public void setVisible(boolean isVisible) {
        if(mRootView != null) {
            mRootView.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
        }
    }

    private void initSystemRecyclerView() {
        mSystemAdapter = new SystemRecyclerdapter();
        mRecyclerSystem.setLayoutManager(new LinearLayoutManager(mXqActivity));
        mRecyclerSystem.setAdapter(mSystemAdapter);
    }

    private void initOrderStatus() {
        mOrderStatusMap = new HashMap<>();
        mHelpStatusMap = new HashMap<>();

        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MATCHING,new StatusMatchBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN,new StatusManIntroBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST,new StatusLadyFirstSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_INTRO_LADY,new StatusLadyChartFirstBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST,new StatusManFirstSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE,new StatusManPerformanceBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND,new StatusLadySecondSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND,new StatusLadyChartSecondBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT,new StatusAngelChartBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND,new StatusManSecondSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL,new StatusLadyFinalSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST,new StatusManFirstQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST,new StatusLadyFirstQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_SECOND,new StatusManSecondQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND,new StatusLadySecondQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL,new StatusManFinalSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL,new StatusChartFinalBean());

        //设置流程序列
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry en = (Map.Entry) entry.next();
            int key = (int) en.getKey();
            ((BaseStatus)en.getValue()).setmOrder(key);
            ((BaseStatus)en.getValue()).setHandleListener(this);
        }

        //添加辅助状态机
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_CHANGR_LIVETYPE,new StatusHelpChangeLiveTypeBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING,new StatusHelpDoingDisturbBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_QUEST_DISTURB,new StatusHelpQuestDisturbBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM,new StatusHelpExitBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_ONLOOKER_ENTER,new StatusOnLookerEnterBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_ONLOOKER_EXIT,new StatusOnLookerExitBean());

        //设置流程序列
        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry en = (Map.Entry) helpEntry.next();
            int key = (int) en.getKey();
            ((BaseStatus)en.getValue()).setmOrder(key);
            ((BaseStatus)en.getValue()).setHandleListener(this);
        }
    }

    public XqStatusChartUIViewMg(ChartRoomActivity xqActivity,FrameLayout frameLayout) {
        this.mXqActivity = xqActivity;
        mParentFrameLayout = frameLayout;
    }

    /**
     * 初始化
     */
    private void initAndSetContentView() {
        //摄像头推送
        mXqCameraViewMg = new XqTxPushViewMg();
        String pushAddress = new String(Base64.decode(DataManager.getInstance().getChartData().getPushAddress().getBytes(),Base64.DEFAULT));
        mXqCameraViewMg.init(mXqActivity,pushAddress);
        Log.i("yy","pushAddress=" + pushAddress);
        mXqCameraViewMg.setVisible(false);

        //摄像头播放
        mXqPlayerViewMg = new XqTxPlayerViewMg();
        String playAddress = new String(Base64.decode(DataManager.getInstance().getChartData().getPlayAddress().getBytes(),Base64.DEFAULT));
        mXqPlayerViewMg.init(mXqActivity,playAddress);
        Log.i("yy","playAddress=" + playAddress);
        mXqPlayerViewMg.setVisible(false);

        viewMgList.add(mXqCameraViewMg);
        viewMgList.add(mXqPlayerViewMg);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mParentFrameLayout.addView(mXqCameraViewMg.getView(),params);
        mParentFrameLayout.addView(mXqPlayerViewMg.getView(),params);
        mParentFrameLayout.addView(mRootView,params);
    }

    private void updateSystemEvent() {
        mSystemAdapter.notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    private void getChartRoomMembersList(long roomId) {
        mApi.getChartRoomMemeberList(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(mXqActivity,"jmChartResp is null",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(mXqActivity,jmChartResp.getMsg(),true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        upDataMembers();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(mXqActivity,throwable.toString(),true);
                    }
                });
    }

    private void exit() {
        if(DataManager.getInstance().getUserInfo().getUser_name().equals(DataManager.getInstance().getChartData().getCreater())) {
            deleteChartRoom();
        }else {
            exitChartRoom();
        }
    }

    private void deleteChartRoom() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("roomId",DataManager.getInstance().getChartData().getRoomId());
        params.put("status",mIsRoomMatchSuccess?1:0);
        mApi.deleteChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(mXqActivity,"delete room failed",true);
                            mXqActivity.finish();
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(mXqActivity,jmChartResp.getMsg(),true);
                            mXqActivity.finish();
                            return;
                        }

                        norifyRoomExit(JMNormalSendBean.NORMAL_EXIT);
                        mXqActivity.finish();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void exitChartRoom() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("roomId",DataManager.getInstance().getChartData().getRoomId());
        params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("roomRoleType",DataManager.getInstance().getSelfMember().getRoomRoleType());
        params.put("status",mIsSelefMatchSuccess?1:0);
        mApi.exitChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(mXqActivity,"exit room failed",true);
                            mXqActivity.finish();
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(mXqActivity,jmChartResp.getMsg(),true);
                            mXqActivity.finish();
                            return;
                        }

                        int progressStatus = 0;
                        if(DataManager.getInstance().getSelfMember().getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                            progressStatus = JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM;
                        }else if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_ONLOOKER) {
                            progressStatus = JMChartRoomSendBean.CHART_ONLOOKER_EXIT;
                        }
                        ArrayList<Member> list = new ArrayList<>();
                        list.addAll(DataManager.getInstance().getChartData().getMembers());
                        list.addAll(DataManager.getInstance().getChartData().getOnLookers());
                        for (Member member:list) {
                            if(!member.getUserInfo().getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                                JMNormalSendBean normalSendBean = new JMNormalSendBean();
                                normalSendBean.setCode(progressStatus);
                                normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                                normalSendBean.setTime(Tools.getCurrentDateTime());
                                normalSendBean.setRoomId(DataManager.getInstance().getChartData().getRoomId());
                                normalSendBean.setMsg(DataManager.getInstance().getUserInfo().getNick_name() + "--离开房间");
                                JMsgSender.sendNomalMessage(normalSendBean);
                            }
                        }

                        mXqActivity.finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(mXqActivity,throwable.toString(),true);
                    }
                });
    }

    private void norifyRoomExit(int code) {
        ArrayList<Member> list = new ArrayList<>();
        list.addAll(DataManager.getInstance().getChartData().getMembers());
        list.addAll(DataManager.getInstance().getChartData().getOnLookers());
        for (Member member:list) {
            if(!member.getUserInfo().getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                JMNormalSendBean normalSendBean = new JMNormalSendBean();
                normalSendBean.setCode(code);
                normalSendBean.setRoomId(DataManager.getInstance().getChartData().getRoomId());
                normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                JMsgSender.sendNomalMessage(normalSendBean);
            }
        }
    }

    /**
     * 信息处理回调
     * @param statusResp
     * @param sendBean
     */
    @Override
    public void onHandleResp(BaseStatus statusInstance,StatusResp statusResp, JMChartRoomSendBean sendBean) {
        if(sendBean.isUpdateMembers()) {
            getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
        }

        handleResp(statusInstance,statusResp,sendBean);
    }

    private void handleResp(BaseStatus statusInstance,StatusResp statusResp, JMChartRoomSendBean sendBean) {
        switch (statusResp.getMessageType()) {
            case TYPE_SEND:
                addSystemEventAndRefresh(sendBean);
                boolean isTipUpdate = true;
                int nType = -1;  //-1:不设置  1：流程   2：辅助help

                if(statusResp.isResetLive()) {
                    //是否停止直播
                    resetLiveStatus();
                }
                if(statusResp.isStopTiming()) {
                    //是否停止倒计时
                    stopTiming();
                }
                if(sendBean.getProcessStatus() == mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN).getStatus()) {
                    //自我介绍开始则因此离开按键
//                    mBtnExit.setVisibility(View.GONE);
                    if(!DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                        mBtnExit.setVisibility(View.GONE);
                    }
                }

                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT) {
                    //重置插话标识
                    mIsDistub = false;
                }

                //语音播放
                String content = sendBean.getMsg();
                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                    content += "流程已结束，可自行离开房间";
                }
                if(sendBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_MATCHING
                        && sendBean.getProcessStatus() != JMChartRoomSendBean.CHART_ONLOOKER_ENTER
                        && sendBean.getProcessStatus() != JMChartRoomSendBean.CHART_ONLOOKER_EXIT) {
                    MscDefaultSpeech.getInstance().startSpeaking(mXqActivity,content);
                }else if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_ONLOOKER_ENTER
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_ONLOOKER_EXIT){
                    isTipUpdate = false;
                }

                switch (statusResp.getHandleType()) {
                    case HANDLE_MATCH:
                        if(statusResp.isLast()) {
                            isTipUpdate = false;
                            if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                                BaseStatus baseStatus = mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
                                JMChartRoomSendBean bean = baseStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_SEND);
                                bean.setIndexNext(baseStatus.getStartIndex());
                                sendRoomMessage(bean);
                            }
                        }
                        nType = 1;
                        break;
                    case HANDLE_TIME:
                        startTiming(statusInstance,sendBean,statusResp);
                        nType = 1;
                        break;
                    case HANDLE_SELECT_LADY_FIRST:
                    case HANDLE_SELECT_LADY_SECOND:
                    case HANDLE_SELECT_LADY_FINAL:
                        if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                            operate_SelectLady(statusInstance,sendBean,statusResp);
                        }
                        nType = 1;
                        break;
                    case HANDLE_SELECT_MAN_FIRST:
                    case HANDLE_SELECT_MAN_SECOND:
                    case HANDLE_SELECT_MAN_FINAL:
                        if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                            operate_SelectMan(statusInstance,sendBean,statusResp);
                        }
                        nType = 1;
                        break;
                    case HANDLE_FINISH:
                        sendDoubleRoom();
                        Tools.toast(mXqActivity,sendBean.getMsg(),true);
                        //显示离开按钮
                        mBtnExit.setVisibility(View.VISIBLE);
                        nType = 1;
                        break;
                    case HANDLE_HELP_QUEST_DISTURB:
                        isTipUpdate = false;

                        mIsDistub = true;
                        mDistubIndex = sendBean.getIndexSelf();
                        break;
                    case HANDLE_HELP_DOING_DISTURB:
                        nType = 2;

                        startTiming(statusInstance,sendBean,statusResp);
                        break;
                    case HANDLE_HELP_EXIT:
                        isTipUpdate = false;

                        //getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
                        break;
                    case HANDLE_HELP_CHANGE_LIVETYPE:
                        isTipUpdate = false;

                        operate_LiveType(statusInstance,sendBean,statusResp);
                        break;
                    default:
                        if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_ONLOOKER_EXIT
                                || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_ONLOOKER_ENTER) {
                            nType = -1;
                            if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_ONLOOKER_ENTER
                                    && DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                                sendToOnLookerCurrentStatus(sendBean.getUserName());
                            }
                        }
                        break;
                }

                if(isTipUpdate) {
                    if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                        mTextTip.setText(sendBean.getMsg());
                    }else {
                        mTextTip.setText(statusInstance.getPublicString());
                    }
                    mTextTip.setVisibility(View.VISIBLE);
                    if(mStartStatusRoomSendBean.getProcessStatus() != sendBean.getProcessStatus()) {
                        Tools.toast(mXqActivity,statusInstance.getPublicString(),false);
                    }
                }

                if(nType == 1) {
                    mStartStatusTimeStatusResp = statusResp;
                    mStartStatusRoomSendBean = sendBean;
                    mStartStatusBasebean = statusInstance;
                    mCurrentStatusType = STATUS_PROGRESS;
                }else if(nType == 2) {
                    mHelpStatusBasebean = statusInstance;
                    mHelpStatusRoomSendBean = sendBean;
                    mHelpStatusTimeStatusResp = statusResp;
                    mCurrentStatusType = STATUS_HELP;
                }

                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
                    if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                        //显示插话按钮
                        mBtnDisturb.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case TYPE_RESPONSE:
                addSystemEventAndRefresh(sendBean);
                switch (statusResp.getHandleType()) {
                    case HANDLE_SELECT_LADY_FIRST:
                    case HANDLE_SELECT_LADY_SECOND:
                    case HANDLE_SELECT_LADY_FINAL:
                    case HANDLE_SELECT_MAN_FIRST:
                    case HANDLE_SELECT_MAN_SECOND:
                    case HANDLE_SELECT_MAN_FINAL:
                        if(statusResp.isLast()) {
                            if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_FIRST
                                    || statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_SECOND) {
                                mCurrentQuestDisturbCount = 0;
                            }

                            if(statusResp.getHandleType() == HANDLE_SELECT_MAN_FINAL) {
                                //流程结束
                                operate_Final(statusInstance,sendBean,statusResp);
                            }else {
                                //必须是同一的阶段的回复
                                if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS
                                        && sendBean.getProcessStatus() == mStartStatusRoomSendBean.getProcessStatus()) {
                                    BaseStatus nextStatus = mOrderStatusMap.get(statusInstance.getmOrder() + 1);
                                    if(nextStatus == null) return;
                                    JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(sendBean,BaseStatus.MessageType.TYPE_SEND);
                                    bean.setIndexNext(nextStatus.getStartIndex());
                                    sendRoomMessage(bean);
                                }
                            }
                        }

                        if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_FINAL
                                || statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_FIRST
                                || statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_SECOND) {
                            if(sendBean.isLadySelected()) {
                                mTempLadySelectedTrueList.add(String.valueOf(sendBean.getIndexSelf()));
                            }

                            if(statusResp.isLast()) {
                                mLadySelectedResultList.clear();
                                mLadySelectedResultList.addAll(mTempLadySelectedTrueList);
                                mTempLadySelectedTrueList.clear();
                            }
                        }

//                        if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_FINAL) {
//                            //女生最后一次选择
//                            if(sendBean.isLadySelected()) {
//                                mLadySelectedResultList.add(String.valueOf(sendBean.getIndexSelf()));
//                            }
//                        }
                        break;
                    case HANDLE_HELP_DOING_DISTURB:
                        mIsDistub = false;
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void sendDoubleRoom() {
        if(mIsRoomMatchSuccess && mIsSelefMatchSuccess) {
            String pushAddress;
            String playAddress;
            UserInfoBean targetBean;
            pushAddress = NetWorkMg.getCameraUrl() + "_" + DataManager.getInstance().getUserInfo().getUser_id();
            if(DataManager.getInstance().getUserInfo().getGender().equals(Constant.GENDER_MAN)) {
                String finalSelectLady = String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex());
                targetBean = mLadyMembersMap.get(Integer.parseInt(finalSelectLady));
            }else {
                targetBean = mManMembersMap.get(0);
            }
            playAddress = NetWorkMg.getCameraUrl() + "_" + targetBean.getUser_id();

            JMNormalSendBean sendBean1 = new JMNormalSendBean();
            sendBean1.setCode(JMChartRoomSendBean.CHART_GOTO_DOUBLE_ROOM);
            String extraStr = "";
            try {
                JSONObject object = new JSONObject();
                object.put("push",pushAddress);
                object.put("play",playAddress);
                object.put("target",new Gson().toJson(DataManager.getInstance().getUserInfo()));
                extraStr = object.toString();
            }catch (Exception e) {

            }
            sendBean1.setRoomId(DataManager.getInstance().getChartData().getRoomId());
            sendBean1.setTargetUserName(targetBean.getUser_name());
            sendBean1.setExtra(extraStr);
            JMsgSender.sendNomalMessage(sendBean1);
        }
    }

    private class MemberRecyclerdapter extends RecyclerView.Adapter<MemberViewHolder> {
        private EnumMemberStatus mMemberStatus = STATUS_NORMAL;
        private int mCurrentIndex = -1;

        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MemberViewHolder viewHolder = new MemberViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.chart_room_member_item,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            setData(holder.viewHolderLeft,position,0);
            //setData(holder.viewHolderRight,position,DataManager.getInstance().getChartData().getLimitLady()/2);
            setData(holder.viewHolderRight,position,getItemCount());
        }

        public void setData(final ViewInstance viewInstance, int position, int offset) {
            final int index = offset + position;
            final UserInfoBean bean = mLadyMembersMap.get(index);
            viewInstance.index = index;
            if(bean == null) {
                viewInstance.textIndex.setText(String.valueOf(index+1));
                Glide.with(mXqActivity)
                        .load(R.drawable.chart_room_default_head)
                        .bitmapTransform(new GlideCircleTransform(mXqActivity))
                        .into(viewInstance.imageHead);
                return;
            }

            if(bean.getNick_name().equals(DataManager.getInstance().getUserInfo().getNick_name())) {
                viewInstance.textIndex.setTextColor(Color.RED);
            }
            viewInstance.textIndex.setText(String.valueOf(index + 1));
            if(bean.getGender().equals(Constant.GENDER_LADY)) {
                viewInstance.imageGender.setImageResource(R.drawable.chart_room_gender_lady);
            }else {
                viewInstance.imageGender.setImageResource(R.drawable.chart_room_gender_man);
            }
            viewInstance.imageGender.setVisibility(View.VISIBLE);
            Glide.with(mXqActivity)
                    .load(bean.getHead_image())
                    .placeholder(R.drawable.chart_room_default_head)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(viewInstance.imageHead);
            viewInstance.imageHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setHeadInfoData(bean);
                }
            });

            final ArrayList<String> manSelectedResultList = new ArrayList<>();
            if(mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL
                    || mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                manSelectedResultList.add(String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex()));
            }else {
                manSelectedResultList.add(String.valueOf(((StatusManFirstSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).getSelectLadyIndex()));
                manSelectedResultList.add(String.valueOf(((StatusManSecondSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).getSelectLadyIndex()));
                manSelectedResultList.add(String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex()));
            }

            UserInfoBean selfUserBean = DataManager.getInstance().getUserInfo();
            if((selfUserBean.getRole_type().equals(Constant.ROLETYPE_GUEST) && selfUserBean.getGender().equals(Constant.GENDER_MAN))
                    || DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_ONLOOKER) {
                //是男嘉宾
                if(manSelectedResultList.contains(String.valueOf(index))) {
                    //选中的女生
                    viewInstance.setLightLabelStatus(true);
                }else {
                    viewInstance.setLightLabelStatus(false);
                }
            }

            if(mStartStatusRoomSendBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                if(selfUserBean.getRole_type().equals(Constant.ROLETYPE_GUEST) && selfUserBean.getGender().equals(Constant.GENDER_LADY)) {
                    if(mLadySelecteResult && index == DataManager.getInstance().getSelfMember().getIndex()) {
                        viewInstance.setLightLabelStatus(true);
                    }else {
                        viewInstance.setLightLabelStatus(false);
                    }
                }
            }else {
                if(mIsRoomMatchSuccess && manSelectedResultList.contains(String.valueOf(index))) {
                    //选中的女生
                    viewInstance.setLightLabelStatus(true);
                }else {
                    viewInstance.setLightLabelStatus(false);
                }
            }

            if(mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL) {
                viewInstance.setLightLabelStatus(false);
            }

            switch (mMemberStatus) {
                case STATUS_SELECT:
                    if(viewInstance.imageLabelLight.getVisibility() != View.VISIBLE) {
                        viewInstance.setSelectedStatus();
                    }
                    break;
                case STATUS_NORMAL:
                    viewInstance.setNormalStatus();
                    break;
                case STATUS_CHART:
                    if(mCurrentIndex == index) {
                        viewInstance.setChartStatus();
                    }else {
                        viewInstance.setNormalStatus();
                    }
                    break;
            }

            if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_ONLOOKER) {
                viewInstance.setLightLabelStatus(false);
            }

            viewInstance.imageBtnLight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewInstance.setLightLabelStatus(true);
                    //停止计时
                    mManSelectedIndex = viewInstance.index;
                    stopTiming();
                    mStartStatusTimeStatusResp.setManSelect(false);
                    onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
                    if(mStartStatusRoomSendBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                        changeStatus(STATUS_NORMAL);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            //return (DataManager.getInstance().getChartData().getLimitLady() + 1)/2;
            return 5;
        }

        /**
         * 更改为选择模式
         */
        private void changeStatus(EnumMemberStatus state) {
            mMemberStatus = state;
            notifyDataSetChanged();
        }

        public EnumMemberStatus getStatus() {
            return mMemberStatus;
        }

        public void setCurrentIndex(int index) {
            mCurrentIndex = index;
            changeStatus(STATUS_CHART);
        }
    }

    private class SystemRecyclerdapter extends RecyclerView.Adapter<SystemRecyclerdapter.SystemViewHolder> {
        @Override
        public SystemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SystemViewHolder viewHolder = new SystemViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.layout_chart_ui_item_system,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SystemViewHolder holder, int position) {
            JMChartRoomSendBean bean = mSystemEventList.get(position);
            if(bean == null) return;
            holder.mTxvEventTime.setText(bean.getTime());
            holder.mTxvEvent.setText(bean.getMsg() + "\n" + new Gson().toJson(bean) + "\n" + new Gson().toJson(mStartStatusTimeStatusResp));
            //holder.mTxvEvent.setText(bean.getMsg());
        }

        @Override
        public int getItemCount() {
            return mSystemEventList.size();
        }

        public class SystemViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxvEvent;
            public TextView mTxvEventTime;

            public SystemViewHolder(View itemView) {
                super(itemView);
                mTxvEventTime = itemView.findViewById(R.id.text_event_time);
                mTxvEvent = itemView.findViewById(R.id.text_event);
            }
        }
    }

    private class LiveTypeRadioChangeListener implements RadioGroup.OnCheckedChangeListener {
        private boolean isSendMessage;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //发送直播方式更改
            StatusHelpChangeLiveTypeBean changeLiveTypeBean = (StatusHelpChangeLiveTypeBean) mHelpStatusMap
                    .get(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_CHANGR_LIVETYPE);
            JMChartRoomSendBean sendBean = changeLiveTypeBean.getChartSendBeanWillSend(null, BaseStatus.MessageType.TYPE_SEND);
            String liveStr;
            int type;
            if(checkedId == R.id.radio_camera) {
                type = JMChartRoomSendBean.LIVE_CAMERA;
                liveStr = "相机";
            }else if(checkedId == R.id.radio_mic) {
                type = JMChartRoomSendBean.LIVE_MIC;
                liveStr = "音频";
            }else {
                type = JMChartRoomSendBean.LIVE_NONE;
                liveStr = "不使用";
            }

            if(mStartStatusRoomSendBean.getLiveType() == type) {
                return;
            }

            if(!isSendMessage) {
                return;
            }

            sendBean.setLiveType(type);
            sendBean.setMsg(DataManager.getInstance().getUserInfo().getNick_name() + "更改直播方式--" + liveStr);
            sendRoomMessage(sendBean);
            Tools.toast(mXqActivity,"您更改直播方式为--" + liveStr,false);

            mStartStatusRoomSendBean.setLiveType(type);
        }

        public boolean isSendMessage() {
            return isSendMessage;
        }

        public void setSendMessage(boolean sendMessage) {
            isSendMessage = sendMessage;
        }
    }

    /**
     * 添加到系统事件并更新
     * @param bean
     */
    private void addSystemEventAndRefresh(JMChartRoomSendBean bean) {
        mSystemEventList.add(bean);
        updateSystemEvent();
        mRecyclerSystem.scrollToPosition(mSystemAdapter.getItemCount() - 1);
    }

    /**
     * 操作结束
     */
    private void onOperateEnd(BaseStatus baseStatus,JMChartRoomSendBean sendBean,StatusResp statusResp) {
        //恢复初始化
        resetLiveStatus();
        stopTiming();

        if(mLadySelectDialog != null &&mLadySelectDialog.isShowing()) {
            mLadySelectDialog.dismiss();
        }

        switch (statusResp.getHandleType()) {
            case HANDLE_TIME: {
                BaseStatus nextStatus = null;
                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
                    if(mIsDistub) {
                        if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                            //已经请求插话
                            nextStatus = mHelpStatusMap.get(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING);
                            JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_SEND);
                            bean.setIndexNext(mDistubIndex);
                            sendRoomMessage(bean);
                        }
                        return;
                    }
                }

                if(statusResp.isLast()) {
                    nextStatus = mOrderStatusMap.get(baseStatus.getmOrder() + 1);
                }else {
                    nextStatus = mOrderStatusMap.get(baseStatus.getmOrder());
                }
                if(nextStatus != null) {
                    int startIndex = nextStatus.getStartIndex();
                    int nextIndex = nextStatus.getNextIndex(sendBean);

                    int firstLadyIndex = ((StatusManFirstSelectBean) mOrderStatusMap
                            .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).getSelectLadyIndex();
                    int secondLadyIndex = ((StatusManSecondSelectBean) mOrderStatusMap
                            .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).getSelectLadyIndex();
                    if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST
                            || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_SECOND) {
                        if(statusResp.isLast()) {
                            startIndex = firstLadyIndex;
                            nextStatus.setStartIndex(startIndex);
                        }
                    }

                    if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST
                            || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND) {
                        if(!statusResp.isLast()) {
                            nextIndex = secondLadyIndex;
                        }
                    }

                    JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_SEND);
                    if(bean == null) return;
                    bean.setMessageType(BaseStatus.MessageType.TYPE_SEND);

                    if(statusResp.isLast()) {
                        bean.setIndexNext(startIndex);
                    }else {
                        bean.setIndexNext(nextIndex);
                    }
                    bean.setProcessStatus(nextStatus.getStatus());

                    if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                        sendRoomMessage(bean);
                    }
                }
            }
                break;

            case HANDLE_SELECT_LADY_FIRST:
            case HANDLE_SELECT_LADY_SECOND:
            case HANDLE_SELECT_LADY_FINAL:
            case HANDLE_SELECT_MAN_FIRST:
            case HANDLE_SELECT_MAN_SECOND:
            case HANDLE_SELECT_MAN_FINAL: {
                JMChartRoomSendBean bean = baseStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_RESPONSE);
                if(bean.getGender().equals(Constant.GENDER_LADY)) {
                    bean.setLadySelected(mLadySelecteResult);
                }else if (bean.getGender().equals(Constant.GENDER_MAN)) {
                    int index = mManSelectedIndex;
                    if(index == -1 && statusResp.getHandleType() != HANDLE_SELECT_MAN_FINAL) {
                        index = (mPreManSelectedIndex+1)%DataManager.getInstance().getChartData().getLimitLady();
                    }
                    bean.setManSelects(String.valueOf(index));
                    mPreManSelectedIndex = index;
                    mManSelectedIndex = -1;
                }

                if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS
                        && statusResp.isSelf()) {
                    sendRoomMessage(bean);
                }
            }
                break;

            case HANDLE_HELP_DOING_DISTURB: {
                if(statusResp.isLast()) {
                    BaseStatus nextStatus;
                    if(mStartStatusTimeStatusResp.isLast()) {
                        nextStatus = mOrderStatusMap.get(mStartStatusBasebean.getmOrder() + 1);
                    }else {
                        nextStatus = mOrderStatusMap.get(mStartStatusBasebean.getmOrder());
                    }
                    if(nextStatus != null) {
                        int startIndex = nextStatus.getStartIndex();
                        int nextIndex = nextStatus.getNextIndex(mStartStatusRoomSendBean);

                        JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(mStartStatusRoomSendBean, BaseStatus.MessageType.TYPE_SEND);
                        if(bean == null) return;

                        if(mStartStatusTimeStatusResp.isLast()) {
                            bean.setIndexNext(startIndex);
                        }else {
                            bean.setIndexNext(nextIndex);
                        }

                        if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                            sendRoomMessage(bean);
                        }
                    }
                }
            }
                break;

            default:
                break;
        }
    }


    /***********************************各个环节的操作**************************************/
    /**
     * 女生选择
     * @param bean
     * @param flags
     */
    private void operate_SelectLady(final BaseStatus baseStatus, final JMChartRoomSendBean bean, final StatusResp flags) {
        startTiming(baseStatus,bean,flags);
        if(!flags.isSelf()) {
            return;
        }
        //先匹配是否为自己
        if(mLadySelecteResult) {//第一次是接受的情况
            mLadySelectDialog = createLadySelectDialog(bean);
            mLadySelectDialog.show();
        }else if(!mLadySelecteResult){//第一次就拒绝的,直接返回回复
            //延迟5s,发送回应
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onOperateEnd(baseStatus,bean,flags);
                }
            },6*1000);
        }
    }

    /**
     * 男生选择
     * @param bean
     * @param flags
     */
    private void operate_SelectMan(BaseStatus baseStatus,JMChartRoomSendBean bean,StatusResp flags) {
        startTiming(baseStatus,bean,flags);
        if(!flags.isSelf()) {
            return;
        }

        //成员头像，切换为选择状态
        startTiming(baseStatus,bean,flags);
        mMemberAdapter.changeStatus(STATUS_SELECT);
    }

    /**
     * 流程结束
     * @param baseStatus
     * @param sendBean
     * @param statusResp
     */
    private void operate_Final(BaseStatus baseStatus,JMChartRoomSendBean sendBean,StatusResp statusResp) {
        String finalSelectLady = String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex());

        String text = "非常遗憾，匹配失败";
        if(mLadySelectedResultList.contains(finalSelectLady)) {
            text = "恭喜匹配成功";
            mIsRoomMatchSuccess = true;
            if (DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS
                    && DataManager.getInstance().getUserInfo().getGender().equals(Constant.GENDER_LADY)
                    && Integer.parseInt(finalSelectLady) == DataManager.getInstance().getSelfMember().getIndex()) {
                mIsSelefMatchSuccess = true;
            }

            if (DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS
                    && DataManager.getInstance().getUserInfo().getGender().equals(Constant.GENDER_MAN)) {
                //男生
                mIsSelefMatchSuccess = true;
            }
            //更新
            mMemberAdapter.notifyDataSetChanged();
        }
        //显示在系统事件中
        JMChartRoomSendBean bean = mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL)
                .getChartSendBeanWillSend(null, BaseStatus.MessageType.TYPE_SEND);
        bean.setMsg(text);
        handleStatusBean(bean);
    }

    /**
     * 直播方式更改
     * @param baseStatus
     * @param sendBean
     * @param statusResp
     */
    private void operate_LiveType(BaseStatus baseStatus,JMChartRoomSendBean sendBean,StatusResp statusResp) {
        if(sendBean.getLiveType() == JMChartRoomSendBean.LIVE_MIC) {
            mXqCameraViewMg.setVisible(false);
            mXqPlayerViewMg.setVisible(false);
        }else if(sendBean.getLiveType() == JMChartRoomSendBean.LIVE_CAMERA) {
            if(mBtnEnd.getVisibility() == View.VISIBLE) {
                mXqCameraViewMg.setVisible(true);
                mXqPlayerViewMg.setVisible(false);
            }else {
                mXqPlayerViewMg.setVisible(true);
                mXqCameraViewMg.setVisible(false);
            }
        }else if(sendBean.getLiveType() == JMChartRoomSendBean.LIVE_NONE){
            mXqCameraViewMg.setVisible(false);
            mXqPlayerViewMg.setVisible(false);
            mXqCameraViewMg.setMute(true);
        }
    }

    private AlertDialog createLadySelectDialog(final JMChartRoomSendBean bean) {
        final UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        String[] items = {"接收","拒接"};
        AlertDialog dialog = new AlertDialog.Builder(mXqActivity).setTitle("请做出选择").setIcon(R.drawable.chart_room_dialog_select_icon)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            mLadySelecteResult = true;
                        }else if(which == 1) {
                            mLadySelecteResult = false;
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        stopTiming();
                        onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
                    }
                })
                .create();
        return dialog;
    }

    /**
     * 开始倒计时
     * @param bean
     */
    private void startTiming(final BaseStatus baseStatus,final JMChartRoomSendBean bean, final StatusResp statusResp) {
        if(baseStatus == null) return;
        stopTiming();
        mTextCountDown.setVisibility(View.VISIBLE);

        if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_TIME
                || statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_HELP_DOING_DISTURB) {
            if(DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                setLiveStatus(bean,statusResp.isSelf());
            }else {
                setLiveStatus(bean,false);
            }

            if(baseStatus.getRequestRoleType().equals(Constant.ROLRTYPE_ANGEL)) {
                mAngelViewInstance.setChartStatus();
            }else if(baseStatus.getRequestRoleType().equals(Constant.ROLETYPE_GUEST) && baseStatus.getRequestGender().equals(Constant.GENDER_MAN)) {
                mManViewInstance.setChartStatus();
            }else {
                mMemberAdapter.setCurrentIndex(bean.getIndexNext());
            }

            if(statusResp.isSelf() && DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                mBtnEnd.setVisibility(View.VISIBLE);
                //发送回应
                JMChartRoomSendBean responseBean = baseStatus.getChartSendBeanWillSend(bean, BaseStatus.MessageType.TYPE_RESPONSE);
                sendRoomMessage(responseBean);

                //显示切换直播方式按钮
                mCheckChangedListener.setSendMessage(true);
                //mRadioGroupLiveType.setVisibility(View.VISIBLE);
            }
        }
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                timeCount++;
                int time = 0;
                time = baseStatus.getLiveTimeCount() - timeCount;

                if(time > 0) {
                    mTextCountDown.setText(String.valueOf(time) + "s");
                    mTextCountDown.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(this,1000);//下一次循环
                }else {
                    //自行操作结束
                    if(mCurrentStatusType == STATUS_HELP) {
                        onOperateEnd(mHelpStatusBasebean,mHelpStatusRoomSendBean,mHelpStatusTimeStatusResp);
                    }else if(mCurrentStatusType == STATUS_PROGRESS) {
                        onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
                    }
                }
            }
        };
        mHandler.postDelayed(mTimeRunnable,1000);
    }

    /**
     * 停止倒计时
     */
    private void stopTiming() {
        if(mTimeRunnable != null) {
            mHandler.removeCallbacks(mTimeRunnable);
        }
        timeCount = 0;
        mTextCountDown.setVisibility(View.INVISIBLE);
        mTextCountDown.setText("");
        mBtnEnd.setVisibility(View.INVISIBLE);
        mBtnDisturb.setVisibility(View.GONE);
        mTextTip.setVisibility(View.INVISIBLE);
        mRadioGroupLiveType.setVisibility(View.GONE);

        mAngelViewInstance.setNormalStatus();
        mManViewInstance.setNormalStatus();
        mMemberAdapter.setCurrentIndex(-1);

        //告知状态机本次处理已经结束
        if(mCurrentStatusType == STATUS_PROGRESS) {
            mStartStatusBasebean.handleEnd();
        }else if(mCurrentStatusType == STATUS_HELP) {
            mHelpStatusBasebean.handleEnd();
        }
    }

    /**
     * StatusBean,使能够处理消息
     */
    private void handleStatusBean(JMChartRoomSendBean chartRoomSendBean) {
        Iterator iterator = mOrderStatusMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer,BaseStatus> entry = (Map.Entry<Integer, BaseStatus>) iterator.next();
            if(entry.getValue() != null) {
                entry.getValue().handlerRoomChart(chartRoomSendBean);
            }
        }

        Iterator helpIterator = mHelpStatusMap.entrySet().iterator();
        while (helpIterator.hasNext()) {
            Map.Entry<Integer,BaseStatus> entry = (Map.Entry<Integer, BaseStatus>) helpIterator.next();
            if(entry.getValue() != null) {
                entry.getValue().handlerRoomChart(chartRoomSendBean);
            }
        }
    }

    /**
     *发送聊天室信息
     * @param chartRoomSendBean
     */
    private void sendRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        JMsgSender.sendRoomMessage(chartRoomSendBean);
        handleStatusBean(chartRoomSendBean);
    }

    /**
     * 更改直播方式的处理
     * @param chartRoomSendBean
     * @param isSelf
     */
    private void setLiveStatus(JMChartRoomSendBean chartRoomSendBean,boolean isSelf) {
        mCheckChangedListener.setSendMessage(false);//不发送改变直播方式的消息
        switch (chartRoomSendBean.getLiveType()) {
            case JMChartRoomSendBean.LIVE_MIC:
                if(isSelf) {
                    mXqCameraViewMg.start();
                    mXqCameraViewMg.setVisible(false);
                }else {
                    mXqPlayerViewMg.start();
                    mXqPlayerViewMg.setVisible(false);
                }
                mRadioGroupLiveType.check(R.id.radio_mic);
                break;
            case JMChartRoomSendBean.LIVE_CAMERA:
                if(isSelf) {
                    mXqCameraViewMg.setVisible(true);
                    mXqCameraViewMg.start();
                }else {
                    mXqPlayerViewMg.start();
                    mXqPlayerViewMg.setVisible(true);
                }
                mRadioGroupLiveType.check(R.id.radio_camera);
                break;
            case JMChartRoomSendBean.LIVE_NONE:
                mXqCameraViewMg.setVisible(false);
                mXqPlayerViewMg.setVisible(false);
                mXqCameraViewMg.stop();
                mXqPlayerViewMg.stop();
                mRadioGroupLiveType.clearCheck();
                break;
        }
    }

    /**
     * 重置直播方式，以备下次直播
     */
    private void resetLiveStatus() {
        mXqCameraViewMg.stop();
        mXqCameraViewMg.setVisible(false);
        mXqPlayerViewMg.stop();
        mXqPlayerViewMg.setVisible(false);
    }

    /**
     * 接收聊天室消息
     * @param event
     */
    public void onEventMainThread(ChatRoomMessageEvent event) {
        Log.d("yy", "chartRoomMessage received .");
        List<Message> msgs = event.getMessages();
        for (Message msg : msgs) {
            //这个页面仅仅展示聊天室会话的消息
            String jsonStr = msg.getContent().toJson();
            String text = null;
            try {
                JSONObject object = new JSONObject(jsonStr);
                text = object.getString("text");
            }catch (Exception e) {
                Log.e("yy",e.toString());
                return;
            }
            JMChartRoomSendBean chartRoomSendBean = new Gson().fromJson(text,JMChartRoomSendBean.class);
            if(chartRoomSendBean.getRoomId() != DataManager.getInstance().getChartData().getRoomId())
            {
                return;
            }

            if(chartRoomSendBean.getProcessStatus() < mStartStatusRoomSendBean.getProcessStatus()
                    && chartRoomSendBean.getMessageType() == BaseStatus.MessageType.TYPE_SEND) {
                return;
            }
            handleStatusBean(chartRoomSendBean);

        }
    }

    /**
     * 接收普通消息
     * @param event
     */
    public void onEventMainThread(MessageEvent event){
        Log.d("yy", "NormalMessage received .");
        //do your own business
        String message = event.getMessage().getContent().toJson();
        String text = null;
        try {
            JSONObject object = new JSONObject(message);
            text = object.getString("text");
        }catch (Exception e) {
            Log.e("yy",e.toString());
            return;
        }
        JMNormalSendBean normalSendBean = new Gson().fromJson(text,JMNormalSendBean.class);
        if(normalSendBean.getRoomId() != DataManager.getInstance().getChartData().getRoomId())
        {
            return;
        }

        if(normalSendBean.getCode() == JMNormalSendBean.NORMAL_EXIT) {//离开
            Tools.toast(mXqActivity,"房间被解散",true);
            mXqActivity.finish();
        }else if(normalSendBean.getCode() == JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM
                || normalSendBean.getCode() == JMChartRoomSendBean.CHART_ONLOOKER_EXIT) {
            BaseStatus status = mHelpStatusMap.get(normalSendBean.getCode());
            JMChartRoomSendBean sendBean = status.getChartSendBeanWillSend(null, BaseStatus.MessageType.TYPE_SEND);
            sendBean.setMsg(normalSendBean.getMsg());
            status.handlerRoomChart(sendBean);
        }else if(normalSendBean.getCode() == JMChartRoomSendBean.CHART_ONLOOKER_ENTER_STATUS) {
            //围观者进入时的当前房间状态
            if(!mIsOnLookerRecCurrentStatus) {
                try {
                    String extra = normalSendBean.getExtra();
                    JSONObject object = new JSONObject(extra);
                    String value = object.getString("manSelect");
                    if(value != null) {
                        ArrayList<String> list = new Gson().fromJson(value,new TypeToken<ArrayList<String>>(){}.getType());
                        ((StatusManFirstSelectBean)mOrderStatusMap
                                .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).setSelectLadyIndex(Tools.parseInt(list.get(0)));
                        ((StatusManSecondSelectBean)mOrderStatusMap
                                .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).setSelectLadyIndex(Tools.parseInt(list.get(1)));
                        ((StatusManFinalSelectBean)mOrderStatusMap
                                .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).setSelectLadyIndex(Tools.parseInt(list.get(2)));
                    }

                    value = object.getString("ladySelect");
                    if(value != null) {
                        ArrayList<String> list = new Gson().fromJson(value,new TypeToken<ArrayList<String>>(){}.getType());
                        mLadySelectedResultList.clear();
                        mLadySelectedResultList.addAll(list);
                    }

                    value = object.getString("status");
                    if(value != null) {
                        JMChartRoomSendBean bean = new Gson().fromJson(value,JMChartRoomSendBean.class);
                        handleStatusBean(bean);
                    }

                    mMemberAdapter.notifyDataSetChanged();
                    mIsOnLookerRecCurrentStatus = true;
                }catch (Exception e) {
                    com.cd.xq.module.util.tools.Log.e("onEventMainThread--" + e.toString());
                }
            }
        }else if(normalSendBean.getCode() == JMChartRoomSendBean.CHART_GOTO_DOUBLE_ROOM) {
            if(mIsGoToDouble) return;
            try {
                mIsGoToDouble = true;
                JSONObject object = new JSONObject(normalSendBean.getExtra());
                String pushAddress = object.getString("push");
                String playAddress = object.getString("play");
                String target = object.getString("target");
                Intent intent = new Intent(mXqActivity, DoubleRoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("push",pushAddress);
                bundle.putString("play",playAddress);
                bundle.putString("target",target);
                intent.putExtras(bundle);
                mXqActivity.startActivity(intent);
                exit();
            }catch (Exception e) {

            }
        }
    }

    /**
     * 发送当前状态给进入的围观者
     */
    private void sendToOnLookerCurrentStatus(String userName) {
        JMNormalSendBean sendBean = new JMNormalSendBean();
        sendBean.setCode(JMChartRoomSendBean.CHART_ONLOOKER_ENTER_STATUS);
        sendBean.setTargetUserName(userName);
        sendBean.setRoomId(DataManager.getInstance().getChartData().getRoomId());
        JSONObject object = new JSONObject();
        try {
            ArrayList<String> manList = new ArrayList<>();
            manList.add(String.valueOf(((StatusManFirstSelectBean)mOrderStatusMap
                    .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).getSelectLadyIndex()));
            manList.add(String.valueOf(((StatusManSecondSelectBean)mOrderStatusMap
                    .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).getSelectLadyIndex()));
            manList.add(String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                    .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex()));
            String strManSelected = new Gson().toJson(manList);
            object.put("manSelect",strManSelected);

            String strLadySelected = new Gson().toJson(mLadySelectedResultList);
            object.put("ladySelect",strLadySelected);
            object.put("status",new Gson().toJson(mStartStatusRoomSendBean));
        }catch (Exception e) {
            Tools.toast(mXqActivity,"sendToOnLookerCurrentStatus--" + e.toString(),false);
            com.cd.xq.module.util.tools.Log.e("sendToOnLookerCurrentStatus--" + e.toString());
        }

        String strExtra = object.toString();
        sendBean.setExtra(strExtra);
        JMsgSender.sendNomalMessage(sendBean);
    }

    private class ViewInstance {
        public ImageView imageHead;
        public ImageView imageLabelLight;
        public ImageButton imageBtnMic;
        public RelativeLayout reLayoutBg;
        public ImageButton imageBtnLight;
        public TextView textIndex;
        public ImageView imageGender;
        public ImageView imageIndex;

        public int index;

        private ColorDrawable mTransparentDrawable;

        public ViewInstance() {
            mTransparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setSelectedStatus() {
            reLayoutBg.setBackgroundResource(R.drawable.shape_member_bg);
            imageBtnLight.setVisibility(View.VISIBLE);
            imageBtnMic.setVisibility(View.GONE);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setNormalStatus() {
            reLayoutBg.setBackground(mTransparentDrawable);
            imageBtnMic.setVisibility(View.GONE);
            imageBtnLight.setVisibility(View.GONE);
        }

        public void setChartStatus() {
            reLayoutBg.setBackgroundResource(R.drawable.shape_member_bg);
            imageBtnMic.setVisibility(View.VISIBLE);
            imageBtnLight.setVisibility(View.GONE);
        }

        public void setLightLabelStatus(boolean isVisible) {
            imageLabelLight.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
        }
    }

    private class MemberViewHolder extends RecyclerView.ViewHolder{
        public ViewInstance viewHolderLeft = new ViewInstance();
        public ViewInstance viewHolderRight = new ViewInstance();

        public MemberViewHolder(View itemView) {
            super(itemView);

            viewHolderLeft.imageBtnLight = itemView.findViewById(R.id.chart_room_imgBtn_Light_left);
            viewHolderLeft.imageBtnMic = itemView.findViewById(R.id.chart_room_imgBtn_mic_left);
            viewHolderLeft.imageHead = itemView.findViewById(R.id.chart_room_img_head_left);
            viewHolderLeft.imageLabelLight = itemView.findViewById(R.id.chart_room_img_label_light_left);
            viewHolderLeft.reLayoutBg = itemView.findViewById(R.id.chart_room_relayout_left);
            viewHolderLeft.textIndex = itemView.findViewById(R.id.chart_room_text_index_left);
            viewHolderLeft.imageGender = itemView.findViewById(R.id.chart_room_img_label_gender_left);
            viewHolderLeft.imageIndex = itemView.findViewById(R.id.chart_room_image_index_left);

            viewHolderRight.imageBtnLight = itemView.findViewById(R.id.chart_room_imgBtn_Light_right);
            viewHolderRight.imageBtnMic = itemView.findViewById(R.id.chart_room_imgBtn_mic_right);
            viewHolderRight.imageHead = itemView.findViewById(R.id.chart_room_img_head_right);
            viewHolderRight.imageLabelLight = itemView.findViewById(R.id.chart_room_img_label_light_right);
            viewHolderRight.reLayoutBg = itemView.findViewById(R.id.chart_room_relayout_right);
            viewHolderRight.textIndex = itemView.findViewById(R.id.chart_room_text_index_right);
            viewHolderRight.imageGender = itemView.findViewById(R.id.chart_room_img_label_gender_right);
            viewHolderRight.imageIndex = itemView.findViewById(R.id.chart_room_image_index_right);

            Glide.with(mXqActivity)
                    .load(R.drawable.chart_room_default_head)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(viewHolderRight.imageHead);

            Glide.with(mXqActivity)
                    .load(R.drawable.chart_room_default_head)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(viewHolderLeft.imageHead);

            viewHolderLeft.setNormalStatus();
            viewHolderRight.setNormalStatus();
            viewHolderLeft.imageGender.setVisibility(View.GONE);
            viewHolderRight.imageGender.setVisibility(View.GONE);
            viewHolderLeft.imageLabelLight.setVisibility(View.INVISIBLE);
            viewHolderRight.imageLabelLight.setVisibility(View.INVISIBLE);
        }
    }

    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mXqActivity);
        builder.setIcon(R.drawable.chart_room_exit)
                .setTitle("退出")
                .setMessage("确认退出?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
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

    private void setHeadInfoData(UserInfoBean bean) {
        mHeadInfoBgRelayout.setVisibility(View.VISIBLE);
        mHeadInfoViewMg.setImgHead(bean.getHead_image());
        mHeadInfoViewMg.setSpecialInfo(bean.getSpecial_info());
        mHeadInfoViewMg.setNickName(bean.getNick_name());
        mHeadInfoViewMg.setSpecailInfoVisible(false);
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
}
