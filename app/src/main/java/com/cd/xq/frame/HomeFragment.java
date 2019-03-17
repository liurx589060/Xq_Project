package com.cd.xq.frame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.AppConstant;
import com.cd.xq.R;
import com.cd.xq.beans.BGetArrays;
import com.cd.xq.beans.BusChatRoomParam;
import com.cd.xq.friend.FriendActivity;
import com.cd.xq.login.BlackCheckListener;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.chart.status.statusBeans.StatusMatchBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerEnterBean;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.common.MultiItemDivider;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.glide.GlideRoundTransform;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.stx.xhb.xbanner.XBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/11/11.
 */

public class HomeFragment extends BaseFragment {
    public static final int AC_CHATROOM_REQUEST_CODE = 1000;

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

    private RequestApi mApi;
    private XqRequestApi mXqApi;

    private String mTXPushAddress = "";
    private String mTXPlayerAddress = "";
    private int mPushAddressType = 0;
    private int mPublic = 1;  // 1：公开    0：非公开

    private ArrayList<BGetArrays> m_roomList;
    private OnLookerRecyclerViewAdapter mOnLookerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_home, null);
        unbinder = ButterKnife.bind(this, mRootView);
        EventBus.getDefault().register(this);

        init();

        return mRootView;
    }

    private void init() {
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mXqApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        m_roomList = new ArrayList<>();
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
                if(!DataManager.getInstance().getUserInfo().isOnLine()) {
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
                requestPermission(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (!isAll) {
                            Tools.toast(getActivity(), "请同意权限", false);
                            XXPermissions.gotoPermissionSettings(getActivity());
                            return;
                        }

                        UserInfoBean infoBean = DataManager.getInstance().getUserInfo();
                        if (infoBean == null) {
                            Tools.toast(getActivity(), "请先登录", false);
                            return;
                        }

                        UserInfoBean bean = new UserInfoBean();
                        bean.setUser_name(DataManager.getInstance().getUserInfo().getUser_name());
                        bean.setRole_type(DataManager.getInstance().getUserInfo().getRole_type());
                        bean.setGender(DataManager.getInstance().getUserInfo().getGender());
                        bean.setLevel(DataManager.getInstance().getUserInfo().getLevel());
                        bean.setLimitLady(AppConstant.CHATROOM_LIMIT_LADY_COUNT);
                        bean.setLimitLevel(-1);
                        createChartRoom(bean);
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
            }
        });

        mBtnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (!isAll) {
                            Tools.toast(getActivity(), "请同意权限", false);
                            XXPermissions.gotoPermissionSettings(getActivity());
                            return;
                        }

                        UserInfoBean infoBean = DataManager.getInstance().getUserInfo();
                        if (infoBean == null) {
                            Tools.toast(getActivity(), "请先登录", false);
                            return;
                        }

                        if (!infoBean.getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                            Tools.toast(getActivity(), "您不是Guest", true);
                            return;
                        }

                        joinChartRoom(Constant.ROOM_ROLETYPE_PARTICIPANTS, -1);
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
            }
        });

        setOnLookerRecyclerView();
        initXBanner();
        initSmartRefreshLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void createChartRoom(UserInfoBean userInfo) {
        if (userInfo == null) {
            Log.e("createChartRoom UserInfoBean=null");
            return;
        }

        if(!DataManager.getInstance().getUserInfo().isOnLine()) {
            Tools.toast(getActivity(), "请先登录...", true);
            return;
        }

        if (!userInfo.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            Tools.toast(getActivity(), "您不是爱心大使", true);
            return;
        }

        setLiveAddress();

        Map<String, Object> params = new HashMap<>();
        params.put("userName", userInfo.getUser_name());
        params.put("gender", userInfo.getGender());
        params.put("level", userInfo.getLevel());
        params.put("limitLevel", userInfo.getLimitLevel());
        params.put("limitLady", userInfo.getLimitLady());
        params.put("limitMan", userInfo.getLimitMan());
        params.put("limitAngel", userInfo.getLimitAngel());
        params.put("pushAddress", Base64.encodeToString(mTXPushAddress.getBytes(), Base64.DEFAULT));
        params.put("playAddress", Base64.encodeToString(mTXPlayerAddress.getBytes(),
                Base64.DEFAULT));
        params.put("public", mPublic);
        params.put("describe", "一起来相亲吧");

        if (userInfo.getLimitLady() % 2 != 0) {
            Tools.toast(getActivity(), "请输入偶数", true);
            return;
        }

        mApi.createChartRoom(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<JMChartResp>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if (jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            Tools.toast(getActivity(), "jmChartResp is null", true);
                            return;
                        }
                        if (jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e(jmChartResp.getMsg());
                            Tools.toast(getActivity(), jmChartResp.getMsg(), true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(getActivity(), ChartRoomActivity.class);
                        getActivity().startActivityForResult(intent, AC_CHATROOM_REQUEST_CODE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(getActivity(), throwable.toString(), true);
                    }
                });
    }

    private void joinChartRoom(int roomRoleType, long roomId) {
        if(!DataManager.getInstance().getUserInfo().isOnLine()) {
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
        if ((Integer) params.get("roomRoleType") == Constant.ROOM_ROLETYPE_ONLOOKER) {
            params.put("roomId", roomId);
        }
        mApi.joinChartRoom(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<JMChartResp>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if (jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            Tools.toast(getActivity(), "jmChartResp is null", true);
                            return;
                        }
                        if (jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e(jmChartResp.getMsg());
                            Tools.toast(getActivity(), jmChartResp.getMsg(), true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(getActivity(), ChartRoomActivity.class);
                        if (DataManager.getInstance().getSelfMember().getRoomRoleType() ==
                                Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                            getActivity().startActivityForResult(intent, AC_CHATROOM_REQUEST_CODE);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        //发送聊天室信息
                        sendChartRoomMessage();
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
        homeRefreshLayout.setEnableLoadMore(false);
    }

    private void initXBanner() {
        ArrayList<String> images;
        ArrayList<String> titles;
        images = new ArrayList<>();
        titles = new ArrayList<>();
        images.add("https://gss1.bdstatic" +
                ".com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign" +
                "=e9449e382d9759ee4a5067cd8ac0242b/94cad1c8a786c9179e80a80cc23d70cf3bc75700.jpg");
        titles.add("这是第1张图片");
        images.add("http://img.ivsky.com/img/bizhi/co/201711/27/nissan_vmotion2_0-001.jpg");
        titles.add("这是第2张图片");
        images.add("http://img.ivsky.com/img/tupian/co/201709/18/zise_huaduo.jpg");
        titles.add("这是第3张图片");
        images.add("http://img.ivsky.com/img/tupian/co/201709/16/dahailidehaiguitupian.jpg");
        titles.add("这是第4张图片");

        homeXbanner.setData(images, titles);
        homeXbanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(getActivity())
                        .load(model)
                        .into((ImageView) view);
            }
        });
    }

    private void sendChartRoomMessage() {
        //发送聊天室信息
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
        JMChartRoomSendBean bean = null;
        if (DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant
                .ROOM_ROLETYPE_PARTICIPANTS) {
            bean = new StatusMatchBean().getChartSendBeanWillSend(null, BaseStatus.MessageType
                    .TYPE_SEND);
        } else if (DataManager.getInstance().getSelfMember().getRoomRoleType() == Constant
                .ROOM_ROLETYPE_ONLOOKER) {
            bean = new StatusOnLookerEnterBean().getChartSendBeanWillSend(null, BaseStatus
                    .MessageType.TYPE_SEND);
        }
        bean.setCurrentCount(data.getMembers().size());
        bean.setIndexNext(DataManager.getInstance().getSelfMember().getIndex());

        JMsgSender.sendRoomMessage(bean);
    }

    /*
     * KEY+ stream_id + txTime
	 */
    private void setLiveAddress() {
        if (mPushAddressType == 0) {
            //本地
            mTXPushAddress = NetWorkMg.getCameraUrl();
            mTXPlayerAddress = mTXPushAddress;
        } else if (mPushAddressType == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);
            long txTime = calendar.getTimeInMillis() / 1000;
            String input = new StringBuilder().
                    append(Constant.TX_LIVE_PUSH_KEY).
                    append(Constant.TX_LIVE_BIZID + "_"
                            + String.valueOf(DataManager.getInstance().getUserInfo().getUser_id())).
                    append(Long.toHexString(txTime).toUpperCase()).toString();
            Log.d("yy", input);

            String txSecret = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                txSecret = byteArrayToHexString(
                        messageDigest.digest(input.getBytes("UTF-8")));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e(e.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(e.toString());
            }

            mTXPlayerAddress = "rtmp://" + Constant.TX_LIVE_BIZID + ".liveplay.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getUserInfo()
                    .getUser_id();

            String ip = "rtmp://" + Constant.TX_LIVE_BIZID + ".livepush.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getUserInfo()
                    .getUser_id()
                    + "?bizid=" + Constant.TX_LIVE_BIZID;
            mTXPushAddress = new StringBuilder().
                    append(ip).
                    append("&").
                    append("txSecret=").
                    append(txSecret).
                    append("&").
                    append("txTime=").
                    append(Long.toHexString(txTime).toUpperCase()).
                    toString();
        }
        Log.i("yy", "TXPlayerAddress=" + mTXPlayerAddress);
        Log.i("yy", "TXPushAddress=" + mTXPushAddress);
    }

    private String byteArrayToHexString(byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                'd', 'e', 'f'};
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        //getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onLogin() {
        super.onLogin();
        setOnLookerRecyclerView();
    }

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
        mXqApi.getArrays(1)
                .subscribeOn(Schedulers.io())
                .compose(this.<NetResult<List<BGetArrays>>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetArrays>>>() {
                    @Override
                    public void accept(NetResult<List<BGetArrays>> bGetArraysNetResult) throws
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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("setOnLookerRecyclerView--" + throwable.toString());
                        Tools.toast(getActivity(), throwable.toString(), false);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AC_CHATROOM_REQUEST_CODE && resultCode == RESULT_OK) {
            Tools.checkUserOrBlack(getActivity(), DataManager.getInstance().getUserInfo()
                    .getUser_name(), new BlackCheckListener() {
                @Override
                public void onResult(boolean isBlack) {
                }
            });
        }
    }

    private class OnLookerViewHolder extends RecyclerView.ViewHolder {
        public TextView textRoomId;
        public TextView textCreater;
        public TextView textDescibe;
        private View.OnClickListener listener;

        public OnLookerViewHolder(View itemView) {
            super(itemView);
            textCreater = itemView.findViewById(R.id.home_onlooker_recycler_item_creater);
            textDescibe = itemView.findViewById(R.id.home_onlooker_recycler_item_describe);
            textRoomId = itemView.findViewById(R.id.home_onlooker_recycler_item_roomId);
        }

        public void setListener(View.OnClickListener listener) {
            this.listener = listener;
            itemView.setOnClickListener(listener);
        }
    }

    private class OnLookerRecyclerViewAdapter extends RecyclerView.Adapter<OnLookerViewHolder> {
        @Override
        public OnLookerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            OnLookerViewHolder holder = new OnLookerViewHolder(LayoutInflater.from(getActivity())
                    .inflate(
                    R.layout.layout_home_onlooker_recycler_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(OnLookerViewHolder holder, int position) {
            final BGetArrays info = m_roomList.get(position);
            holder.textRoomId.setText(String.valueOf(info.getRoomId()));
            holder.textDescibe.setText(info.getDescribe());
            holder.textCreater.setText("创建者：" + info.getCreater());
            holder.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermission(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            if (!isAll) {
                                Tools.toast(getActivity(), "请同意权限", false);
                                XXPermissions.gotoPermissionSettings(getActivity());
                                return;
                            }

                            UserInfoBean infoBean = DataManager.getInstance().getUserInfo();
                            if (infoBean == null) {
                                Tools.toast(getActivity(), "请先登录", false);
                                return;
                            }

                            joinChartRoom(Constant.ROOM_ROLETYPE_ONLOOKER, info.getRoomId());
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {

                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return m_roomList.size();
        }
    }

    /*private class JPushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String message = bundle.getString("message");
                try {
                    JSONObject object = new JSONObject(message);
                    if (object.getInt("type") == AppConstant.JPUSH_TYPE_CHAT_CREATE
                            || object.getInt("type") == AppConstant.JPUSH_TYPE_CHAT_DELETE) {
                        //更新聊天室列表
                        setOnLookerRecyclerView();
                    }
                } catch (Exception e) {
                    Log.e("JPushMessageReceiver--" + e.toString());
                }
            }
        }
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomUpdate(EventBusParam<BusChatRoomParam> param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_UPDATE_CHATROOM) {
            try {
                JSONObject object = new JSONObject(param.getData().getMessage());
                if (object.getInt("type") == AppConstant.JPUSH_TYPE_CHAT_CREATE
                        || object.getInt("type") == AppConstant.JPUSH_TYPE_CHAT_DELETE) {
                    //更新聊天室列表
                    setOnLookerRecyclerView();
                }
            } catch (Exception e) {
                Log.e("JPushMessageReceiver--" + e.toString());
            }
        }
    }
}
