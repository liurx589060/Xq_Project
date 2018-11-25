package com.cd.xq.frame;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cd.xq.R;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.chart.status.statusBeans.StatusMatchBean;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.beans.JMNormalSendBean;
import com.cd.xq.module.util.beans.jmessage.Data;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.jmessage.JMChartRoomSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.status.BaseStatus;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.utils.CheckUtil;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.UserInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/11/11.
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.btn_angel)
    Button mBtnAngel;
    @BindView(R.id.btn_guest)
    Button mBtnGuest;
    @BindView(R.id.edit_limitLady)
    EditText mEditLimitLady;
    @BindView(R.id.radioGroup_1)
    RadioGroup mRadioGroup;
    Unbinder unbinder;

    private RequestApi mApi;

    private String mTXPushAddress = "";
    private String mTXPlayerAddress = "";
    private int mPushAddressType= 0;
    private StatusMatchBean mMatch = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_home, null);
        unbinder = ButterKnife.bind(this, mRootView);

        init();
        return mRootView;
    }

    private void init() {
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mMatch = new StatusMatchBean();

        mBtnAngel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if(!isAll) {
                            Tools.toast(getActivity(),"请同意权限",false);
                            XXPermissions.gotoPermissionSettings(getActivity());
                            return;
                        }

                        UserInfoBean infoBean = DataManager.getInstance().getUserInfo();
                        if(infoBean == null) {
                            Tools.toast(getActivity(),"请先登录",false);
                            return;
                        }

                        UserInfoBean bean = new UserInfoBean();
                        bean.setUser_name(DataManager.getInstance().getUserInfo().getUser_name());
                        bean.setRole_type(DataManager.getInstance().getUserInfo().getRole_type());
                        bean.setGender(DataManager.getInstance().getUserInfo().getGender());
                        bean.setLevel(DataManager.getInstance().getUserInfo().getLevel());
                        try {
                            bean.setLimitLady(Integer.valueOf(mEditLimitLady.getText().toString()));
                        }catch (Exception e) {
                            Log.e(e.toString());
                            bean.setLimitLady(10);
                        }
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
                                if(!isAll) {
                                    Tools.toast(getActivity(),"请同意权限",false);
                                    XXPermissions.gotoPermissionSettings(getActivity());
                                    return;
                                }

                                UserInfoBean infoBean = DataManager.getInstance().getUserInfo();
                                if(infoBean == null) {
                                    Tools.toast(getActivity(),"请先登录",false);
                                    return;
                                }

                                joinChartRoom();
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {

                            }
                        });
            }
        });

        mRadioGroup.check(R.id.radio_local);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_local) {
                    mPushAddressType = 0;
                }else if(checkedId == R.id.radio_tx) {
                    mPushAddressType = 1;
                }
            }
        });

        if(DataManager.getInstance().getUserInfo() != null) {
            if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                mRadioGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    private void createChartRoom(UserInfoBean userInfo) {
        if(userInfo == null){
            Log.e("createChartRoom UserInfoBean=null");
            return;
        }
        if(!userInfo.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            Tools.toast(getActivity(),"您不是爱心大使",true);
            return;
        }

        setLiveAddress();

        Map<String,Object> params = new HashMap<>();
        params.put("userName",userInfo.getUser_name());
        params.put("gender",userInfo.getGender());
        params.put("level",userInfo.getLevel());
        params.put("limitLevel",userInfo.getLimitLevel());
        params.put("limitLady",userInfo.getLimitLady());
        params.put("limitMan",userInfo.getLimitMan());
        params.put("limitAngel",userInfo.getLimitAngel());
        params.put("pushAddress", Base64.encodeToString(mTXPushAddress.getBytes(),Base64.DEFAULT));
        params.put("playAddress",Base64.encodeToString(mTXPlayerAddress.getBytes(),Base64.DEFAULT));

        if(userInfo.getLimitLady()%2 != 0) {
            Tools.toast(getActivity(),"请输入偶数",true);
            return;
        }

        mApi.createChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            Tools.toast(getActivity(),"jmChartResp is null",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e(jmChartResp.getMsg());
                            Tools.toast(getActivity(),jmChartResp.getMsg(),true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(getActivity(),ChartRoomActivity.class);
                        getActivity().startActivity(intent);
                    }}, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(getActivity(),throwable.toString(),true);
                    }
                });
    }

    private void joinChartRoom() {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        if(!userInfo.getRole_type().equals(Constant.ROLETYPE_GUEST)) {
            Tools.toast(getActivity(),"您不是Guest",true);
            return;
        }

        Map<String,Object> params = new HashMap<>();
        params.put("userName",userInfo.getUser_name());
        params.put("gender",userInfo.getGender());
        params.put("level",userInfo.getLevel());
        params.put("roleType",userInfo.getRole_type());
        mApi.joinChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            Tools.toast(getActivity(),"jmChartResp is null",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e(jmChartResp.getMsg());
                            Tools.toast(getActivity(),jmChartResp.getMsg(),true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(getActivity(),ChartRoomActivity.class);
                        getActivity().startActivity(intent);
                        //发送聊天室信息
                        sendChartRoomMessage(true);
                    }}, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(getActivity(),throwable.toString(),true);
                    }
                });
    }

    private void sendChartRoomMessage(boolean isUpdateMembers) {
        //发送聊天室信息
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
        JMChartRoomSendBean bean = mMatch.createBaseChartRoomSendBean();
        bean.setCurrentCount(data.getMembers().size());
        bean.setProcessStatus(mMatch.getStatus());
        bean.setMessageType(BaseStatus.MessageType.TYPE_SEND);
        bean.setUpdateMembers(isUpdateMembers);
        bean.setIndexNext(DataManager.getInstance().getSelfMember().getIndex());
        bean.setMsg(selfInfo.getNick_name() + "进入房间");

        JMsgSender.sendRoomMessage(bean);
    }

    /*
	 * KEY+ stream_id + txTime
	 */
    private void setLiveAddress() {
        if(mPushAddressType == 0) {
            //本地
            mTXPushAddress = NetWorkMg.getCameraUrl();
            mTXPlayerAddress = mTXPushAddress;
        }else if(mPushAddressType == 1){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR,24);
            long txTime = calendar.getTimeInMillis()/1000;
            String input = new StringBuilder().
                    append(Constant.TX_LIVE_PUSH_KEY).
                    append(Constant.TX_LIVE_BIZID + "_"
                            + String.valueOf(DataManager.getInstance().getChartData().getRoomId())).
                    append(Long.toHexString(txTime).toUpperCase()).toString();
            Log.d("yy",input);

            String txSecret = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                txSecret  = byteArrayToHexString(
                        messageDigest.digest(input.getBytes("UTF-8")));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e(e.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(e.toString());
            }

            mTXPlayerAddress = "rtmp://" + Constant.TX_LIVE_BIZID + ".liveplay.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getChartData().getRoomId();

            String ip = "rtmp://" + Constant.TX_LIVE_BIZID + ".livepush.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getChartData().getRoomId()
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
        Log.i("yy","TXPlayerAddress=" + mTXPlayerAddress);
        Log.i("yy","TXPushAddress=" + mTXPushAddress);
    }

    private String byteArrayToHexString(byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
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
    }

    @Override
    public void onLogin() {
        super.onLogin();
        if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            mRadioGroup.setVisibility(View.VISIBLE);
        }else {
            mRadioGroup.setVisibility(View.GONE);
        }
    }

    private void requestPermission(OnPermission onPermission) {
        if(onPermission == null) {
            return;
        }
        XXPermissions.with(getActivity())
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.CAMERA,Permission.RECORD_AUDIO)
                .request(onPermission);
    }
}
