package com.cd.xq.frame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cd.xq.CommonDialogActivity;
import com.cd.xq.R;
import com.cd.xq.beans.BCheckRoomExpiry;
import com.cd.xq.beans.BGetBanner;
import com.cd.xq.friend.FriendActivity;
import com.cd.xq.login.BlackCheckListener;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.chart.beans.BConsumeGift;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.chart.status.statusBeans.StatusParticipantsEnterBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerEnterBean;
import com.cd.xq.module.chart.utils.ChatTools;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.JMRoomSendParam;
import com.cd.xq.module.util.beans.JMSingleSendParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.BChatRoom;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.common.MultiItemDivider;
import com.cd.xq.module.util.jmessage.JMsgUtil;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.tools.DateUtils;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.my.MyGiftBuyActivity;
import com.cd.xq.network.XqRequestApi;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stx.xhb.xbanner.XBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/11/11.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.btn_angel)
    Button mBtnAngel;
    @BindView(R.id.btn_guest)
    Button mBtnGuest;
    Unbinder unbinder;
    @BindView(R.id.home_onLooker_RecyclerView)
    RecyclerView homeOnLookerRecyclerView;
    @BindView(R.id.home_xbanner)
    XBanner homeXbanner;
    @BindView(R.id.home_refresh_layout)
    SmartRefreshLayout homeRefreshLayout;
    @BindView(R.id.home_btn_friend)
    Button homeBtnFriend;
    @BindView(R.id.text_time_count_down)
    TextView textTimeCountDown;
    @BindView(R.id.linearLayout_float_room)
    LinearLayout linearLayoutFloatRoom;

    private RequestApi mApi;
    private XqRequestApi mXqApi;
    private ChatRequestApi mChatApi;

    private ArrayList<BChatRoom> m_roomList;
    private OnLookerRecyclerViewAdapter mOnLookerAdapter;
    private ArrayList<String> mImageList;
    private Dialog mRoomFloatDialog;
    private RoomFloatViewHolder mFloatViewHolder;
    private BChatRoom mJmChartResp;
    private Runnable mTimeDownRunnable;
    private Handler mHandler;
    private boolean mIsMatch;
    private long mRoomID;
    private EventBusHelp mEventBusHelp;
    private boolean mIsSelfRoomDelete;
    private long mTipTime = 2;  //2分钟
    private boolean mIsRecTipStartRoom;
    private boolean mIsRecAppotintTimeClose;
    private RefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_home, null);
        unbinder = ButterKnife.bind(this, mRootView);
        JMessageClient.registerEventReceiver(this);
        mEventBusHelp = new EventBusHelp();
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mXqApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        mChatApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);
        mHandler = new Handler();

        init();

        return mRootView;
    }

    private void init() {
        m_roomList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mOnLookerAdapter = new OnLookerRecyclerViewAdapter();
        homeOnLookerRecyclerView.setAdapter(mOnLookerAdapter);
        homeOnLookerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MultiItemDivider divider = new MultiItemDivider(getActivity(), DividerItemDecoration
                .VERTICAL,
                ContextCompat.getDrawable(getActivity(), R.drawable.shape_home_recycler_divider));
        divider.setDividerMode(MultiItemDivider.INSIDE);
        homeOnLookerRecyclerView.addItemDecoration(divider);

        homeBtnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataManager.getInstance().getUserInfo().isOnLine()) {
                    Tools.toast(getActivity(), "请先登录...", true);
                    return;
                }

                Intent intent = new Intent(getActivity(), FriendActivity.class);
                getActivity().startActivity(intent);
            }
        });

        mBtnAngel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataManager.getInstance().getUserInfo().isOnLine()) {
                    Tools.toast(getActivity(), "请先登录...", true);
                    return;
                }

                if (!DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                    Tools.toast(getActivity(), "您不是爱心大使", true);
                    return;
                }

                Intent intent = new Intent(getActivity(), CreateRoomActivity.class);
                getActivity().startActivity(intent);
            }
        });

        mBtnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                    Tools.toast(getActivity(), "您不是Guest", true);
                    return;
                }

                if (DataManager.getInstance().getUserInfo().getMarrige() != Constant.ROLE_UNMARRIED) {
                    //不是未婚状态
                    Tools.toast(getActivity(), "您是观众身份只能围观房间不可参与", true);
                    return;
                }

                //参与者加入房间
                mIsMatch = true;
                mRoomID = -1;
                toCommitJoinParticipant();
            }
        });

        linearLayoutFloatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutFloatRoom.setVisibility(View.GONE);
                mRoomFloatDialog.show();
                mRoomFloatDialog.getWindow().setContentView(mFloatViewHolder.rootView);
            }
        });

        requestGetBanner();
        initSmartRefreshLayout();
        initRoomFloatDialog();
    }

    private void toShowFloatBtn() {
        //判断是否要显示
        if(mJmChartResp != null) {
            linearLayoutFloatRoom.setVisibility(View.VISIBLE);
        }else {
            linearLayoutFloatRoom.setVisibility(View.GONE);
        }
    }

    private void initRoomFloatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_home_float_room, null);
        mFloatViewHolder = new RoomFloatViewHolder();
        mFloatViewHolder.rootView = rootView;
        mFloatViewHolder.viewBtnLayout = rootView.findViewById(R.id.linearLayout_room_btn);
        mFloatViewHolder.textAppointTime = rootView.findViewById(R.id.text_room_appoint_time);
        mFloatViewHolder.textCount = rootView.findViewById(R.id.text_room_count);
        mFloatViewHolder.textCountDownTime = rootView.findViewById(R.id.text_room_time_count_down);
        mFloatViewHolder.textDescription = rootView.findViewById(R.id.text_room_description);
        mFloatViewHolder.textRoomID = rootView.findViewById(R.id.text_room_id);
        mFloatViewHolder.textTitle = rootView.findViewById(R.id.text_room_title);
        mFloatViewHolder.textStatus = rootView.findViewById(R.id.text_room_status);
        mFloatViewHolder.textCurrentNum = rootView.findViewById(R.id.text_room_current_num);
        mFloatViewHolder.textRoomRole = rootView.findViewById(R.id.text_room_role);
        mFloatViewHolder.btnEnter = rootView.findViewById(R.id.btn_room_enter);
        mFloatViewHolder.btnExit = rootView.findViewById(R.id.btn_room_exit);
        mFloatViewHolder.imageClose = rootView.findViewById(R.id.image_room_close);

        mRoomFloatDialog = builder.create();
        mFloatViewHolder.imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRoomFloatDialog.dismiss();
            }
        });
        mFloatViewHolder.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入房间
                doEnterChatRoom();
            }
        });
        mRoomFloatDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(mIsSelfRoomDelete) {
                    if(mJmChartResp != null && DataManager.getInstance().getUserInfo().getUser_name().equals(mJmChartResp.getUser_name())) {
                        requestDeleteChatRoom(-1); //预约退出的
                    }else {
                        requestExitChatRoom(-1); //预约退出的
                    }
                }else {
                    toShowFloatBtn();
                }
            }
        });

        mTimeDownRunnable = new Runnable() {
            @Override
            public void run() {
                calcFloatTimeDown();
                mHandler.postDelayed(this,1000);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBusHelp.onDestroy();
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * 加入房间
     *
     * @param roomRoleType
     * @param roomId
     */
    private void joinChartRoom(int roomRoleType,long roomId,boolean isMatch) {
        if (!DataManager.getInstance().getUserInfo().isOnLine()) {
            Tools.toast(getActivity(), "请先登录...", true);
            return;
        }

        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();

        Map<String, Object> params = new HashMap<>();
        params.put("userName", userInfo.getUser_name());
        params.put("gender", userInfo.getGender());
        params.put("level", userInfo.getLevel());
        params.put("roleType", userInfo.getRole_type());
        params.put("roomRoleType", roomRoleType);
        params.put("roomId",roomId);
        if(isMatch) {
            //匹配模式
            params.put("handleType",1);
        }else {
            //指定模式
            params.put("handleType",2);
        }
        mApi.joinChatRoom(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<NetResult<BChatRoom>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BChatRoom>>() {
                    @Override
                    public void accept(NetResult<BChatRoom> jmChartResp) throws Exception {
                        if (jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            return;
                        }
                        if (jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            if(jmChartResp.getStatus() == XqErrorCode.ERROR_FULL_CHATROOM) {
                                Tools.toast(getActivity(), "房间已满员", false);
                            }else if(jmChartResp.getStatus() == XqErrorCode.ERROR_ALREADY_START_CHATROOM) {
                                Tools.toast(getActivity(), "房间已开始", false);
                            }else if(jmChartResp.getStatus() == XqErrorCode.ERROR_NOT_FIND_CHATROOM) {
                                Tools.toast(getActivity(), "未找到房间", false);
                            }else if(jmChartResp.getStatus() == XqErrorCode.ERROR_ALREADY_JOIN_CHATROOM) {
                                Tools.toast(getActivity(), "你已加入过其他房间", false);
                            }else {
                                Tools.toast(getActivity(), jmChartResp.getMsg(), false);
                            }
                            Log.e(jmChartResp.getMsg());
                            return;
                        }
                        //发送加入房间的消息
                        JMRoomSendParam param = new JMRoomSendParam();
                        param.setCode(JM_ROOM_CODE_JOIN); //加入的消息
                        param.setRoomId(jmChartResp.getData().getRoom_id());
                        sendJMRoomMessage(param,true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(getActivity(), throwable.toString(), true);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        homeXbanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        homeXbanner.stopAutoPlay();
    }

    private void initSmartRefreshLayout() {
        homeRefreshLayout.setRefreshHeader(new BezierCircleHeader(getActivity()));
        //设置 Footer 为 球脉冲 样式
        homeRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle
                (SpinnerStyle.Scale));
        homeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //刷新
                mRefreshLayout = refreshLayout;
                requestGetBanner();
                requestGetChatRoomByUser();
            }
        });
        homeRefreshLayout.setEnableLoadMore(false);
    }

    /**
     * 获取Banner
     */
    private void requestGetBanner() {
        mXqApi.getBanner()
                .compose(this.<NetResult<List<BGetBanner>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetBanner>>>() {
                    @Override
                    public void accept(NetResult<List<BGetBanner>> bGetBannerNetResult) throws Exception {
                        if (bGetBannerNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("requestGetBanner--" + bGetBannerNetResult.getMsg());
                            return;
                        }
                        mImageList.clear();
                        for (int i = 0; i < bGetBannerNetResult.getData().size(); i++) {
                            mImageList.add(bGetBannerNetResult.getData().get(i).getImage());
                        }
                        initXBanner();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(), throwable.toString(), false);
                        Log.e("requestGetBanner--" + throwable.toString());
                    }
                });
   }

    /**
     * 获取房间
     */
    private void requestGetChatRoomByUser() {
        mApi.getChatRoomByUser(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<BChatRoom>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BChatRoom>>() {
                    @Override
                    public void accept(NetResult<BChatRoom> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("requestGetChatRoomByUser--" + netResult.getMsg());
                            return;
                        }

                        mJmChartResp = netResult.getData();
                        setFloatViewInfo();
                        //获取下方列表
                        setOnLookerRecyclerView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("requestGetChatRoomByUser--" + throwable.toString());
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                    }
                });
    }

    private void doEnterChatRoom() {
        if(mJmChartResp == null) return;
        //申请权限
        requestPermission(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                if (!isAll) {
                    Tools.toast(getActivity(), "请同意权限", false);
                    XXPermissions.gotoPermissionSettings(getActivity());
                    return;
                }

                requestEnterChatRoom();

            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {

            }
        });
    }

    /**
     * 进入房间
     */
    private void requestEnterChatRoom() {
        if(mJmChartResp == null) return;
        mApi.enterChatRoom(mJmChartResp.getRoom_id(),DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<BChatRoom>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BChatRoom>>() {
                    @Override
                    public void accept(NetResult<BChatRoom> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("requestEnterChatRoom--" + netResult.getMsg());
                            return;
                        }

                        if(netResult.getData() != null) {
                            //设置到全局中
                            DataManager.getInstance().setChartBChatRoom(netResult.getData());
                            //进入到聊天页面
                            mRoomFloatDialog.dismiss();
                            Intent intent = new Intent(getActivity(),ChartRoomActivity.class);
                            getActivity().startActivity(intent);
                        }else {
                            Log.e("requestEnterChatRoom--Data is null");
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("requestEnterChatRoom--" + throwable.toString());
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                    }
                });

    }

    /**
     * 删除房间
     */
    private void requestDeleteChatRoom(final int status) {
        if(mJmChartResp == null) return;
        //发送聊天室删除消息
        JMRoomSendParam sendParam = new JMRoomSendParam();
        sendParam.setCode(JM_ROOM_CODE_DELETE); //加入的消息
        sendParam.setRoomId(mJmChartResp.getRoom_id());
        sendJMRoomMessage(sendParam,false);

        HashMap<String,Object> param = new HashMap<>();
        param.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        param.put("roomId",mJmChartResp.getRoom_id());
        param.put("status",status); //失败
        mApi.deleteChatRoom(param)
                .compose(this.<NetResult<BChatRoom>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BChatRoom>>() {
                    @Override
                    public void accept(NetResult<BChatRoom> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("requestDeleteChatRoom--" + netResult.getMsg());
                            return;
                        }

                        mJmChartResp = null;
                        setFloatViewInfo();
                        Tools.toast(getActivity().getApplicationContext(),"房间已删除",false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("requestDeleteChatRoom--" + throwable.toString());
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                    }
                });
    }

    /**
     * 退出房间
     */
   private void requestExitChatRoom(final int status) {
       if(mJmChartResp == null) return;

       HashMap<String,Object> param = new HashMap<>();
       param.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
       param.put("roomId",mJmChartResp.getRoom_id());
       param.put("status",status); //失败
       param.put("innerId",-1);
       param.put("joinType",mJmChartResp.getIsQueue());
       mApi.exitChatRoom(param)
                .compose(this.<NetResult<BChatRoom>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BChatRoom>>() {
                    @Override
                    public void accept(NetResult<BChatRoom> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getActivity().getApplication(),netResult.getMsg(),false);
                            Log.e("requestExitChatRoom--" + netResult.getMsg());
                            return;
                        }

                        mJmChartResp = null;
                        setFloatViewInfo();
                        //发送退出房间的消息
                        JMRoomSendParam sendParam = new JMRoomSendParam();
                        sendParam.setCode(JM_ROOM_CODE_EXIT); //加入的消息
                        sendParam.setRoomId(netResult.getData().getRoom_id());
                        sendJMRoomMessage(sendParam,true);
                        //退出聊天室
                        JMsgUtil.exitJMChatRoom(netResult.getData().getRoom_id(),null);

                        if(status == -1 && !mIsSelfRoomDelete) {
                            Tools.toast(getActivity().getApplicationContext(),"退出房间成功",false);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("requestExitChatRoom--" + throwable.toString());
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                    }
                });
   }

    /**
     * 设置float view info
     */
    private void setFloatViewInfo() {
        mHandler.removeCallbacks(mTimeDownRunnable);
        if(mJmChartResp == null) {
            mRoomFloatDialog.dismiss();
            linearLayoutFloatRoom.setVisibility(View.GONE);
            return;
        }

        if(!mRoomFloatDialog.isShowing()) {
            linearLayoutFloatRoom.setVisibility(View.VISIBLE);
        }

        mFloatViewHolder.textAppointTime.setText("开始时间：" + mJmChartResp.getAppoint_time());
        int currentNum = mJmChartResp.getCount_angel() + mJmChartResp.getCount_lady()
                + mJmChartResp.getCount_man();
        int limitNum = mJmChartResp.getLimit_angel()
                + mJmChartResp.getLimit_lady() + mJmChartResp.getLimit_man();
        mFloatViewHolder.textCount.setText("参与人数：" + limitNum);
        String str = "当前人数：" + currentNum;
        if(currentNum < limitNum) {
            str += "（未满）";
        }else {
            str += "（满员）";
        }
        mFloatViewHolder.textCurrentNum.setText(str);
        if(TextUtils.isEmpty(mJmChartResp.getDescribe())) {
            mFloatViewHolder.textDescription.setVisibility(View.GONE);
        }else {
            mFloatViewHolder.textDescription.setVisibility(View.VISIBLE);
            mFloatViewHolder.textDescription.setText("描述：" + "\n" + mJmChartResp.getDescribe());
        }
        mFloatViewHolder.textRoomID.setText("房间ID：" + String.valueOf(mJmChartResp.getRoom_id()));
        mFloatViewHolder.textTitle.setText("主题：" + mJmChartResp.getTitle());
        mFloatViewHolder.textCountDownTime.setVisibility(View.GONE);
        mFloatViewHolder.btnEnter.setVisibility(View.VISIBLE);
        if(mJmChartResp.getWork() == 0) {
            //还未开始
            mHandler.post(mTimeDownRunnable);
            mFloatViewHolder.textStatus.setText("未开始");
            mFloatViewHolder.textCountDownTime.setVisibility(View.VISIBLE);
        }else if(mJmChartResp.getWork() == 1) {
            //开始中
            if(!mIsRecAppotintTimeClose) {
                //发送已经开始
                showAppointTimeCloseDialog();
                mIsRecAppotintTimeClose = true;
            }
            mFloatViewHolder.textStatus.setText("进行中");
        }else {
            //已结束
            mFloatViewHolder.textStatus.setText("已结束");
            mFloatViewHolder.btnEnter.setVisibility(View.GONE);
        }
        str = "本人角色：";
        if(DataManager.getInstance().getUserInfo().getUser_name().equals(mJmChartResp.getUser_name())) {
            //创造者
            str += "创建者";
        }else {
            if(mJmChartResp.getRoom_role_type() == Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                str += "参与者";
            }else {
                str += "观众";
            }
        }
        mFloatViewHolder.textRoomRole.setText(str);
        if(mIsSelfRoomDelete) {
            mFloatViewHolder.btnExit.setText("关闭");
            mFloatViewHolder.textStatus.setText("被取消");
        }else {
            if(DataManager.getInstance().getUserInfo().getUser_name().equals(mJmChartResp.getUser_name())) {
                mFloatViewHolder.btnExit.setText("删除");
            }else {
                mFloatViewHolder.btnExit.setText("退出");
            }
        }
        mFloatViewHolder.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsSelfRoomDelete) {
                    mRoomFloatDialog.dismiss();
                }else {
                    if(DataManager.getInstance().getUserInfo().getUser_name().equals(mJmChartResp.getUser_name())) {
                        requestDeleteChatRoom(-1); //预约退出的
                    }else {
                        requestExitChatRoom(-1); //预约退出的
                    }
                }
            }
        });
        //显示Dialog
        mRoomFloatDialog.show();
        mRoomFloatDialog.getWindow().setContentView(mFloatViewHolder.rootView);
    }

    private void calcFloatTimeDown() {
        if(mJmChartResp == null) return;
        long delta = -(System.currentTimeMillis() - DateUtils.getStringToDate(mJmChartResp.getAppoint_time(),
                "yyyy-MM-dd HH:mm:ss")*1000)/1000;
        boolean isReturn = false;
        String returnStr = "";

        if(delta <= 0) {
            isReturn = true;
            returnStr = "到预约时间";
        }

        if(delta <= 0 && !mIsRecAppotintTimeClose) {
            //发送已经开始
            showAppointTimeCloseDialog();
            mIsRecAppotintTimeClose = true;
        }

        if(mJmChartResp.getWork() == 2) {
            //房间结束
            isReturn = true;
            returnStr = "已结束";
            mRoomFloatDialog.setCanceledOnTouchOutside(false);
            mFloatViewHolder.viewBtnLayout.setVisibility(View.GONE);
            mRoomFloatDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestExitChatRoom(0); //结束，标识为失败
                }
            });
        }

        if(isReturn) {
            mHandler.removeCallbacks(mTimeDownRunnable);
            mFloatViewHolder.textCountDownTime.setText(returnStr);
            textTimeCountDown.setText(returnStr);
            return;
        }

        mFloatViewHolder.viewBtnLayout.setVisibility(View.VISIBLE);
        int hour = (int) (delta/(60*60));
        int min = (int) ((delta-hour*60*60)/60);
        int sec = (int) ((delta - hour*60*60 - min*60));
        String str = "";
        if(hour > 0) {
            str += hour+"时";
        }
        if(min > 0) {
            str += min + "分";
        }
        if(sec > 0) {
            str += sec + "秒";
        }
        String timeCount = "倒计时：" + str;
        mFloatViewHolder.textCountDownTime.setText(timeCount);
        textTimeCountDown.setText(str);

        if(delta > 0 && delta <= mTipTime*60 && !mIsRecTipStartRoom) {
            //发送进入房间的提示
            String text = "房间还有" + str + "将开始，你是否进入房间？";
            showTipStartDialog(text);
            mIsRecTipStartRoom = true;
        }
    }


    private void initXBanner() {
        homeXbanner.setData(mImageList, null);
        homeXbanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(getActivity())
                        .load(model)
                        .into((ImageView) view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mHandler.removeCallbacks(mTimeDownRunnable);
    }

    @Override
    public void onLogin() {
        super.onLogin();
        //获取房间
        requestGetChatRoomByUser();
    }

    /**
     * 申请权限
     *
     * @param onPermission
     */
    private void requestPermission(OnPermission onPermission) {
        if (onPermission == null) {
            return;
        }
        XXPermissions.with(getActivity())
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission
                // .REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.CAMERA, Permission.RECORD_AUDIO)
                .request(onPermission);
    }

    private void setOnLookerRecyclerView() {
        /*m_roomList.clear();
        for(int i = 0 ; i < 100 ; i++) {
            BGetArrays bean = new BGetArrays();
            bean.setCreater("wys30201");
            if(i % 5 == 0) {
                bean.setDescribe("我的得分热热热斯蒂芬大师傅大师傅大师傅大师傅大师傅大师傅但是但是但是犯得上犯得上犯得上犯得上犯得上");
            }else {
                bean.setDescribe("欢迎回来");
            }
            bean.setRoomId(i);
            m_roomList.add(bean);
        }
        mOnLookerAdapter.notifyDataSetChanged();*/

        //只获取公开的
        HashMap<String,Object> param = new HashMap<>();
        param.put("public",1);
//        if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)) {
//            if(DataManager.getInstance().getUserInfo().getMarrige() == Constant.ROLE_UNMARRIED) {
//                //未婚
//                param.put("work",0);  //进行中
//            }else {
//                param.put("work",1);  //预约状态的
//            }
//        }else {
//            param.put("work",0);
//        }
        param.put("work",0);
        mXqApi.getChatRoomList(param)
                .subscribeOn(Schedulers.io())
                .compose(this.<NetResult<List<BChatRoom>>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BChatRoom>>>() {
                    @Override
                    public void accept(NetResult<List<BChatRoom>> bGetArraysNetResult) throws
                            Exception {
                        if (bGetArraysNetResult.getStatus() != XqErrorCode.SUCCESS
                                && bGetArraysNetResult.getStatus() != XqErrorCode.ERROR_NO_DATA) {
                            Log.e(bGetArraysNetResult.getMsg());
                            Tools.toast(getActivity(), bGetArraysNetResult.getMsg(), true);
                            return;
                        }

                        m_roomList.clear();
                        if (bGetArraysNetResult.getData() != null) {
                            m_roomList.addAll(bGetArraysNetResult.getData());
                        }
                        mOnLookerAdapter.notifyDataSetChanged();
                        if(mRefreshLayout != null) {
                            mRefreshLayout.finishRefresh();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("setOnLookerRecyclerView--" + throwable.toString());
                        Tools.toast(getActivity(), throwable.toString(), false);
                    }
                });
    }

    private class OnLookerViewHolder extends RecyclerView.ViewHolder {
        public View viewBtnLayout;
        public TextView textTitle;
        public TextView textRoomID;
        public TextView textCount;
        public TextView textAppointTime;
        public TextView textCountDownTime;
        public TextView textDescription;
        public TextView textStatus;
        public TextView textCurrentNum;
        public Button btnExit;
        public Button btnEnter;

        public boolean isSelfRoom;  //是否是自己的房间

        public OnLookerViewHolder(View rootView) {
            super(rootView);
            viewBtnLayout = rootView.findViewById(R.id.linearLayout_room_btn);
            textAppointTime = rootView.findViewById(R.id.text_room_appoint_time);
            textCount = rootView.findViewById(R.id.text_room_count);
            textCountDownTime = rootView.findViewById(R.id.text_room_time_count_down);
            textDescription = rootView.findViewById(R.id.text_room_description);
            textRoomID = rootView.findViewById(R.id.text_room_id);
            textTitle = rootView.findViewById(R.id.text_room_title);
            textStatus = rootView.findViewById(R.id.text_room_status);
            textCurrentNum = rootView.findViewById(R.id.text_room_current_num);
            btnEnter = rootView.findViewById(R.id.btn_room_enter);
            btnExit = rootView.findViewById(R.id.btn_room_exit);
        }
    }

    private class OnLookerRecyclerViewAdapter extends RecyclerView.Adapter<OnLookerViewHolder> {
        @Override
        public OnLookerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            OnLookerViewHolder holder = new OnLookerViewHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.layout_home_onlooker_recycler_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(final OnLookerViewHolder holder, int position) {
            final BChatRoom resp = m_roomList.get(position);
            if(resp == null) return;
            holder.textAppointTime.setText("开始时间：" + resp.getAppoint_time());
            int currentNum = resp.getCount_angel() + resp.getCount_lady() + resp.getCount_man();
            int limitNum = resp.getLimit_angel() + resp.getLimit_lady() + resp.getLimit_man();
            holder.textCount.setText("参与人数：" + limitNum);
            String str = "当前人数：" + currentNum;
            if(currentNum < limitNum) {
                str += "（未满）";
            }else {
                str += "（满员）";
            }
            holder.textCurrentNum.setText(str);
            if(TextUtils.isEmpty(resp.getDescribe())) {
                holder.textDescription.setVisibility(View.GONE);
            }else {
                holder.textDescription.setVisibility(View.VISIBLE);
                holder.textDescription.setText("描述：" + "\n" + resp.getDescribe());
            }
            holder.textRoomID.setText("房间ID：" + String.valueOf(resp.getRoom_id()));
            holder.textTitle.setText("主题：" + resp.getTitle());
            if(resp.getWork() == 0) {
                //还未开始
                holder.textStatus.setText("未开始");
                holder.textCountDownTime.setVisibility(View.VISIBLE);
            }else if(resp.getWork() == 1) {
                //开始中
                holder.textStatus.setText("进行中");
            }else {
                //已结束
                holder.textStatus.setText("已结束");
            }
            holder.textCountDownTime.setVisibility(View.GONE);
            boolean isSelf = false;
            if(mJmChartResp != null && mJmChartResp.getRoom_id() == resp.getRoom_id()) {
                //是自己加入的房间
                isSelf = true;
            }
            holder.isSelfRoom = isSelf;
            final String STR_EXIT = "退出";
            final String STR_DELETE = "删除";
            final String STR_ONLOOKER = "围观";
            final String STR_ENTER = "进入";
            final String STR_JOIN = "加入";
            if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                if(isSelf) {
                    holder.btnExit.setText(STR_DELETE);
                    holder.btnEnter.setText(STR_ENTER);
                    holder.btnExit.setVisibility(View.VISIBLE);
                }else {
                    holder.btnEnter.setText(STR_ONLOOKER);
                    holder.btnExit.setVisibility(View.GONE);
                }
            }else {
                if(DataManager.getInstance().getUserInfo().getMarrige() == Constant.ROLE_UNMARRIED) {
                    holder.btnExit.setText(isSelf?STR_EXIT:STR_ONLOOKER);
                    holder.btnEnter.setText(isSelf?STR_ENTER:STR_JOIN);
                    holder.btnExit.setVisibility(View.VISIBLE);
                }else {
                    if(isSelf) {
                        holder.btnExit.setText(STR_EXIT);
                        holder.btnEnter.setText(STR_ENTER);
                        holder.btnExit.setVisibility(View.VISIBLE);
                    }else {
                        holder.btnEnter.setText(STR_ONLOOKER);
                        holder.btnExit.setVisibility(View.GONE);
                    }
                }
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = ((Button)v).getText().toString();
                    if(text.equals(STR_EXIT)) {
                        //退出
                        requestExitChatRoom(resp.getWork()==0?-1:0);
                    }else if(text.equals(STR_DELETE)) {
                        //删除
                        requestDeleteChatRoom(resp.getWork()==0?-1:0);
                    }else if(text.equals(STR_ENTER)) {
                        //进入
                        doEnterChatRoom();
                    }else if(text.equals(STR_JOIN)) {
                        //加入
                        mIsMatch = false;
                        mRoomID = resp.getRoom_id();
                        toCommitJoinParticipant();
                    }else if(text.equals(STR_ONLOOKER)) {
                        //围观
                        joinChartRoom(Constant.ROOM_ROLETYPE_ONLOOKER,resp.getRoom_id(),false);
                    }
                }
            };
            holder.btnExit.setOnClickListener(listener);
            holder.btnEnter.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
            return m_roomList.size();
        }
    }

    /*******************参与者加入房间**************************/
    private void toCommitJoinParticipant() {
        //加入房间
        mXqApi.checkRoomExpiry(DataManager.getInstance().getUserInfo().getUser_name(), 2)
                .compose(this.<NetResult<BCheckRoomExpiry>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BCheckRoomExpiry>>() {
                    @Override
                    public void accept(NetResult<BCheckRoomExpiry> bCheckRoomExpiry) throws Exception {
                        if (bCheckRoomExpiry.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getActivity().getApplicationContext(), bCheckRoomExpiry.getMsg(), false);
                            Log.e("checkRoomExpiry--" + bCheckRoomExpiry.getMsg());
                            return;
                        }

                        if (bCheckRoomExpiry.getData().getExpiry() != null) {
                            //有使用中的卡,则直接创建房间
                            String str = "您有使用中的" + bCheckRoomExpiry.getData().getExpiry().getName() +
                                    ",可免费加入房间,剩余次数" + bCheckRoomExpiry.getData().getExpiry().getExpiry_num();
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 0);
                            toast.show();

                            //加入房间
                            joinChartRoom(Constant.ROOM_ROLETYPE_PARTICIPANTS, mRoomID,mIsMatch);
                        } else {
                            if (bCheckRoomExpiry.getData().getHasCard() == 1) {
                                //有未使用的卡，提示是否使用卡
                                doCreateUseCardDialog(bCheckRoomExpiry.getData());
                            } else {
                                //没有卡，则提示是否使用钻石创建房间
                                doCreateCoinDialog(bCheckRoomExpiry.getData());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(), throwable.toString(), false);
                        Log.e("checkRoomExpiry--" + throwable.toString());
                    }
                });
    }

    /**
     * 去消费
     *
     * @param item
     * @param handleType
     */
    private void doRequestConsumeGift(final BGetGiftItem item, int handleType) {
        //用钻石消费
        if (handleType == 1 && !ChatTools.checkBalance(getActivity(), item.getCoin())) return;
        requestConsumeGift(item, handleType);
    }

    private void requestConsumeGift(final BGetGiftItem item, final int handleType) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userName", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId", item.getGift_id());
        params.put("coin", item.getCoin());
        params.put("toUser", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("handleType", handleType);  //消费方式
        mChatApi.consumeGift(params)
                .compose(this.<NetResult<BConsumeGift>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BConsumeGift>>() {
                    @Override
                    public void accept(NetResult<BConsumeGift> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            if (netResult.getStatus() == XqErrorCode.ERROR_LACK_STOCK) {
                                //余额不足
                                //更新余额
                                DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                                doRequestConsumeGift(item, handleType);
                            } else {
                                Tools.toast(getActivity().getApplicationContext(), netResult.getMsg(), false);
                            }
                            Log.e("requestConsumeGift--" + netResult.getMsg());
                            return;
                        }

                        //更新余额
                        DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                        //加入房间
                        joinChartRoom(Constant.ROOM_ROLETYPE_PARTICIPANTS, mRoomID,mIsMatch);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(), throwable.toString(), false);
                        Log.e("requestConsumeGift--" + throwable.toString());
                    }
                });
    }

    /**
     * 创建是否使用建房卡
     *
     * @param checkRoomExpiry
     */
    private void doCreateUseCardDialog(final BCheckRoomExpiry checkRoomExpiry) {
        String text = "您有未使用的" + checkRoomExpiry.getGift().getName() + ",使用后可免费加入房间"
                + checkRoomExpiry.getGift().getValue() + "次"
                + "，或者使用" + checkRoomExpiry.getTargetGift().getCoin() + "钻石";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("使用卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //使用卡,包裹使用
                        doRequestConsumeGift(checkRoomExpiry.getGift(), 2);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("使用钻石", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //钻石消费
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(), 1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 创建是否购买建房卡
     *
     * @param checkRoomExpiry
     */
    private void doCreateCoinDialog(final BCheckRoomExpiry checkRoomExpiry) {
        String text = "您是否花费" + checkRoomExpiry.getGift().getCoin() + "钻石购买" + checkRoomExpiry.getGift().getName() + ",使用后可免费创建房间"
                + checkRoomExpiry.getGift().getValue() + "次"
                + "，或者使用" + checkRoomExpiry.getTargetGift().getCoin() + "钻石进入房间";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("去购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //购买卡，调转到购买页面
                        Intent intent = new Intent(getActivity(), MyGiftBuyActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("使用钻石", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //钻石消费
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(), 1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Float room viewHolder右下角房间按钮
     */
    private class RoomFloatViewHolder {
        public View rootView;
        public View viewBtnLayout;
        public TextView textTitle;
        public TextView textRoomID;
        public TextView textCount;
        public TextView textAppointTime;
        public TextView textCountDownTime;
        public TextView textDescription;
        public TextView textStatus;
        public TextView textCurrentNum;
        public TextView textRoomRole;
        public Button btnExit;
        public Button btnEnter;
        public ImageView imageClose;
    }

    /**
     * 提示进入房间
     */
    private void showTipStartDialog(String text) {
        if(mJmChartResp == null || DataManager.getInstance().isInChatRoom()) return;
        CommonDialogActivity.ICommonTipStartListener listener = new CommonDialogActivity.ICommonTipStartListener() {
            @Override
            public void onNegative() {
            }

            @Override
            public void onPositive() {
                doEnterChatRoom();
            }
        };
        CommonDialogActivity.setICommonTipStartListener(listener);
        CommonDialogActivity.showDialog(getActivity(),CommonDialogActivity.TYPE_JM_TIP_DIALOG);
    }

    /**
     * 预约时间到
     */
    private void showAppointTimeCloseDialog() {
        if(mJmChartResp == null || DataManager.getInstance().isInChatRoom()) return;
        final boolean isCreater = mJmChartResp.getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name());
        CommonDialogActivity.ICommonAppointTimeCloseListener listener = new CommonDialogActivity.ICommonAppointTimeCloseListener() {
            @Override
            public void onNegative() {
                if(isCreater) {
                    requestDeleteChatRoom(-1);
                }else {
                    requestExitChatRoom(-1);
                }
            }

            @Override
            public void onPositive() {
                doEnterChatRoom();
            }
        };
        CommonDialogActivity.setICommonAppointTimeCloseListener(listener);
        CommonDialogActivity.showDialog(getActivity(),CommonDialogActivity.TYPE_JM_APPOINTTIME_CLOSE_DIALOG
                ,mJmChartResp.getUser_name());
        requestGetChatRoomByUser();
    }

    /****************************JM 房间信息处理*********************************************/
    private final int JM_ROOM_CODE_JOIN = 1;
    private final int JM_ROOM_CODE_EXIT = 2;
    private final int JM_ROOM_CODE_DELETE = 3;

    private void sendJMRoomMessage(JMRoomSendParam param,boolean isHanldeSelf) {
        JMsgUtil.sendJMRoomMessage(param);
        if(isHanldeSelf) {
            handleJMRoomMessage(param);
        }
    }

    private void sendJMSingleMessage(JMSingleSendParam param,boolean isHanldeSelf) {
        JMsgUtil.sendJMSigleMessage(param);
        if(isHanldeSelf) {
            handleJMSingleMessage(param);
        }
    }

    /**
     * 处理聊天室的消息
     * @param param
     */
    private void handleJMRoomMessage(JMRoomSendParam param) {
        if(param.getCode() == JM_ROOM_CODE_JOIN
                || param.getCode() == JM_ROOM_CODE_EXIT
                || param.getCode() == JM_ROOM_CODE_DELETE) {
            //有人加入房间，更新自己的房间
            mIsSelfRoomDelete = false;
            if(param.getCode() == JM_ROOM_CODE_DELETE) {
                mIsSelfRoomDelete = true;
            }
            requestGetChatRoomByUser();
        }
    }

    /**
     * 处理单个的消息
     * @param param
     */
    private void handleJMSingleMessage(JMSingleSendParam param) {

    }



    /**
     * 接收聊天室消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChatRoomMessageEvent event) {
        List<Message> msgs = event.getMessages();
        for (Message msg : msgs) {
            //这个页面仅仅展示聊天室会话的消息
            String jsonStr = msg.getContent().toJson();
            String text = null;
            try {
                JSONObject object = new JSONObject(jsonStr);
                text = object.getString("text");
                JMRoomSendParam chartRoomSendBean = new Gson().fromJson(text,JMRoomSendParam.class);
                handleJMRoomMessage(chartRoomSendBean);
            }catch (Exception e) {
                Log.e("yy",e.toString());
                return;
            }
        }
    }

    /**
     * 接收普通消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String message = event.getMessage().getContent().toJson();
        String text = null;
        try {
            JSONObject object = new JSONObject(message);
            text = object.getString("text");
            JMSingleSendParam chartRoomSendBean = new Gson().fromJson(text,JMSingleSendParam.class);
            handleJMSingleMessage(chartRoomSendBean);
        }catch (Exception e) {
            Log.e("yy",e.toString());
            return;
        }
    }

    /**
     * 处理EventBus的类
     */
    public class EventBusHelp {
        public EventBusHelp() {
            EventBus.getDefault().register(this);
        }

        /**
         * EventBus事件
         *
         * @param param
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventBus(EventBusParam param) {
            if (param.getEventBusCode() == EventBusParam.EVENT_BUS_CHATROOM_CREATE
                    || param.getEventBusCode() == EventBusParam.EVENT_BUS_CHATROOM_DELETE) {
                //更新聊天室列表
                requestGetChatRoomByUser();
            } else if (param.getEventBusCode() == EventBusParam.EVENT_BUS_CHECK_BLACKUSER) {
                //检查是否是黑名单
                Tools.checkUserOrBlack(getActivity(), DataManager.getInstance().getUserInfo().getUser_name(), new BlackCheckListener() {
                    @Override
                    public void onResult(boolean isBlack) {

                    }
                });
            }
        }

        public void onDestroy() {
            EventBus.getDefault().unregister(this);
        }
    }
}
