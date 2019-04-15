package com.cd.xq.frame;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.beans.BCheckRoomExpiry;
import com.cd.xq.beans.BGetArrays;
import com.cd.xq.beans.BGetBanner;
import com.cd.xq.beans.BusChatRoomParam;
import com.cd.xq.friend.FriendActivity;
import com.cd.xq.login.BlackCheckListener;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.chart.beans.BConsumeGift;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.chart.status.statusBeans.StatusMatchBean;
import com.cd.xq.module.chart.status.statusBeans.StatusOnLookerEnterBean;
import com.cd.xq.module.chart.utils.ChatTools;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.common.MultiItemDivider;
import com.cd.xq.module.util.interfaces.ICheckBlackListener;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.my.MyGiftBuyActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

    private RequestApi mApi;
    private XqRequestApi mXqApi;
    private ChatRequestApi mChatApi;

    private ArrayList<BGetArrays> m_roomList;
    private OnLookerRecyclerViewAdapter mOnLookerAdapter;
    private ArrayList<String> mImageList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_home, null);
        unbinder = ButterKnife.bind(this, mRootView);
        EventBus.getDefault().register(this);
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mXqApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        mChatApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);

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

                        if(!DataManager.getInstance().getUserInfo().isOnLine()) {
                            Tools.toast(getActivity(), "请先登录...", true);
                            return;
                        }

                        if (!DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                            Tools.toast(getActivity(), "您不是爱心大使", true);
                            return;
                        }

                        Intent intent = new Intent(getActivity(),CreateRoomActivity.class);
                        getActivity().startActivity(intent);
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

                        if (!DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)) {
                            Tools.toast(getActivity(), "您不是Guest", true);
                            return;
                        }

                        if (DataManager.getInstance().getUserInfo().getMarrige() != 0) {
                            //不是未婚状态
                            Tools.toast(getActivity(), "您已婚，只能围观房间不可参与！", true);
                            return;
                        }

                        //参与者加入房间
                        toCommitJoinParticipant();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
            }
        });

        requestGetBanner();
        setOnLookerRecyclerView();
        initSmartRefreshLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 加入房间
     * @param roomRoleType
     * @param roomId
     */
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
                        getActivity().startActivity(intent);
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
                        if(bGetBannerNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("requestGetBanner--" + bGetBannerNetResult.getMsg());
                            return;
                        }
                        mImageList.clear();
                        for(int i = 0 ; i < bGetBannerNetResult.getData().size() ; i++) {
                            mImageList.add(bGetBannerNetResult.getData().get(i).getImage());
                        }
                        initXBanner();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                        Log.e("requestGetBanner--" + throwable.toString());
                    }
                });
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

    /**
     * 发送加入房间的消息
     */
    private void sendChartRoomMessage() {
        //发送聊天室信息
        Data data = DataManager.getInstance().getChartData();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onLogin() {
        super.onLogin();
        setOnLookerRecyclerView();
    }

    /**
     * 申请权限
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
        mXqApi.getNowChatRoomList(1)
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

    /**
     * EventBus事件
     * @param param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomUpdate(EventBusParam<BusChatRoomParam> param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_CHATROOM_CREATE
                || param.getEventBusCode() == EventBusParam.EVENT_BUS_CHATROOM_DELETE) {
            //更新聊天室列表
            setOnLookerRecyclerView();
        }else if(param.getEventBusCode() == EventBusParam.EVENT_BUS_CHECK_BLACKUSER) {
            //检查是否是黑名单
            Tools.checkUserOrBlack(getActivity(), DataManager.getInstance().getUserInfo().getUser_name(), new BlackCheckListener() {
                @Override
                public void onResult(boolean isBlack) {

                }
            });
        }
    }


    /*******************参与者加入房间**************************/
    private void toCommitJoinParticipant() {
        //加入房间
        mXqApi.checkRoomExpiry(DataManager.getInstance().getUserInfo().getUser_name(),2)
                .compose(this.<NetResult<BCheckRoomExpiry>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BCheckRoomExpiry>>() {
                    @Override
                    public void accept(NetResult<BCheckRoomExpiry> bCheckRoomExpiry) throws Exception {
                        if(bCheckRoomExpiry.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getActivity().getApplicationContext(), bCheckRoomExpiry.getMsg(), false);
                            Log.e("checkRoomExpiry--" + bCheckRoomExpiry.getMsg());
                            return;
                        }

                        if(bCheckRoomExpiry.getData().getExpiry() != null) {
                            //有使用中的卡,则直接创建房间
                            String str = "您有使用中的" + bCheckRoomExpiry.getData().getExpiry().getName() +
                                    ",可免费加入房间,剩余次数" + bCheckRoomExpiry.getData().getExpiry().getExpiry_num();
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),str,Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,0);
                            toast.show();

                            //加入房间
                            joinChartRoom(Constant.ROOM_ROLETYPE_PARTICIPANTS,-1);
                        }else {
                            if(bCheckRoomExpiry.getData().getHasCard() == 1) {
                                //有未使用的卡，提示是否使用卡
                                doCreateUseCardDialog(bCheckRoomExpiry.getData());
                            }else {
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
     * @param item
     * @param handleType
     */
    private void doRequestConsumeGift(final BGetGiftItem item, int handleType) {
        //用钻石消费
        if(handleType == 1 && !ChatTools.checkBalance(getActivity(),item.getCoin())) return;
        requestConsumeGift(item,handleType);
    }

    private void requestConsumeGift(final BGetGiftItem item, final int handleType) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId",item.getGift_id());
        params.put("coin",item.getCoin());
        params.put("toUser",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("handleType",handleType);  //消费方式
        mChatApi.consumeGift(params)
                .compose(this.<NetResult<BConsumeGift>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BConsumeGift>>() {
                    @Override
                    public void accept(NetResult<BConsumeGift> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            if(netResult.getStatus() == XqErrorCode.ERROR_LACK_STOCK) {
                                //余额不足
                                //更新余额
                                DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                                doRequestConsumeGift(item,handleType);
                            }else {
                                Tools.toast(getActivity().getApplicationContext(), netResult.getMsg(), false);
                            }
                            Log.e("requestConsumeGift--" + netResult.getMsg());
                            return;
                        }

                        //更新余额
                        DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                        //加入房间
                        joinChartRoom(Constant.ROOM_ROLETYPE_PARTICIPANTS,-1);
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
                        doRequestConsumeGift(checkRoomExpiry.getGift(),2);
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
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(),1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 创建是否购买建房卡
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
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(),1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
